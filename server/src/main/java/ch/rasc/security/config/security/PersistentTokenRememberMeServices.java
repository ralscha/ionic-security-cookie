package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppUser.APP_USER;
import static ch.rasc.security.db.tables.RememberMeToken.REMEMBER_ME_TOKEN;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import ch.rasc.security.Application;
import ch.rasc.security.config.AppProperties;
import ch.rasc.security.db.tables.pojos.RememberMeToken;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p>
 * This is a specific implementation of Spring Security's remember-me authentication, but
 * it is much more powerful than the standard implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and invalidate
 * them</li>
 * <li>It stores more information, such as the IP address and the user agent, for audit
 * purposes
 * <li>
 * <li>When a user logs out, only his current session is invalidated, and not all of his
 * sessions</li>
 * </ul>
 * <p>
 * Please note that it allows the use of the same token for 5 seconds, and this value
 * stored in a specific cache during that period. This is to allow concurrent requests
 * from the same user: otherwise, two requests being sent at the same time could
 * invalidate each other's token.
 * <p>
 * This is inspired by:
 * <ul>
 * <li><a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved
 * Persistent Login Cookie Best Practice</a></li>
 * <li><a href="https://github.com/blog/1661-modeling-your-app-s-user-session">GitHub's
 * "Modeling your App's User Session"</a></li>
 * </ul>
 * <p>
 * The main algorithm comes from Spring Security's PersistentTokenBasedRememberMeServices,
 * but this class couldn't be cleanly extended.
 */
@Service
public class PersistentTokenRememberMeServices extends AbstractRememberMeServices {

  private static final int UPGRADED_TOKEN_VALIDITY_SECONDS = 5;

  private final Cache<String, UpgradedRememberMeToken> upgradedTokenCache = Caffeine
      .newBuilder().expireAfterWrite(UPGRADED_TOKEN_VALIDITY_SECONDS, TimeUnit.SECONDS)
      .build();

  private final int tokenValidInDays;

  private final int tokenValidInSeconds;

  private final DSLContext dsl;

  public PersistentTokenRememberMeServices(AppProperties appProperties,
      UserDetailsService userDetailsService, DSLContext dsl) {
    super(appProperties.getRemembermeCookieKey(), userDetailsService);
    this.tokenValidInDays = appProperties.getRemembermeCookieValidInDays();
    this.tokenValidInSeconds = 60 * 60 * 24
        * appProperties.getRemembermeCookieValidInDays();
    this.dsl = dsl;
  }

  @Override
  protected UserDetails processAutoLoginCookie(String[] cookieTokens,
      HttpServletRequest request, HttpServletResponse response) {

    synchronized (this) { // prevent 2 authentication requests from the same user in
      // parallel
      String login = null;
      UpgradedRememberMeToken upgradedToken = this.upgradedTokenCache
          .getIfPresent(cookieTokens[0]);
      if (upgradedToken != null) {
        login = upgradedToken.getUserLoginIfValidAndRecentUpgrade(cookieTokens);
        Application.logger.debug("Detected previously upgraded login token for user '{}'",
            login);
      }

      if (login == null) {
        RememberMeToken token = getPersistentToken(cookieTokens);
        login = token.getUsername();

        // Token also matches, so login is valid. Update the token value, keeping
        // the
        // *same* series number.
        Application.logger.debug(
            "Refreshing persistent login token for user '{}', series '{}'", login,
            token.getSeries());

        this.dsl.update(REMEMBER_ME_TOKEN)
            .set(REMEMBER_ME_TOKEN.TOKEN_DATE, LocalDateTime.now(ZoneOffset.UTC))
            .set(REMEMBER_ME_TOKEN.TOKEN_VALUE, UUID.randomUUID().toString())
            .set(REMEMBER_ME_TOKEN.IP_ADDRESS, request.getRemoteAddr())
            .set(REMEMBER_ME_TOKEN.USER_AGENT, request.getHeader("User-Agent"))
            .where(REMEMBER_ME_TOKEN.ID.equal(token.getId())).execute();

        addCookie(token, request, response);
        this.upgradedTokenCache.put(cookieTokens[0],
            new UpgradedRememberMeToken(cookieTokens, login));
      }
      return getUserDetailsService().loadUserByUsername(login);
    }
  }

  @Override
  protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication successfulAuthentication) {

    String username = successfulAuthentication.getName();

    boolean userExists = this.dsl.fetchExists(
        DSL.select().from(APP_USER).where(APP_USER.USER_NAME.equal(username)));

    Application.logger.debug("Creating new persistent login for user {}", username);

    if (userExists) {
      RememberMeToken token = new RememberMeToken();
      token.setSeries(UUID.randomUUID().toString());
      token.setUsername(username);
      token.setTokenValue(UUID.randomUUID().toString());
      token.setTokenDate(LocalDateTime.now(ZoneOffset.UTC));
      token.setIpAddress(request.getRemoteAddr());
      token.setUserAgent(request.getHeader("User-Agent"));

      this.dsl.insertInto(REMEMBER_ME_TOKEN)
          .columns(REMEMBER_ME_TOKEN.USERNAME, REMEMBER_ME_TOKEN.SERIES,
              REMEMBER_ME_TOKEN.TOKEN_DATE, REMEMBER_ME_TOKEN.TOKEN_VALUE,
              REMEMBER_ME_TOKEN.IP_ADDRESS, REMEMBER_ME_TOKEN.USER_AGENT)
          .values(token.getUsername(), token.getSeries(), token.getTokenDate(),
              token.getTokenValue(), token.getIpAddress(), token.getUserAgent())
          .execute();

      addCookie(token, request, response);
    }
    else {
      throw new UsernameNotFoundException(
          "User " + username + " was not found in the database");
    }
  }

  /**
   * When logout occurs, only invalidate the current token, and not all user sessions.
   * <p>
   * The standard Spring Security implementations are too basic: they invalidate all
   * tokens for the current user, so when he logs out from one browser, all his other
   * sessions are destroyed.
   */
  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) {
    String rememberMeCookie = extractRememberMeCookie(request);
    if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
      try {
        String[] cookieTokens = decodeCookie(rememberMeCookie);
        RememberMeToken token = getPersistentToken(cookieTokens);

        this.dsl.delete(REMEMBER_ME_TOKEN)
            .where(REMEMBER_ME_TOKEN.ID.equal(token.getId())).execute();

      }
      catch (InvalidCookieException ice) {
        Application.logger.info("Invalid cookie, no persistent token could be deleted",
            ice);
      }
      catch (RememberMeAuthenticationException rmae) {
        Application.logger
            .debug("No persistent token found, so no token could be deleted", rmae);
      }
    }
    super.logout(request, response, authentication);
  }

  /**
   * Validate the token and return it.
   */
  private RememberMeToken getPersistentToken(String[] cookieTokens) {
    if (cookieTokens.length != 2) {
      throw new InvalidCookieException("Cookie token did not contain " + 2
          + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
    }
    String presentedSeries = cookieTokens[0];
    String presentedToken = cookieTokens[1];

    var result = this.dsl.selectFrom(REMEMBER_ME_TOKEN)
        .where(REMEMBER_ME_TOKEN.SERIES.equal(presentedSeries)).fetchOne();

    RememberMeToken token = null;
    if (result != null) {
      token = result.into(RememberMeToken.class);
    }

    if (token == null) {
      // No series match, so we can't authenticate using this cookie
      throw new RememberMeAuthenticationException(
          "No persistent token found for series id: " + presentedSeries);
    }

    // We have a match for this user/series combination
    Application.logger.info("presentedToken={} / tokenValue={}", presentedToken,
        token.getTokenValue());
    if (!presentedToken.equals(token.getTokenValue())) {
      // Token doesn't match series value. Delete this session and throw an
      // exception.
      this.dsl.delete(REMEMBER_ME_TOKEN).where(REMEMBER_ME_TOKEN.ID.equal(token.getId()))
          .execute();

      throw new CookieTheftException(
          "Invalid remember-me token (Series/token) mismatch. Implies previous "
              + "cookie theft attack.");
    }

    if (token.getTokenDate().plus(this.tokenValidInDays, ChronoUnit.DAYS)
        .isBefore(LocalDateTime.now(ZoneOffset.UTC))) {

      this.dsl.delete(REMEMBER_ME_TOKEN).where(REMEMBER_ME_TOKEN.ID.equal(token.getId()))
          .execute();

      throw new RememberMeAuthenticationException("Remember-me login has expired");
    }
    return token;
  }

  private void addCookie(RememberMeToken token, HttpServletRequest request,
      HttpServletResponse response) {
    setCookie(new String[] { token.getSeries(), token.getTokenValue() },
        this.tokenValidInSeconds, request, response);
  }

  private static class UpgradedRememberMeToken implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String[] upgradedToken;

    private final Date upgradeTime;

    private final String userLogin;

    UpgradedRememberMeToken(String[] upgradedToken, String userLogin) {
      this.upgradedToken = upgradedToken;
      this.userLogin = userLogin;
      this.upgradeTime = new Date();
    }

    String getUserLoginIfValidAndRecentUpgrade(String[] currentToken) {
      if (currentToken[0].equals(this.upgradedToken[0])
          && currentToken[1].equals(this.upgradedToken[1]) && this.upgradeTime.getTime()
              + UPGRADED_TOKEN_VALIDITY_SECONDS * 1000 > new Date().getTime()) {
        return this.userLogin;
      }
      return null;
    }
  }
}

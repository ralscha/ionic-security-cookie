package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppUser.APP_USER;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jooq.DSLContext;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import ch.rasc.security.config.AppProperties;

@Component
public class UserAuthenticationErrorHandler
    implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

  private final DSLContext dsl;

  private final AppProperties appProperties;

  UserAuthenticationErrorHandler(DSLContext dsl, AppProperties appProperties) {
    this.dsl = dsl;
    this.appProperties = appProperties;
  }

  @Override
  public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    if (this.appProperties.getLoginLockAttempts() != null
        && principal instanceof String) {
      String username = (String) principal;

      var result = this.dsl.select(APP_USER.ID, APP_USER.FAILED_LOGINS).from(APP_USER)
          .where(APP_USER.USER_NAME.eq(username)).fetchOne();

      if (result != null) {
        Long userId = result.get(APP_USER.ID);

        Integer failedLogins = result.get(APP_USER.FAILED_LOGINS);
        if (failedLogins == null) {
          failedLogins = 1;
        }
        else {
          failedLogins++;
        }

        LocalDateTime lockedOut = null;
        if (failedLogins >= this.appProperties.getLoginLockAttempts()) {
          if (this.appProperties.getLoginLockMinutes() != null) {
            lockedOut = LocalDateTime.now(ZoneOffset.UTC)
                .plusMinutes(this.appProperties.getLoginLockMinutes());
          }
          else {
            lockedOut = LocalDateTime.now(ZoneOffset.UTC).plusYears(1000);
          }
        }

        this.dsl.update(APP_USER).set(APP_USER.LOCKED_OUT_UNTIL, lockedOut)
            .set(APP_USER.FAILED_LOGINS, failedLogins).where(APP_USER.ID.eq(userId))
            .execute();
      }
      else {
        LoggerFactory.getLogger(UserAuthenticationErrorHandler.class)
            .error("Unknown user login attempt: {}", principal);
      }

    }
  }
}
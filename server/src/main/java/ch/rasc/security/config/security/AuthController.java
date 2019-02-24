package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppRole.APP_ROLE;
import static ch.rasc.security.db.tables.AppUser.APP_USER;
import static ch.rasc.security.db.tables.AppUserRoles.APP_USER_ROLES;
import static ch.rasc.security.db.tables.RememberMeToken.REMEMBER_ME_TOKEN;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.security.config.MailService;
import ch.rasc.security.db.tables.pojos.RememberMeToken;
import ch.rasc.security.db.tables.records.AppUserRecord;

@RestController
public class AuthController {

  private final DSLContext dsl;

  private final PasswordEncoder passwordEncoder;

  private final MailService mailService;

  public AuthController(PasswordEncoder passwordEncoder, DSLContext dsl,
      MailService mailService) {
    this.dsl = dsl;
    this.passwordEncoder = passwordEncoder;
    this.mailService = mailService;
  }

  @GetMapping("/authenticate")
  public String authenticate(@AuthenticationPrincipal UserDetails user) {
    return user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }

  @PostMapping("/signup")
  public String signup(@RequestBody UserDto signupUser) {
    int count = this.dsl.selectCount().from(APP_USER)
        .where(APP_USER.USER_NAME.equalIgnoreCase(signupUser.getUserName()))
        .fetchOne(0, int.class);
    if (count > 0) {
      return "EXISTS";
    }

    this.dsl.transaction(txConf -> {
      try (var txdsl = DSL.using(txConf)) {

        var result = txdsl
            .insertInto(APP_USER, APP_USER.FIRST_NAME, APP_USER.LAST_NAME,
                APP_USER.USER_NAME, APP_USER.EMAIL, APP_USER.PASSWORD_HASH,
                APP_USER.ENABLED, APP_USER.LAST_ACCESS)
            .values(signupUser.getFirstName(), signupUser.getLastName(),
                signupUser.getUserName(), signupUser.getEmail(),
                this.passwordEncoder.encode(signupUser.getPassword()), true,
                LocalDateTime.now(ZoneOffset.UTC))
            .returning(APP_USER.ID).fetchOne();

        long id = result.get(APP_USER.ID);

        long roleId = txdsl.select(APP_ROLE.ID).from(APP_ROLE)
            .where(APP_ROLE.NAME.eq("USER")).limit(1).fetchOne(APP_ROLE.ID);

        txdsl.insertInto(APP_USER_ROLES, APP_USER_ROLES.APP_USER_ID,
            APP_USER_ROLES.APP_ROLE_ID).values(id, roleId).execute();
      }
    });

    return null;
  }

  @PostMapping("/reset")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void passwordRequest(@RequestBody String usernameOrEmail) {

    var record = this.dsl.select(APP_USER.ID, APP_USER.EMAIL, APP_USER.USER_NAME)
        .from(APP_USER).where(APP_USER.USER_NAME.equalIgnoreCase(usernameOrEmail)
            .or(APP_USER.EMAIL.equalIgnoreCase(usernameOrEmail)))
        .limit(1).fetchOne();

    if (record != null) {
      long userId = record.get(APP_USER.ID);

      String token = Base64.getUrlEncoder()
          .encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

      this.dsl.update(APP_USER).set(APP_USER.PASSWORD_RESET_TOKEN, token)
          .set(APP_USER.PASSWORD_RESET_TOKEN_VALID_UNTIL,
              LocalDateTime.now(ZoneOffset.UTC).plusHours(4))
          .where(APP_USER.ID.equal(userId)).execute();

      this.mailService.sendPasswordResetEmail(record.get(APP_USER.EMAIL),
          record.get(APP_USER.USER_NAME), token);
    }

  }

  @PostMapping("/change")
  public boolean passwordChange(@RequestParam("token") String token,
      @RequestParam("password") String password) {

    var record = this.dsl.select(APP_USER.ID, APP_USER.PASSWORD_RESET_TOKEN_VALID_UNTIL)
        .from(APP_USER).where(APP_USER.PASSWORD_RESET_TOKEN.equal(token)).fetchOne();

    if (record != null) {
      long userId = record.get(APP_USER.ID);
      LocalDateTime validUntil = record.get(APP_USER.PASSWORD_RESET_TOKEN_VALID_UNTIL);

      if (validUntil != null && validUntil.isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
        this.dsl.update(APP_USER).set(APP_USER.PASSWORD_RESET_TOKEN, (String) null)
            .set(APP_USER.PASSWORD_RESET_TOKEN_VALID_UNTIL, (LocalDateTime) null)
            .set(APP_USER.PASSWORD_HASH, this.passwordEncoder.encode(password))
            .where(APP_USER.ID.equal(userId)).execute();
        return true;
      }

      this.dsl.update(APP_USER).set(APP_USER.PASSWORD_RESET_TOKEN, (String) null)
          .set(APP_USER.PASSWORD_RESET_TOKEN_VALID_UNTIL, (LocalDateTime) null)
          .where(APP_USER.ID.equal(userId)).execute();
    }

    return false;

  }

  @GetMapping("/profile")
  @RequireAuthenticated
  public UserDto getProfile(@AuthenticationPrincipal JooqUserDetails user) {
    var record = this.dsl.selectFrom(APP_USER).where(APP_USER.ID.eq(user.getUserDbId()))
        .fetchOne();

    return new UserDto(record.get(APP_USER.FIRST_NAME), record.get(APP_USER.LAST_NAME),
        record.get(APP_USER.USER_NAME), record.get(APP_USER.EMAIL));
  }

  @PostMapping("/updateProfile")
  @RequireAuthenticated
  public void updateProfile(@AuthenticationPrincipal JooqUserDetails userDetail,
      @RequestBody UserDto modifiedUser) {

    var record = this.dsl.select(APP_USER.USER_NAME, APP_USER.PASSWORD_HASH)
        .from(APP_USER).where(APP_USER.ID.eq(userDetail.getUserDbId())).fetchOne();

    if (record != null) {

      this.dsl.transaction(txConf -> {
        try (var txdsl = DSL.using(txConf)) {

          try (UpdateSetMoreStep<AppUserRecord> updateStmt = txdsl.update(APP_USER)
              .set(APP_USER.FIRST_NAME, modifiedUser.getFirstName())
              .set(APP_USER.LAST_NAME, modifiedUser.getLastName())
              .set(APP_USER.EMAIL, modifiedUser.getEmail())) {

            String password = record.get(APP_USER.PASSWORD_HASH);
            if (StringUtils.hasText(modifiedUser.getPassword())
                && StringUtils.hasText(modifiedUser.getOldPassword())
                && this.passwordEncoder.matches(modifiedUser.getOldPassword(),
                    password)) {
              updateStmt.set(APP_USER.PASSWORD_HASH,
                  this.passwordEncoder.encode(modifiedUser.getPassword()));
            }

            boolean usernameChanged = false;
            String username = record.get(APP_USER.USER_NAME);
            if (!username.equals(modifiedUser.getUserName())) {
              updateStmt.set(APP_USER.USER_NAME, modifiedUser.getUserName());
              usernameChanged = true;
            }

            updateStmt.where(APP_USER.ID.equal(userDetail.getUserDbId())).execute();

            if (usernameChanged) {
              txdsl.delete(REMEMBER_ME_TOKEN)
                  .where(REMEMBER_ME_TOKEN.USERNAME.equal(username)).execute();
            }

          }
        }
      });
    }

  }

  @GetMapping("/rememberMeTokens")
  @RequireAuthenticated
  public List<RememberMeToken> fetchTokens(
      @AuthenticationPrincipal JooqUserDetails userDetail) {
    return this.dsl.selectFrom(REMEMBER_ME_TOKEN)
        .where(REMEMBER_ME_TOKEN.USERNAME.equal(userDetail.getUsername()))
        .fetchInto(RememberMeToken.class);
  }

  @PostMapping("/deleteRememberMeTokens")
  @RequireAuthenticated
  public void deleteRememberMeTokens(@AuthenticationPrincipal JooqUserDetails userDetail,
      @RequestBody String series) {
    this.dsl.delete(REMEMBER_ME_TOKEN).where(REMEMBER_ME_TOKEN.SERIES.equal(series)
        .and(REMEMBER_ME_TOKEN.USERNAME.equal(userDetail.getUsername()))).execute();
  }
}

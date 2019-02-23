package ch.rasc.security.config.security;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import ch.rasc.security.db.RememberMeToken;
import ch.rasc.security.db.User;
import ch.rasc.security.db.XodusManager;
import ch.rasc.security.db.tables.pojos.AppUser;
import static ch.rasc.security.db.tables.AppUser.APP_USER;

@RestController
public class AuthController {

  private final DSLContext dsl;

  private final PasswordEncoder passwordEncoder;

  private final MailService mailService;

  public AuthController(PasswordEncoder passwordEncoder, 
      DSLContext dsl,
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
  public String signup(@RequestBody SignupUser signupUser) {
    int count = this.dsl.selectCount().from(APP_USER)
                          .where(APP_USER.USER_NAME.eq(signupUser.getUserName()))
                          .fetchOne(0, int.class);
    if (count > 0) {
      return "EXISTS";
    }

    signupUser.setEnabled(true);
    signupUser.setLastAccess(Instant.now().getEpochSecond());
    signupUser.setAuthorities(Collections.singletonList(Authority.USER));
    signupUser.setPassword(this.passwordEncoder.encode(signupUser.getPassword()));
    this.xodusManager.persistUser(signupUser);
    return null;
  }

  @PostMapping("/reset")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void passwordRequest(@RequestBody String usernameOrEmail) {
    SignupUser user = this.xodusManager.generatePasswordResetToken(usernameOrEmail);
    if (user != null) {
      this.mailService.sendPasswordResetEmail(user);
    }
  }

  @PostMapping("/change")
  public boolean passwordChange(@RequestParam("token") String token,
      @RequestParam("password") String password) {
    return this.xodusManager.changePassword(token, this.passwordEncoder.encode(password));
  }

  @GetMapping("/profile")
  @RequireAuthenticated
  public SignupUser getProfile(@AuthenticationPrincipal UserDetails user) {
    return this.xodusManager.fetchUser(user.getUsername());
  }

  @PostMapping("/updateProfile")
  @RequireAuthenticated
  public void updateProfile(@AuthenticationPrincipal UserDetails userDetail,
      @RequestBody SignupUser modifiedUser) {
    SignupUser user = this.xodusManager.fetchUser(userDetail.getUsername());
    if (user != null) {
      user.setFirstName(modifiedUser.getFirstName());
      user.setLastName(modifiedUser.getLastName());

      if (StringUtils.hasText(modifiedUser.getOldPassword()) && this.passwordEncoder
          .matches(modifiedUser.getOldPassword(), user.getPassword())) {

        user.setEmail(modifiedUser.getEmail());
        if (StringUtils.hasText(modifiedUser.getPassword())) {
          user.setPassword(this.passwordEncoder.encode(modifiedUser.getPassword()));
        }

      }
      this.xodusManager.persistUser(user);
    }
  }

  @GetMapping("/rememberMeTokens")
  @RequireAuthenticated
  public List<ch.rasc.security.db.tables.pojos.RememberMeToken> fetchTokens(@AuthenticationPrincipal UserDetails user) {
    return this.xodusManager.fetchTokens(user.getUsername());
  }

  @PostMapping("/deleteRememberMeTokens")
  @RequireAuthenticated
  public void deleteRememberMeTokens(@AuthenticationPrincipal UserDetails userDetail,
      @RequestBody String series) {
    this.xodusManager.deleteToken(userDetail.getUsername(), series);
  }
}

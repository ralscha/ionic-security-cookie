package ch.rasc.security.config.security;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.security.config.MailService;
import ch.rasc.security.db.User;
import ch.rasc.security.db.XodusManager;

@RestController
public class AuthController {

  private final XodusManager xodusManager;

  private final PasswordEncoder passwordEncoder;

  private final MailService mailService;

  public AuthController(PasswordEncoder passwordEncoder, XodusManager xodusManager,
      MailService mailService) {
    this.xodusManager = xodusManager;
    this.passwordEncoder = passwordEncoder;
    this.mailService = mailService;
  }

  @EventListener
  public void applicationReady(@SuppressWarnings("unused") ApplicationReadyEvent event) {
    if (!this.xodusManager.hasUsers()) {
      User user = new User();
      user.setUsername("admin");
      user.setPassword(this.passwordEncoder.encode("admin"));
      user.setFirstName("admin");
      user.setLastName("admin");
      user.setAuthorities(Collections.singletonList(Authority.ADMIN));
      user.setEmail("test@test.com");
      user.setEnabled(true);
      user.setLastAccess(Instant.now().getEpochSecond());
      this.xodusManager.persistUser(user);
    }
    this.xodusManager.printAllUsers();
  }

  @GetMapping("/authenticate")
  public String authenticate(@AuthenticationPrincipal UserDetails user) {
    return user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }

  @PostMapping("/signup")
  public String signup(@RequestBody User signupUser) {
    if (this.xodusManager.userExists(signupUser.getUsername())) {
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
  public boolean passwordRequest(@RequestBody String usernameOrEmail) {
    User user = this.xodusManager.generatePasswordResetToken(usernameOrEmail);
    if (user != null) {
      this.mailService.sendPasswordResetEmail(user);
    }
    return user != null;
  }

  @PostMapping("/change")
  public boolean passwordChange(@RequestParam("token") String token,
      @RequestParam("password") String password) {
    return this.xodusManager.changePassword(token, this.passwordEncoder.encode(password));
  }

  @GetMapping("/profile")
  @RequireAuthenticated
  public User getProfile(@AuthenticationPrincipal UserDetails user) {
    return this.xodusManager.fetchUser(user.getUsername());
  }

  @PostMapping("/updateProfile")
  @RequireAuthenticated
  public void updateProfile(@AuthenticationPrincipal UserDetails userDetail,
      @RequestBody User modifiedUser) {
    User user = this.xodusManager.fetchUser(userDetail.getUsername());
    if (user != null) {
      user.setFirstName(modifiedUser.getFirstName());
      user.setLastName(modifiedUser.getLastName());
      user.setEmail(modifiedUser.getEmail());

      if (StringUtils.hasText(modifiedUser.getOldPassword())
          && StringUtils.hasText(modifiedUser.getPassword())) {
        if (this.passwordEncoder.matches(modifiedUser.getOldPassword(),
            user.getPassword())) {
          user.setPassword(this.passwordEncoder.encode(modifiedUser.getPassword()));
        }
      }
      this.xodusManager.persistUser(user);
    }
  }
}

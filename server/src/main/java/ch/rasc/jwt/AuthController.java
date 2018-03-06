package ch.rasc.jwt;

import java.time.Instant;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.jwt.db.User;
import ch.rasc.jwt.db.XodusManager;

@RestController
public class AuthController {

  private final XodusManager xodusManager;

  private final PasswordEncoder passwordEncoder;

  public AuthController(PasswordEncoder passwordEncoder, XodusManager xodusManager) {
    this.xodusManager = xodusManager;
    this.passwordEncoder = passwordEncoder;
  }

  @EventListener
  public void startupReady(@SuppressWarnings("unused") ApplicationReadyEvent event) {
    if (!this.xodusManager.hasUsers()) {
      User user = new User();
      user.setUsername("admin");
      user.setPassword(this.passwordEncoder.encode("admin"));
      user.setName("admin");
      user.setAuthorities("ADMIN");
      user.setEmail("test@test.com");
      user.setEnabled(true);
      user.setLastAccess(Instant.now().getEpochSecond());
      this.xodusManager.persistUser(user);
    }
  }

  @GetMapping("/authenticate")
  public String authenticate(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
  }

  @PostMapping("/signup")
  public String signup(@RequestBody User signupUser) {
    if (this.xodusManager.userExists(signupUser.getUsername())) {
      return "EXISTS";
    }

    signupUser.setPassword(this.passwordEncoder.encode(signupUser.getPassword()));
    this.xodusManager.persistUser(signupUser);
    return null;
  }

}

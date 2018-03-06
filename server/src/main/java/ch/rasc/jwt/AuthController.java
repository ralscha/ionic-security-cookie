package ch.rasc.jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.jwt.db.User;
import ch.rasc.jwt.db.UserService;

@RestController
public class AuthController {

  private final UserService userService;

  private final PasswordEncoder passwordEncoder;

  public AuthController(PasswordEncoder passwordEncoder, UserService userService) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;

    User user = new User();
    user.setUsername("admin");
    user.setPassword(this.passwordEncoder.encode("admin"));
    this.userService.save(user);
  }

  @GetMapping("/authenticate")
  public String authenticate(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
  }

  @PostMapping("/signup")
  public String signup(@RequestBody User signupUser) {
    if (this.userService.usernameExists(signupUser.getUsername())) {
      return "EXISTS";
    }

    signupUser.encodePassword(this.passwordEncoder);
    this.userService.save(signupUser);
    return null;
  }

}

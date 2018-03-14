package ch.rasc.security;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.security.config.security.RequireAuthenticated;

@RestController
public class TestController {

  @GetMapping("/public")
  public String publicService() {
    return "This message is public";
  }

  @RequireAuthenticated
  @GetMapping("/secret")
  public String secretService() {
    return "A secret message";
  }

}

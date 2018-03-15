package ch.rasc.security.config.security;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.security.db.User;
import ch.rasc.security.db.XodusManager;

@RestController
@RequireAdminAuthority
@RequestMapping("/admin")
public class AdminController {

  private final XodusManager xodusManager;

  public AdminController(XodusManager xodusManager) {
    this.xodusManager = xodusManager;
  }

  @GetMapping("/users")
  public List<User> fetchUsers() {
    return this.xodusManager.fetchAllUsers();
  }
  
  @PostMapping("/unlock")
  public void unlock(@RequestBody String username) {
    this.xodusManager.resetLockedProperties(username, false);
  }

}

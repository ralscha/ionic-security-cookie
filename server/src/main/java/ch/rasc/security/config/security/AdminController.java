package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppUser.APP_USER;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.rasc.security.db.tables.daos.AppUserDao;
import ch.rasc.security.db.tables.pojos.AppUser;

@RestController
@RequireAdminAuthority
@RequestMapping("/admin")
public class AdminController {

  private final AppUserDao appUserDao;

  private final DSLContext dsl;

  public AdminController(DSLContext dsl, Configuration jooqConfiguration) {
    this.dsl = dsl;
    this.appUserDao = new AppUserDao(jooqConfiguration);
  }

  @GetMapping("/users")
  public List<AppUser> fetchUsers() {
    return this.appUserDao.findAll();
  }

  @PostMapping("/unlock")
  public void unlock(@RequestBody String username) {
    this.dsl.update(APP_USER).set(APP_USER.FAILED_LOGINS, (Integer) null)
        .set(APP_USER.LOCKED_OUT_UNTIL, (LocalDateTime) null)
        .where(APP_USER.USER_NAME.eq(username)).execute();
  }

}

package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppRole.APP_ROLE;
import static ch.rasc.security.db.tables.AppUser.APP_USER;
import static ch.rasc.security.db.tables.AppUserRoles.APP_USER_ROLES;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  public List<UserDto> fetchUsers() {
    List<AppUser> appUsers = this.appUserDao.findAll();

    Map<Long, String> roleNameToId = this.dsl.selectFrom(APP_ROLE).fetchMap(APP_ROLE.ID,
        APP_ROLE.NAME);
    Map<Long, List<Long>> userRoles = this.dsl.selectFrom(APP_USER_ROLES)
        .fetchGroups(APP_USER_ROLES.APP_USER_ID, APP_USER_ROLES.APP_ROLE_ID);

    return appUsers.stream().map(au -> {

      String authorities = null;
      List<Long> roleIds = userRoles.get(au.getId());
      if (roleIds != null) {
        authorities = roleIds.stream().map(id -> roleNameToId.get(id))
            .collect(Collectors.joining(", "));
      }

      return new UserDto(au.getFirstName(), au.getLastName(), au.getUserName(),
          au.getEmail(), au.getLockedOutUntil() != null, au.getLastAccess(), authorities);
    }).collect(Collectors.toList());
  }

  @PostMapping("/unlock")
  public void unlock(@RequestBody String username) {
    this.dsl.update(APP_USER).set(APP_USER.FAILED_LOGINS, (Integer) null)
        .set(APP_USER.LOCKED_OUT_UNTIL, (LocalDateTime) null)
        .where(APP_USER.USER_NAME.eq(username)).execute();
  }

}

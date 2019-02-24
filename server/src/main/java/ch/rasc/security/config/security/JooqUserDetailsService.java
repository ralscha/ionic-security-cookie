package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppRole.APP_ROLE;
import static ch.rasc.security.db.tables.AppUser.APP_USER;
import static ch.rasc.security.db.tables.AppUserRoles.APP_USER_ROLES;

import java.util.List;

import org.jooq.DSLContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ch.rasc.security.db.tables.pojos.AppUser;
import ch.rasc.security.db.tables.records.AppUserRecord;

@Component
public class JooqUserDetailsService implements UserDetailsService {

  private final DSLContext dsl;

  public JooqUserDetailsService(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {

    AppUserRecord appUserRecord = this.dsl.selectFrom(APP_USER)
        .where(APP_USER.USER_NAME.eq(username)).limit(1).fetchOne();

    if (appUserRecord != null) {
      AppUser appUser = appUserRecord.into(AppUser.class);

      List<String> roles = this.dsl.select(APP_ROLE.NAME).from(APP_ROLE)
          .join(APP_USER_ROLES).on(APP_ROLE.ID.eq(APP_USER_ROLES.APP_ROLE_ID))
          .where(APP_USER_ROLES.APP_USER_ID.eq(appUser.getId())).fetch(APP_ROLE.NAME);

      return new JooqUserDetails(appUser, roles);
    }
    throw new UsernameNotFoundException(username);
  }

}

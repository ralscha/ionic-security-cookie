package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppUser.APP_USER;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jooq.DSLContext;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationSuccessfulHandler
    implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

  private final DSLContext dsl;

  UserAuthenticationSuccessfulHandler(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    if (principal instanceof JooqUserDetails) {

      Long id = ((JooqUserDetails) principal).getUserDbId();

      this.dsl.update(APP_USER).set(APP_USER.LOCKED_OUT_UNTIL, (LocalDateTime) null)
          .set(APP_USER.FAILED_LOGINS, (Integer) null)
          .set(APP_USER.LAST_ACCESS, LocalDateTime.now(ZoneOffset.UTC))
          .where(APP_USER.ID.eq(id)).execute();
    }
  }
}
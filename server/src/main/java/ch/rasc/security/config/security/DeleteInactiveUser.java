package ch.rasc.security.config.security;

import static ch.rasc.security.db.tables.AppUser.APP_USER;
import static ch.rasc.security.db.tables.RememberMeToken.REMEMBER_ME_TOKEN;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeleteInactiveUser {

  private final DSLContext dsl;

  public DeleteInactiveUser(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Scheduled(cron = "0 0 5 * * *")
  public void doCleanup() {
    // Delete user that are inactive for over a year

    LocalDateTime oneYearAgo = LocalDateTime.now(ZoneOffset.UTC).minusYears(1);

    var results = this.dsl.select(APP_USER.ID, APP_USER.USER_NAME).from(APP_USER)
        .where(APP_USER.LAST_ACCESS.le(oneYearAgo)).fetch();

    if (results.isNotEmpty()) {
      this.dsl.transaction(txConf -> {
        try (var txdsl = DSL.using(txConf)) {
          for (var result : results) {
            Long id = result.get(APP_USER.ID);
            String username = result.get(APP_USER.USER_NAME);

            txdsl.delete(REMEMBER_ME_TOKEN).where(REMEMBER_ME_TOKEN.USERNAME.eq(username))
                .execute();
            txdsl.delete(APP_USER).where(APP_USER.ID.eq(id)).execute();
          }
        }
      });
    }
  }

}

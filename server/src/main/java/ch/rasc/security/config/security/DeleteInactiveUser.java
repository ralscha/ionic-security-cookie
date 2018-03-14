package ch.rasc.security.config.security;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.rasc.security.db.XodusManager;

@Component
public class DeleteInactiveUser {

  private final XodusManager xodusManager;

  public DeleteInactiveUser(XodusManager xodusManager) {
    this.xodusManager = xodusManager;
  }

  @Scheduled(cron = "0 0 5 * * *")
  public void doCleanup() {
    // Delete user that are inactive for over a year
    ZonedDateTime oneYearAgo = ZonedDateTime.now(ZoneOffset.UTC).minusYears(1);
    this.xodusManager.deleteInactiveUsers(oneYearAgo.toEpochSecond());
  }

}

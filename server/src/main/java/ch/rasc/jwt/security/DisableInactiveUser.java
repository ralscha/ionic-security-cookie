package ch.rasc.jwt.security;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.rasc.jwt.db.XodusManager;

@Component
public class DisableInactiveUser {

  private final XodusManager xodusManager;

  public DisableInactiveUser(XodusManager xodusManager) {
    this.xodusManager = xodusManager;
  }

  @Scheduled(cron = "0 0 5 * * *")
  public void doCleanup() {
    // Delete user that are inactive for over a year
    ZonedDateTime oneYearAgo = ZonedDateTime.now(ZoneOffset.UTC).minusYears(1);
    this.xodusManager.deleteInactiveUsers(oneYearAgo.toEpochSecond());
  }

}

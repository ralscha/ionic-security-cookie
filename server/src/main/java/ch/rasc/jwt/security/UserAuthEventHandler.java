package ch.rasc.jwt.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ch.rasc.jwt.AppConfig;
import ch.rasc.jwt.Application;
import ch.rasc.jwt.db.XodusManager;

@Component
public class UserAuthEventHandler {

  private final XodusManager xodusManager;

  private final Integer loginLockAttempts;

  private final Integer loginLockMinutes;

  public UserAuthEventHandler(XodusManager xodusManager, AppConfig appConfig) {
    this.xodusManager = xodusManager;
    this.loginLockAttempts = appConfig.getLoginLockAttempts();
    this.loginLockMinutes = appConfig.getLoginLockMinutes();
  }

  public void onAuthenticationSuccess(InteractiveAuthenticationSuccessEvent event) {
    Object principal = event.getAuthentication().getPrincipal();
    System.out.println(principal);
    if (principal instanceof UserDetails) {
      String username = ((UserDetails) principal).getUsername();
      this.xodusManager.resetLockedProperties(username);
    }
  }

  @EventListener
  public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    System.out.println(event);
    updateLockedProperties(event);
  }

  private void updateLockedProperties(AuthenticationFailureBadCredentialsEvent event) {
    Object principal = event.getAuthentication().getPrincipal();

    if (this.loginLockAttempts != null
        && (principal instanceof String || principal instanceof UserDetails)) {

      String username;
      if (principal instanceof String) {
        username = (String) principal;
      }
      else {
        username = ((UserDetails) principal).getUsername();
      }

      if (!this.xodusManager.updateLockedProperties(username, this.loginLockAttempts,
          this.loginLockMinutes)) {
        Application.logger.warn("Unknown user login attempt: {}", principal);
      }
    }
    else {
      Application.logger.warn("Invalid login attempt: {}", principal);
    }
  }

}

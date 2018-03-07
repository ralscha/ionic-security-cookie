package ch.rasc.security.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import ch.rasc.security.AppConfig;
import ch.rasc.security.Application;
import ch.rasc.security.db.XodusManager;

@Component
public class UserAuthEventHandler {

  private final XodusManager xodusManager;

  private final boolean isLoginLockEnabled;

  public UserAuthEventHandler(XodusManager xodusManager, AppConfig appConfig) {
    this.xodusManager = xodusManager;
    this.isLoginLockEnabled = appConfig.getLoginLockAttempts() != null;
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

    if (this.isLoginLockEnabled
        && (principal instanceof String || principal instanceof UserDetails)) {

      String username;
      if (principal instanceof String) {
        username = (String) principal;
      }
      else {
        username = ((UserDetails) principal).getUsername();
      }

      if (!this.xodusManager.updateLockedProperties(username)) {
        Application.logger.warn("Unknown user login attempt: {}", principal);
      }
    }
    else {
      Application.logger.warn("Invalid login attempt: {}", principal);
    }
  }

}

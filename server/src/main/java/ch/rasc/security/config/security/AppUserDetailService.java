package ch.rasc.security.config.security;

import java.time.Instant;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ch.rasc.security.db.User;
import ch.rasc.security.db.XodusManager;

@Component
public class AppUserDetailService implements UserDetailsService {

  private final XodusManager xodusManager;

  public AppUserDetailService(XodusManager xodusManager) {
    this.xodusManager = xodusManager;
  }

  @Override
  public final UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {

    final User user = this.xodusManager.fetchUser(username);

    if (user == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }

    boolean locked = user.getLockedOutUntil() != null
        && Instant.ofEpochSecond(user.getLockedOutUntil()).isAfter(Instant.now());

    return org.springframework.security.core.userdetails.User.withUsername(username)
        .password(user.getPassword()).authorities(user.getAuthorities())
        .accountExpired(false).accountLocked(locked).credentialsExpired(false)
        .disabled(!user.isEnabled()).build();
  }

}
package ch.rasc.jwt.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import ch.rasc.jwt.db.User;
import ch.rasc.jwt.db.XodusManager;

@Component
public class AppUserDetailService implements UserDetailsService {

  private final XodusManager xodusManager;

  public AppUserDetailService(XodusManager xodusManager) {
    this.xodusManager = xodusManager;
  }

  @Override
  public final UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    System.out.println(username);
    final User user = this.xodusManager.fetchUser(username);
    System.out.println(user);
    if (user == null) {
      throw new UsernameNotFoundException("User '" + username + "' not found");
    }
System.out.println(user);
    return org.springframework.security.core.userdetails.User.withUsername(username)
        .password(user.getPassword())
        .authorities(user.getAuthorities())
        .accountExpired(false)
        .accountLocked(user.getLockedOutUntil() != null)
        .credentialsExpired(false)
        .disabled(!user.isEnabled())
        .build();
  }

}
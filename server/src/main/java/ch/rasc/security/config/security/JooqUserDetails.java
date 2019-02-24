package ch.rasc.security.config.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import ch.rasc.security.db.tables.pojos.AppUser;

public class JooqUserDetails implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final Collection<GrantedAuthority> authorities;

  private final String password;

  private final String username;

  private final boolean enabled;

  private final Long userDbId;

  private final boolean locked;

  private final boolean expired;

  public JooqUserDetails(AppUser user, List<String> roles) {
    this.userDbId = user.getId();

    this.password = user.getPasswordHash();
    this.username = user.getUserName();
    this.enabled = user.getEnabled() != null ? user.getEnabled().booleanValue() : false;

    if (user.getLockedOutUntil() != null
        && user.getLockedOutUntil().isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
      this.locked = true;
    }
    else {
      this.locked = false;
    }

    this.expired = false;

    Set<GrantedAuthority> auths = new HashSet<>();
    for (String role : roles) {
      auths.add(new SimpleGrantedAuthority(role));
    }

    this.authorities = Collections.unmodifiableCollection(auths);
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  public Long getUserDbId() {
    return this.userDbId;
  }

  @Override
  public boolean isAccountNonExpired() {
    return !this.expired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !this.locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

}

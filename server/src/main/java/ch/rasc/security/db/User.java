package ch.rasc.security.db;

import jetbrains.exodus.entitystore.Entity;

public class User {

  private String name;

  private String username;

  private String email;

  private String password;

  private String authorities;

  private boolean enabled;

  private Integer failedLogins;

  private Long lockedOutUntil;

  private Long lastAccess;

  private String passwordResetToken;

  private Long passwordResetTokenValidUntil;

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getAuthorities() {
    return this.authorities;
  }

  public void setAuthorities(String authorities) {
    this.authorities = authorities;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Integer getFailedLogins() {
    return this.failedLogins;
  }

  public void setFailedLogins(Integer failedLogins) {
    this.failedLogins = failedLogins;
  }

  public Long getLockedOutUntil() {
    return this.lockedOutUntil;
  }

  public void setLockedOutUntil(Long lockedOutUntil) {
    this.lockedOutUntil = lockedOutUntil;
  }

  public Long getLastAccess() {
    return this.lastAccess;
  }

  public void setLastAccess(Long lastAccess) {
    this.lastAccess = lastAccess;
  }

  public String getPasswordResetToken() {
    return this.passwordResetToken;
  }

  public void setPasswordResetToken(String passwordResetToken) {
    this.passwordResetToken = passwordResetToken;
  }

  public Long getPasswordResetTokenValidUntil() {
    return this.passwordResetTokenValidUntil;
  }

  public void setPasswordResetTokenValidUntil(Long passwordResetTokenValidUntil) {
    this.passwordResetTokenValidUntil = passwordResetTokenValidUntil;
  }

  public static User fromEntity(Entity entity) {
    User user = new User();
    user.setName((String) entity.getProperty("name"));
    user.setUsername((String) entity.getProperty("username"));
    user.setEmail((String) entity.getProperty("email"));
    user.setPassword((String) entity.getProperty("password"));
    user.setAuthorities((String) entity.getProperty("authorities"));
    user.setEnabled((Boolean) entity.getProperty("enabled"));
    user.setFailedLogins((Integer) entity.getProperty("failedLogins"));
    user.setLockedOutUntil((Long) entity.getProperty("lockedOutUntil"));
    user.setLastAccess((Long) entity.getProperty("lastAccess"));
    user.setPasswordResetToken((String) entity.getProperty("passwordResetToken"));
    user.setPasswordResetTokenValidUntil(
        (Long) entity.getProperty("passwordResetTokenValidUntil"));
    return user;
  }

  public void toEntity(Entity entity) {
    entity.setProperty("name", this.getName());
    entity.setProperty("username", this.getUsername());
    entity.setProperty("email", this.getEmail());
    entity.setProperty("password", this.getPassword());
    entity.setProperty("authorities", this.getAuthorities());
    entity.setProperty("enabled", this.isEnabled());

    if (this.getFailedLogins() != null) {
      entity.setProperty("failedLogins", this.getFailedLogins());
    }
    else {
      entity.deleteProperty("failedLogins");
    }

    if (this.getLockedOutUntil() != null) {
      entity.setProperty("lockedOutUntil", this.getLockedOutUntil());
    }
    else {
      entity.deleteProperty("lockedOutUntil");
    }

    entity.setProperty("lastAccess", this.getLastAccess());

    if (this.getPasswordResetToken() != null) {
      entity.setProperty("passwordResetToken", this.getPasswordResetToken());
    }
    else {
      entity.deleteProperty("passwordResetToken");
    }

    if (this.getPasswordResetTokenValidUntil() != null) {
      entity.setProperty("passwordResetTokenValidUntil",
          this.getPasswordResetTokenValidUntil());
    }
    else {
      entity.deleteProperty("passwordResetTokenValidUntil");
    }
  }

  @Override
  public String toString() {
    return "User [name=" + this.name + ", username=" + this.username + ", email="
        + this.email + ", password=" + this.password + ", authorities=" + this.authorities
        + ", enabled=" + this.enabled + ", failedLogins=" + this.failedLogins
        + ", lockedOutUntil=" + this.lockedOutUntil + ", lastAccess=" + this.lastAccess
        + ", passwordResetToken=" + this.passwordResetToken
        + ", passwordResetTokenValidUntil=" + this.passwordResetTokenValidUntil + "]";
  }

}

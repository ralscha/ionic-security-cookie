package ch.rasc.jwt.db;

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

  @Override
  public String toString() {
    return "User [name=" + this.name + ", username=" + this.username + ", email=" + this.email
        + ", password=" + this.password + ", authorities=" + this.authorities + ", enabled="
        + this.enabled + ", failedLogins=" + this.failedLogins + ", lockedOutUntil="
        + this.lockedOutUntil + ", lastAccess=" + this.lastAccess + ", passwordResetToken="
        + this.passwordResetToken + ", passwordResetTokenValidUntil="
        + this.passwordResetTokenValidUntil + "]";
  }

}

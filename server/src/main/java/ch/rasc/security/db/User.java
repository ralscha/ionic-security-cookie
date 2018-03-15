package ch.rasc.security.db;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.rasc.security.config.security.Authority;
import jetbrains.exodus.entitystore.Entity;

@JsonInclude(Include.NON_NULL)
public class User {

  private String firstName;

  private String lastName;

  private String username;

  private String email;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String oldPassword;

  private List<Authority> authorities;

  private boolean enabled;

  private Integer failedLogins;

  private Long lockedOutUntil;

  private Long lastAccess;

  @JsonIgnore
  private String passwordResetToken;

  private Long passwordResetTokenValidUntil;

  public String getFirstName() {
    return this.firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
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

  public String getOldPassword() {
    return this.oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public List<Authority> getAuthorities() {
    return this.authorities;
  }

  public void setAuthorities(List<Authority> authorities) {
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
    user.setFirstName((String) entity.getProperty("firstName"));
    user.setLastName((String) entity.getProperty("lastName"));
    user.setUsername((String) entity.getProperty("username"));
    user.setEmail((String) entity.getProperty("email"));
    user.setPassword((String) entity.getProperty("password"));

    String authorities = (String) entity.getProperty("authorities");
    if (authorities != null) {
      user.setAuthorities(Arrays.stream(authorities.split(",")).map(Authority::valueOf)
          .collect(Collectors.toList()));
    }

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
    entity.setProperty("firstName", this.getFirstName());
    entity.setProperty("lastName", this.getLastName());
    entity.setProperty("username", this.getUsername());
    entity.setProperty("email", this.getEmail());
    entity.setProperty("password", this.getPassword());
    if (this.getAuthorities() != null) {
      entity.setProperty("authorities", this.getAuthorities().stream()
          .map(Authority::name).collect(Collectors.joining(",")));
    }
    else {
      entity.deleteProperty("authorities");
    }
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
    return "User [firstName=" + this.firstName + ", lastName=" + this.lastName
        + ", username=" + this.username + ", email=" + this.email + ", password="
        + this.password + ", oldPassword=" + this.oldPassword + ", authorities="
        + this.authorities + ", enabled=" + this.enabled + ", failedLogins="
        + this.failedLogins + ", lockedOutUntil=" + this.lockedOutUntil + ", lastAccess="
        + this.lastAccess + ", passwordResetToken=" + this.passwordResetToken
        + ", passwordResetTokenValidUntil=" + this.passwordResetTokenValidUntil + "]";
  }

}

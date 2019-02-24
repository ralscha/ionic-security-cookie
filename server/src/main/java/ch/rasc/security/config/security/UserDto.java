package ch.rasc.security.config.security;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UserDto {

  private String firstName;

  private String lastName;

  private String userName;

  private String email;

  private String password;

  private String oldPassword;

  private Long lastAccess;

  private String authorities;

  private boolean lockedOut;

  public UserDto() {
    // default constructor for jackson
  }

  public UserDto(String firstName, String lastName, String userName, String email) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userName = userName;
    this.email = email;
  }

  public UserDto(String firstName, String lastName, String userName, String email,
      boolean lockedOut, LocalDateTime lastAccess, String authorities) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userName = userName;
    this.email = email;
    this.lockedOut = lockedOut;
    if (lastAccess != null) {
      this.lastAccess = lastAccess.toEpochSecond(ZoneOffset.UTC);
    }
    else {
      this.lastAccess = null;
    }
    this.authorities = authorities;
  }

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

  public String getUserName() {
    return this.userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
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

  public Long getLastAccess() {
    return this.lastAccess;
  }

  public void setLastAccess(Long lastAccess) {
    this.lastAccess = lastAccess;
  }

  public String getAuthorities() {
    return this.authorities;
  }

  public void setAuthorities(String authorities) {
    this.authorities = authorities;
  }

  public boolean isLockedOut() {
    return this.lockedOut;
  }

  public void setLockedOut(boolean lockedOut) {
    this.lockedOut = lockedOut;
  }

}

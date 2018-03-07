package ch.rasc.security.db;

import jetbrains.exodus.entitystore.Entity;

public class RememberMeToken {

  private String series;

  private String tokenValue;

  private long tokenDate;

  private String ipAddress;

  private String userAgent;

  private String username;

  public String getSeries() {
    return this.series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getTokenValue() {
    return this.tokenValue;
  }

  public void setTokenValue(String tokenValue) {
    this.tokenValue = tokenValue;
  }

  public long getTokenDate() {
    return this.tokenDate;
  }

  public void setTokenDate(long tokenDate) {
    this.tokenDate = tokenDate;
  }

  public String getIpAddress() {
    return this.ipAddress;
  }

  public void setIpAddress(String ipAddress) {
    this.ipAddress = ipAddress;
  }

  public String getUserAgent() {
    return this.userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public static RememberMeToken fromEntity(Entity entity) {
    RememberMeToken token = new RememberMeToken();
    token.setSeries((String) entity.getProperty("series"));
    token.setTokenValue((String) entity.getProperty("tokenValue"));
    token.setTokenDate((Long) entity.getProperty("tokenDate"));
    token.setIpAddress((String) entity.getProperty("ipAddress"));
    token.setUserAgent((String) entity.getProperty("userAgent"));
    token.setUsername((String) entity.getProperty("username"));
    return token;
  }

  public void toEntity(Entity entity) {
    entity.setProperty("series", this.getSeries());
    entity.setProperty("tokenValue", this.getTokenValue());
    entity.setProperty("tokenDate", this.getTokenDate());
    entity.setProperty("ipAddress", this.getIpAddress());
    entity.setProperty("userAgent", this.getUserAgent());
    entity.setProperty("username", this.getUsername());
  }

}

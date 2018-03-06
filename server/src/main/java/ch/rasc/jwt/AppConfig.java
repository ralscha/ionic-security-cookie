package ch.rasc.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {
  private String allowOrigin;

  public String getAllowOrigin() {
    return this.allowOrigin;
  }

  public void setAllowOrigin(String allowOrigin) {
    this.allowOrigin = allowOrigin;
  }

}

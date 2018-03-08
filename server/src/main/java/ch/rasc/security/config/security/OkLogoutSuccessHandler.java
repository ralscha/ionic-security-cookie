package ch.rasc.security.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.rasc.security.config.AppProperties;

@Component
public class OkLogoutSuccessHandler implements LogoutSuccessHandler {

  private final AppProperties appProperties;

  public OkLogoutSuccessHandler(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (StringUtils.hasText(this.appProperties.getAllowOrigin())) {
      response.addHeader("Access-Control-Allow-Origin",
          this.appProperties.getAllowOrigin());
      response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    response.setStatus(HttpServletResponse.SC_OK);
  }

}

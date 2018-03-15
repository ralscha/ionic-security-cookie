package ch.rasc.security.config.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.rasc.security.config.AppProperties;

@Component
public class JsonAuthFailureHandler implements AuthenticationFailureHandler {

  private final AppProperties appProperties;

  public JsonAuthFailureHandler(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException exception)
      throws IOException, ServletException {

    if (StringUtils.hasText(this.appProperties.getAllowOrigin())
        && !"false".equals(this.appProperties.getAllowOrigin())) {
      response.addHeader("Access-Control-Allow-Origin",
          this.appProperties.getAllowOrigin());
      response.addHeader("Access-Control-Allow-Credentials", "true");
    }
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
  }

}

package ch.rasc.jwt.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.rasc.jwt.AppConfig;

@Component
public class OkLogoutSuccessHandler implements LogoutSuccessHandler {

  private final AppConfig appConfig;

  public OkLogoutSuccessHandler(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    if (StringUtils.hasText(this.appConfig.getAllowOrigin())) {
      response.addHeader("Access-Control-Allow-Origin", this.appConfig.getAllowOrigin());
      response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    response.setStatus(HttpStatus.OK.value());
    response.getWriter().flush();
  }

}

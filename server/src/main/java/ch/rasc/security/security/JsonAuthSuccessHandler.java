package ch.rasc.security.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.rasc.security.AppConfig;

@Component
public class JsonAuthSuccessHandler implements AuthenticationSuccessHandler {

  private final AppConfig appConfig;

  public JsonAuthSuccessHandler(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    if (StringUtils.hasText(this.appConfig.getAllowOrigin())) {
      response.addHeader("Access-Control-Allow-Origin", this.appConfig.getAllowOrigin());
      response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    response.setStatus(HttpServletResponse.SC_OK);
    response.getWriter()
        .print(SecurityContextHolder.getContext().getAuthentication().getName());
  }

}
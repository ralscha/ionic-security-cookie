package ch.rasc.security.config.security;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.rasc.security.config.AppProperties;

@Component
public class JsonAuthSuccessHandler implements AuthenticationSuccessHandler {

  private final AppProperties appProperties;

  public JsonAuthSuccessHandler(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    if (StringUtils.hasText(this.appProperties.getAllowOrigin())
        && !"false".equals(this.appProperties.getAllowOrigin())) {
      response.addHeader("Access-Control-Allow-Origin",
          this.appProperties.getAllowOrigin());
      response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    response.getWriter()
        .print(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
            .stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(",")));
  }

}
package ch.rasc.security.config.security;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import ch.rasc.security.config.AppProperties;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final JsonAuthFailureHandler jsonAuthFailureHandler;

  private final JsonAuthSuccessHandler jsonAuthSuccessHandler;

  private final OkLogoutSuccessHandler okLogoutSuccessHandler;

  private final AppProperties appProperties;

  private final RememberMeServices rememberMeServices;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  public SecurityConfig(JsonAuthFailureHandler jsonAuthFailureHandler,
      JsonAuthSuccessHandler jsonAuthSuccessHandler,
      OkLogoutSuccessHandler okLogoutSuccessHandler, AppProperties appProperties,
      RememberMeServices rememberMeServices) {
    this.jsonAuthFailureHandler = jsonAuthFailureHandler;
    this.jsonAuthSuccessHandler = jsonAuthSuccessHandler;
    this.okLogoutSuccessHandler = okLogoutSuccessHandler;
    this.appProperties = appProperties;
    this.rememberMeServices = rememberMeServices;
  }

  @Bean
  @ConditionalOnProperty(name = "app.allow-origin", havingValue = "")
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(this.appProperties.getAllowOrigin());
    configuration.setAllowedMethods(List.of("GET", "POST"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(List.of(HttpHeaders.CONTENT_TYPE));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .rememberMe(customizer -> {
          customizer.rememberMeServices(this.rememberMeServices);
          customizer.key(this.appProperties.getRemembermeCookieKey());
        })
        .formLogin(customizer -> {
          customizer.successHandler(this.jsonAuthSuccessHandler);
          customizer.failureHandler(this.jsonAuthFailureHandler);
          customizer.permitAll();
        })
        .logout(customizer -> {
          customizer.logoutSuccessHandler(this.okLogoutSuccessHandler);
          customizer.deleteCookies("JSESSIONID");
          customizer.permitAll();
        })
        .authorizeRequests(customizer -> {
          customizer.antMatchers("/signup", "/login", "/public", "/reset", "/change")
              .permitAll();
          customizer.anyRequest().authenticated();
        })
        .exceptionHandling(customizer -> customizer
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
  }

}
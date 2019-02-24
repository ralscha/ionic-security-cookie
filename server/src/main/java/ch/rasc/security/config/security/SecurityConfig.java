package ch.rasc.security.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;

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

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
	  http
		.csrf().disable()
	    .cors()
	  .and()
  	  .rememberMe()
        .rememberMeServices(this.rememberMeServices)
        .key(this.appProperties.getRemembermeCookieKey())
  	.and()
  	  .formLogin()
  	    .successHandler(this.jsonAuthSuccessHandler)
  	    .failureHandler(this.jsonAuthFailureHandler)
  	    .permitAll()
  	.and()
  	  .logout()
  	    .logoutSuccessHandler(this.okLogoutSuccessHandler)
  	    .deleteCookies("JSESSIONID")
  	    .permitAll()
  	.and()
		  .authorizeRequests()
		    .antMatchers("/signup", "/login", "/public", "/reset", "/change").permitAll()
		    .anyRequest().authenticated()
    .and()
      .exceptionHandling()
        .authenticationEntryPoint(new Http401UnauthorizedEntryPoint());
    // @formatter:on
  }

}
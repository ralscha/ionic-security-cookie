package ch.rasc.security.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  private final JsonAuthFailureHandler jsonAuthFailureHandler;

  private final JsonAuthSuccessHandler jsonAuthSuccessHandler;

  private final OkLogoutSuccessHandler okLogoutSuccessHandler;

  private final AppUserDetailService appUserDetailService;

  public SecurityConfig(JsonAuthFailureHandler jsonAuthFailureHandler,
      JsonAuthSuccessHandler jsonAuthSuccessHandler,
      OkLogoutSuccessHandler okLogoutSuccessHandler,
      AppUserDetailService appUserDetailService) {
    this.jsonAuthFailureHandler = jsonAuthFailureHandler;
    this.jsonAuthSuccessHandler = jsonAuthSuccessHandler;
    this.okLogoutSuccessHandler = okLogoutSuccessHandler;
    this.appUserDetailService = appUserDetailService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // @formatter:off
		http
		  .csrf().disable()
		  .cors()
	    .and()
  	  .rememberMe()
  	    .userDetailsService(this.appUserDetailService)
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
		    .antMatchers("/signup", "/login", "/public").permitAll()
		    .anyRequest().authenticated()
      .and()
      .exceptionHandling()
        .authenticationEntryPoint(new Http401UnauthorizedEntryPoint());
		// @formatter:on
  }

}
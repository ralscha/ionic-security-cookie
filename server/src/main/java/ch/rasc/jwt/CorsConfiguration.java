package ch.rasc.jwt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

  final AppConfig appConfig;

  public CorsConfiguration(AppConfig appConfig) {
    this.appConfig = appConfig;
  }

  @Bean
  @ConditionalOnProperty(name = "app.allow-origin", havingValue = "")
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(CorsConfiguration.this.appConfig.getAllowOrigin())
            .allowCredentials(true).maxAge(3600);
      }
    };
  }
}
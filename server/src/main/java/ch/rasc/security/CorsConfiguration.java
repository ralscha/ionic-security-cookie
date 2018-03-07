package ch.rasc.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {

  final AppProperties appProperties;

  public CorsConfiguration(AppProperties appProperties) {
    this.appProperties = appProperties;
  }

  @Bean
  @ConditionalOnProperty(name = "app.allow-origin", havingValue = "")
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(CorsConfiguration.this.appProperties.getAllowOrigin())
            .allowCredentials(true).maxAge(3600);
      }
    };
  }
}
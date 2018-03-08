package ch.rasc.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.samskivert.mustache.Mustache;

@SpringBootApplication(exclude = MustacheAutoConfiguration.class)
@EnableScheduling
@EnableAsync
public class Application {

  public final static Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Mustache.Compiler mustacheCompiler() {
    return Mustache.compiler();
  }

}

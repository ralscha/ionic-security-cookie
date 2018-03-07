package ch.rasc.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

import ch.rasc.security.db.User;

@Service
public class MailService {

  private final JavaMailSender mailSender;

  private final String defaultSender;

  private final String appUrl;

  private final String appName;

  private final Mustache.Compiler mustacheCompiler;

  public MailService(JavaMailSender mailSender, AppProperties appProperties,
      Mustache.Compiler mustacheCompiler, @Value("${spring.application.name}") String appName) {
    this.mailSender = mailSender;
    this.defaultSender = appProperties.getDefaultEmailSender();
    this.appUrl = appProperties.getUrl();
    this.appName = appName;
    this.mustacheCompiler = mustacheCompiler;
  }

  @Async
  public void sendPasswordResetEmail(User receiver) {
    String link = this.appUrl + "?token="
        + Base64.getUrlEncoder().encodeToString(receiver.getPasswordResetToken().getBytes(StandardCharsets.UTF_8));

    try {
      sendHtmlMessage(this.defaultSender, receiver.getEmail(),
          this.appName + ": " + "Password Reset",
          getEmailText(receiver.getUsername(), link));
    }
    catch (MessagingException | IOException e) {
      Application.logger.error("sendPasswordResetEmail", e);
    }
  }

  private String getEmailText(String loginName, String link) throws IOException {
    String resource = "pwreset_email.mustache";
    ClassPathResource cp = new ClassPathResource(resource);
    try (InputStream is = cp.getInputStream()) {
      Template template = this.mustacheCompiler.compile(new InputStreamReader(is));

      Map<String, Object> data = new HashMap<>();
      data.put("loginName", loginName);
      data.put("link", link);

      return template.execute(data);
    }
  }

  @Async
  public void sendHtmlMessage(String from, String to, String subject, String text)
      throws MessagingException {
    MimeMessage message = this.mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);
    helper.setFrom(this.defaultSender);
    helper.setTo(to);
    helper.setReplyTo(from);
    helper.setText(text, true);
    helper.setSubject(subject);

    this.mailSender.send(message);
  }

}

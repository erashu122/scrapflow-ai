package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.api.AuthProperties;
import com.scrapflow.identity.application.EmailDeliveryPort;
import java.nio.charset.StandardCharsets;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriUtils;

@Component
public class SmtpEmailDeliveryAdapter implements EmailDeliveryPort {
  private final JavaMailSender mailSender;
  private final AuthProperties properties;

  public SmtpEmailDeliveryAdapter(JavaMailSender mailSender, AuthProperties properties) {
    this.mailSender = mailSender;
    this.properties = properties;
  }

  @Override
  public void sendVerification(String recipientEmail, String recipientName, String verificationToken) {
    send(recipientEmail, "Verify your ScrapFlow email", "Hello " + recipientName + ",\n\nVerify your email to activate your ScrapFlow account:\n" + link("/verify-email", verificationToken) + "\n\nThis link expires in " + properties.verificationTokenTtl() + ".");
  }

  @Override
  public void sendPasswordReset(String recipientEmail, String recipientName, String resetToken) {
    send(recipientEmail, "Reset your ScrapFlow password", "Hello " + recipientName + ",\n\nReset your ScrapFlow password:\n" + link("/reset-password", resetToken) + "\n\nThis link expires in " + properties.passwordResetTokenTtl() + ". If you did not request this, you can ignore this email.");
  }

  private String link(String path, String token) {
    return properties.webBaseUrl().replaceAll("/$", "") + path + "?token=" + UriUtils.encodeQueryParam(token, StandardCharsets.UTF_8);
  }

  private void send(String recipient, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(properties.mailFrom()); message.setTo(recipient); message.setSubject(subject); message.setText(body);
    try { mailSender.send(message); }
    catch (MailException exception) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email delivery is temporarily unavailable"); }
  }
}

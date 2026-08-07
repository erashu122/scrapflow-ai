package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.application.EmailDeliveryPort;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/** Fails closed outside configured delivery infrastructure; no verification secret is ever logged. */
@Component
@Profile("!dev")
@ConditionalOnMissingBean(EmailDeliveryPort.class)
public class UnavailableEmailDeliveryAdapter implements EmailDeliveryPort {
  @Override public void sendVerification(String recipientEmail, String recipientName, String verificationToken) { unavailable(); }
  @Override public void sendPasswordReset(String recipientEmail, String recipientName, String resetToken) { unavailable(); }
  private void unavailable() { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Email delivery is not configured"); }
}

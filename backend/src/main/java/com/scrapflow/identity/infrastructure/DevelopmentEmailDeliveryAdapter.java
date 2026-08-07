package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.application.EmailDeliveryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Development-only adapter. Production must provide an EmailDeliveryPort implementation. */
@Component
@Profile("dev")
public class DevelopmentEmailDeliveryAdapter implements EmailDeliveryPort {
  private static final Logger log = LoggerFactory.getLogger(DevelopmentEmailDeliveryAdapter.class);
  @Override public void sendVerification(String recipientEmail, String recipientName, String verificationToken) { log.info("Verification email queued for {}. Token delivery is intentionally delegated to the development mail sink.", recipientEmail); }
  @Override public void sendPasswordReset(String recipientEmail, String recipientName, String resetToken) { log.info("Password-reset email queued for {}. Token delivery is intentionally delegated to the development mail sink.", recipientEmail); }
}

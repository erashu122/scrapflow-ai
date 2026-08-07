package com.scrapflow.identity.application;

/** Outbound boundary. Production adapters must send short-lived links through an approved provider. */
public interface EmailDeliveryPort {
  void sendVerification(String recipientEmail, String recipientName, String verificationToken);
  void sendPasswordReset(String recipientEmail, String recipientName, String resetToken);
}

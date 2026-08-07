package com.scrapflow.identity.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthDtosTest {
  @Test void registrationRejectsMismatchedPasswordConfirmation() {
    assertFalse(new AuthDtos.RegisterRequest("Ada Buyer", "ada@example.com", "password-that-is-long", "different", true).isPasswordConfirmed());
    assertTrue(new AuthDtos.RegisterRequest("Ada Buyer", "ada@example.com", "password-that-is-long", "password-that-is-long", true).isPasswordConfirmed());
  }
}

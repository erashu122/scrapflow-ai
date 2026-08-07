package com.scrapflow.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class TokenHashingServiceTest {
  private final TokenHashingService service = new TokenHashingService();
  @Test void producesDeterministicNonReversibleHash() {
    assertEquals(service.hash("one-time-token"), service.hash("one-time-token"));
    assertNotEquals("one-time-token", service.hash("one-time-token"));
  }
}

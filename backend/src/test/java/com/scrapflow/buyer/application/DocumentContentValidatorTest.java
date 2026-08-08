package com.scrapflow.buyer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.scrapflow.buyer.domain.BuyerDocumentType;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class DocumentContentValidatorTest {
  private final DocumentContentValidator validator = new DocumentContentValidator();

  @Test void detectsPermittedDocumentSignatures() throws Exception {
    assertEquals("application/pdf", validator.validate(BuyerDocumentType.TRADE_LICENSE, new ByteArrayInputStream(new byte[] {0x25, 0x50, 0x44, 0x46, 0x2D})));
    assertEquals("image/png", validator.validate(BuyerDocumentType.COMPANY_LOGO, new ByteArrayInputStream(new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A})));
  }

  @Test void rejectsSpoofedOrDisallowedContent() {
    assertThrows(ResponseStatusException.class, () -> validator.validate(BuyerDocumentType.COMPANY_LOGO, new ByteArrayInputStream(new byte[] {0x25, 0x50, 0x44, 0x46, 0x2D})));
    assertThrows(ResponseStatusException.class, () -> validator.validate(BuyerDocumentType.TRADE_LICENSE, new ByteArrayInputStream(new byte[] {1, 2, 3})));
  }
}

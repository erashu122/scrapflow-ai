package com.scrapflow.buyer.application;

import com.scrapflow.buyer.domain.BuyerDocumentType;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/** Validates file bytes rather than trusting a client-supplied content type. */
@Component
public class DocumentContentValidator {
  public String validate(BuyerDocumentType type, InputStream input) throws IOException {
    byte[] header = input.readNBytes(8);
    String detected = detect(header);
    if (detected == null || (type == BuyerDocumentType.COMPANY_LOGO ? "application/pdf".equals(detected) : false)) {
      throw unsupported();
    }
    return detected;
  }

  private String detect(byte[] bytes) {
    if (startsWith(bytes, 0x25, 0x50, 0x44, 0x46, 0x2D)) return "application/pdf";
    if (startsWith(bytes, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) return "image/png";
    if (startsWith(bytes, 0xFF, 0xD8, 0xFF)) return "image/jpeg";
    return null;
  }

  private boolean startsWith(byte[] bytes, int... signature) {
    if (bytes.length < signature.length) return false;
    for (int index = 0; index < signature.length; index++) if ((bytes[index] & 0xFF) != signature[index]) return false;
    return true;
  }

  private ResponseStatusException unsupported() { return new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "File contents are not permitted for this document type"); }
}

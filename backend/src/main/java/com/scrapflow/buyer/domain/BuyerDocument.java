package com.scrapflow.buyer.domain;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("documents")
@CompoundIndex(name = "buyer_document_type", def = "{'buyerId': 1, 'type': 1}", unique = true)
public class BuyerDocument {
  @Id private String id; private String buyerId; private BuyerDocumentType type; private String storageKey; private String contentType; private long sizeBytes; private String sha256; private Instant uploadedAt;
  public BuyerDocument() { }
  public BuyerDocument(String buyerId, BuyerDocumentType type, String storageKey, String contentType, long sizeBytes, String sha256) { this.buyerId = buyerId; this.type = type; this.storageKey = storageKey; this.contentType = contentType; this.sizeBytes = sizeBytes; this.sha256 = sha256; this.uploadedAt = Instant.now(); }
  public String getId() { return id; } public String getBuyerId() { return buyerId; } public BuyerDocumentType getType() { return type; } public String getContentType() { return contentType; } public long getSizeBytes() { return sizeBytes; } public Instant getUploadedAt() { return uploadedAt; }
}

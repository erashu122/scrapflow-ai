package com.scrapflow.identity.domain;

import java.time.Instant;

public class RefreshTokenSession {
  private String tokenId;
  private String tokenHash;
  private Instant expiresAt;
  private boolean remembered;
  private Instant revokedAt;

  public RefreshTokenSession() { }

  public RefreshTokenSession(String tokenId, String tokenHash, Instant expiresAt, boolean remembered) {
    this.tokenId = tokenId; this.tokenHash = tokenHash; this.expiresAt = expiresAt; this.remembered = remembered;
  }

  public String getTokenId() { return tokenId; }
  public String getTokenHash() { return tokenHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public boolean isRemembered() { return remembered; }
  public Instant getRevokedAt() { return revokedAt; }
  public void revoke(Instant at) { this.revokedAt = at; }
  public boolean isActive(Instant now) { return revokedAt == null && expiresAt.isAfter(now); }
}

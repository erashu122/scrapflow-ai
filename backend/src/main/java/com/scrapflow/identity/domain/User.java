package com.scrapflow.identity.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@CompoundIndex(name = "email_unique", def = "{'email': 1}", unique = true)
public class User {
  @Id private String id;
  private String fullName;
  private String email;
  private String passwordHash;
  private Role role;
  private boolean emailVerified;
  private boolean enabled;
  @Indexed(sparse = true) private String emailVerificationTokenHash;
  private Instant emailVerificationExpiresAt;
  @Indexed(sparse = true) private String passwordResetTokenHash;
  private Instant passwordResetExpiresAt;
  private List<RefreshTokenSession> refreshTokens = new ArrayList<>();
  private Instant createdAt;
  private Instant updatedAt;

  public User() { }
  public User(String fullName, String email, String passwordHash, Role role) {
    this.fullName = fullName; this.email = email; this.passwordHash = passwordHash; this.role = role;
    this.enabled = true; this.createdAt = Instant.now(); this.updatedAt = this.createdAt;
  }
  public String getId() { return id; }
  public String getFullName() { return fullName; }
  public String getEmail() { return email; }
  public String getPasswordHash() { return passwordHash; }
  public Role getRole() { return role; }
  public boolean isEmailVerified() { return emailVerified; }
  public boolean isEnabled() { return enabled; }
  public List<RefreshTokenSession> getRefreshTokens() { return refreshTokens; }
  public String getEmailVerificationTokenHash() { return emailVerificationTokenHash; }
  public Instant getEmailVerificationExpiresAt() { return emailVerificationExpiresAt; }
  public String getPasswordResetTokenHash() { return passwordResetTokenHash; }
  public Instant getPasswordResetExpiresAt() { return passwordResetExpiresAt; }
  public void setEmailVerification(String hash, Instant expiry) { emailVerificationTokenHash = hash; emailVerificationExpiresAt = expiry; touch(); }
  public void verifyEmail() { emailVerified = true; emailVerificationTokenHash = null; emailVerificationExpiresAt = null; touch(); }
  public void setPasswordReset(String hash, Instant expiry) { passwordResetTokenHash = hash; passwordResetExpiresAt = expiry; touch(); }
  public void clearPasswordReset() { passwordResetTokenHash = null; passwordResetExpiresAt = null; touch(); }
  public void changePassword(String hash) { passwordHash = hash; clearPasswordReset(); }
  public void addRefreshToken(RefreshTokenSession token) { refreshTokens.add(token); touch(); }
  public void revokeRefreshToken(String tokenId, Instant now) { refreshTokens.stream().filter(t -> t.getTokenId().equals(tokenId)).forEach(t -> t.revoke(now)); touch(); }
  public void revokeAllRefreshTokens(Instant now) { refreshTokens.forEach(t -> t.revoke(now)); touch(); }
  public void pruneExpiredRefreshTokens(Instant now) { refreshTokens.removeIf(t -> !t.isActive(now)); touch(); }
  private void touch() { updatedAt = Instant.now(); }
}

package com.scrapflow.identity.application;

import com.scrapflow.identity.api.AuthDtos;
import com.scrapflow.identity.api.AuthProperties;
import com.scrapflow.identity.domain.RefreshTokenSession;
import com.scrapflow.identity.domain.Role;
import com.scrapflow.identity.domain.User;
import com.scrapflow.identity.infrastructure.UserRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private static final SecureRandom RANDOM = new SecureRandom();
  private final UserRepository users;
  private final PasswordEncoder passwords;
  private final JwtTokenService jwt;
  private final TokenHashingService tokenHashing;
  private final AuthProperties properties;
  private final EmailDeliveryPort emailDelivery;
  public AuthService(UserRepository users, PasswordEncoder passwords, JwtTokenService jwt, TokenHashingService tokenHashing, AuthProperties properties, EmailDeliveryPort emailDelivery) {
    this.users = users; this.passwords = passwords; this.jwt = jwt; this.tokenHashing = tokenHashing; this.properties = properties; this.emailDelivery = emailDelivery;
  }
  @Transactional
  public AuthResult register(AuthDtos.RegisterRequest request) {
    String email = normalizeEmail(request.email());
    if (users.existsByEmail(email)) throw conflict("An account already exists for this email address");
    User user = new User(request.fullName().trim(), email, passwords.encode(request.password()), Role.BUYER);
    String verificationToken = newOpaqueToken();
    user.setEmailVerification(tokenHashing.hash(verificationToken), Instant.now().plus(properties.verificationTokenTtl()));
    emailDelivery.sendVerification(user.getEmail(), user.getFullName(), verificationToken);
    users.save(user);
    return issueTokens(user, request.rememberMe());
  }
  public AuthResult login(AuthDtos.LoginRequest request, Role expectedRole) {
    User user = users.findByEmail(normalizeEmail(request.email())).orElseThrow(this::invalidCredentials);
    if (!user.isEnabled() || !passwords.matches(request.password(), user.getPasswordHash())) throw invalidCredentials();
    if (expectedRole != null && user.getRole() != expectedRole) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This account cannot use this sign-in route");
    return issueTokens(user, request.rememberMe());
  }
  public AuthResult refresh(String rawRefreshToken) {
    JwtTokenService.JwtSubject subject;
    try { subject = jwt.parseRefreshToken(rawRefreshToken); }
    catch (RuntimeException exception) { throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token"); }
    User user = users.findById(subject.userId()).orElseThrow(this::invalidCredentials);
    Instant now = Instant.now();
    RefreshTokenSession session = user.getRefreshTokens().stream().filter(token -> token.getTokenId().equals(subject.tokenId()) && token.isActive(now) && token.getTokenHash().equals(tokenHashing.hash(rawRefreshToken))).findFirst().orElse(null);
    if (session == null || !user.isEnabled()) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
    user.revokeRefreshToken(subject.tokenId(), now);
    users.save(user);
    return issueTokens(user, session.isRemembered());
  }
  public void logout(String rawRefreshToken) {
    try {
      JwtTokenService.JwtSubject subject = jwt.parseRefreshToken(rawRefreshToken);
      users.findById(subject.userId()).ifPresent(user -> { user.revokeRefreshToken(subject.tokenId(), Instant.now()); users.save(user); });
    } catch (RuntimeException ignored) { }
  }
  public void requestPasswordReset(AuthDtos.ForgotPasswordRequest request) {
    users.findByEmail(normalizeEmail(request.email())).ifPresent(user -> {
      String token = newOpaqueToken();
      user.setPasswordReset(tokenHashing.hash(token), Instant.now().plus(properties.passwordResetTokenTtl()));
      users.save(user);
      emailDelivery.sendPasswordReset(user.getEmail(), user.getFullName(), token);
    });
  }
  public void resetPassword(AuthDtos.ResetPasswordRequest request) {
    String hash = tokenHashing.hash(request.token()); Instant now = Instant.now();
    User user = users.findByPasswordResetTokenHash(hash).filter(candidate -> candidate.getPasswordResetExpiresAt() != null && candidate.getPasswordResetExpiresAt().isAfter(now))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired password reset token"));
    user.changePassword(passwords.encode(request.password())); user.revokeAllRefreshTokens(now); users.save(user);
  }
  public void verifyEmail(AuthDtos.VerifyEmailRequest request) {
    String hash = tokenHashing.hash(request.token()); Instant now = Instant.now();
    User user = users.findByEmailVerificationTokenHash(hash).filter(candidate -> candidate.getEmailVerificationExpiresAt() != null && candidate.getEmailVerificationExpiresAt().isAfter(now))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired verification token"));
    user.verifyEmail(); users.save(user);
  }
  public void resendVerification(String email) {
    users.findByEmail(normalizeEmail(email)).filter(user -> !user.isEmailVerified()).ifPresent(user -> {
      String token = newOpaqueToken(); user.setEmailVerification(tokenHashing.hash(token), Instant.now().plus(properties.verificationTokenTtl())); users.save(user);
      emailDelivery.sendVerification(user.getEmail(), user.getFullName(), token);
    });
  }
  public AuthDtos.UserResponse currentUser(String id) {
    return users.findById(id).map(this::toResponse).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User session is invalid"));
  }
  public void bootstrapAdminIfConfigured() {
    if (properties.bootstrapAdminEmail() == null || properties.bootstrapAdminEmail().isBlank() || properties.bootstrapAdminPassword() == null || properties.bootstrapAdminPassword().isBlank()) return;
    String email = normalizeEmail(properties.bootstrapAdminEmail());
    if (users.existsByEmail(email)) return;
    User admin = new User("Platform Administrator", email, passwords.encode(properties.bootstrapAdminPassword()), Role.ADMIN);
    admin.verifyEmail(); users.save(admin);
  }
  private AuthResult issueTokens(User user, boolean rememberMe) {
    Instant now = Instant.now(); user.pruneExpiredRefreshTokens(now);
    JwtTokenService.RefreshToken refresh = jwt.createRefreshToken(user, rememberMe);
    user.addRefreshToken(new RefreshTokenSession(refresh.id(), tokenHashing.hash(refresh.value()), refresh.expiresAt(), rememberMe)); users.save(user);
    return new AuthResult(jwt.createAccessToken(user), refresh.value(), refresh.expiresAt(), rememberMe, toResponse(user));
  }
  private AuthDtos.UserResponse toResponse(User user) { return new AuthDtos.UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.isEmailVerified()); }
  private String normalizeEmail(String email) { return email.trim().toLowerCase(java.util.Locale.ROOT); }
  private String newOpaqueToken() { byte[] bytes = new byte[32]; RANDOM.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
  private ResponseStatusException invalidCredentials() { return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"); }
  private ResponseStatusException conflict(String message) { return new ResponseStatusException(HttpStatus.CONFLICT, message); }
  public record AuthResult(String accessToken, String refreshToken, Instant refreshExpiresAt, boolean rememberMe, AuthDtos.UserResponse user) { }
}

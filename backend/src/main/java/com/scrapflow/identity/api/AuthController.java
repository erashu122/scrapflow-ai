package com.scrapflow.identity.api;

import com.scrapflow.identity.application.AuthService;
import com.scrapflow.identity.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication")
public class AuthController {
  private final AuthService auth;
  private final SecurityProperties security;
  public AuthController(AuthService auth, SecurityProperties security) { this.auth = auth; this.security = security; }

  @PostMapping("/register") @Operation(summary = "Register a buyer account")
  public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) { return withSession(auth.register(request), HttpStatus.CREATED); }
  @PostMapping("/login") @Operation(summary = "Sign in with any permitted account")
  public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) { return withSession(auth.login(request, null), HttpStatus.OK); }
  @PostMapping("/buyer/login") @Operation(summary = "Sign in as a buyer")
  public ResponseEntity<AuthDtos.AuthResponse> buyerLogin(@Valid @RequestBody AuthDtos.LoginRequest request) { return withSession(auth.login(request, Role.BUYER), HttpStatus.OK); }
  @PostMapping("/admin/login") @Operation(summary = "Sign in as an administrator")
  public ResponseEntity<AuthDtos.AuthResponse> adminLogin(@Valid @RequestBody AuthDtos.LoginRequest request) { return withSession(auth.login(request, Role.ADMIN), HttpStatus.OK); }
  @PostMapping("/refresh") @Operation(summary = "Rotate the refresh token")
  public ResponseEntity<AuthDtos.AuthResponse> refresh(HttpServletRequest request) {
    String refreshToken = readRefreshToken(request);
    return withSession(auth.refresh(refreshToken), HttpStatus.OK);
  }
  @PostMapping("/logout") @Operation(summary = "Revoke the current refresh token")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    String token = readOptionalRefreshToken(request); if (token != null) auth.logout(token);
    return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, clearCookie().toString()).build();
  }
  @PostMapping("/forgot-password") @Operation(summary = "Request a password reset")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody AuthDtos.ForgotPasswordRequest request) { auth.requestPasswordReset(request); return ResponseEntity.accepted().build(); }
  @PostMapping("/reset-password") @Operation(summary = "Reset a password with a one-time token")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest request) { auth.resetPassword(request); return ResponseEntity.noContent().build(); }
  @PostMapping("/verify-email") @Operation(summary = "Verify an email address with a one-time token")
  public ResponseEntity<Void> verifyEmail(@Valid @RequestBody AuthDtos.VerifyEmailRequest request) { auth.verifyEmail(request); return ResponseEntity.noContent().build(); }
  @PostMapping("/resend-verification") @Operation(summary = "Request a new verification email")
  public ResponseEntity<Void> resendVerification(@Valid @RequestBody AuthDtos.ResendVerificationRequest request) { auth.resendVerification(request.email()); return ResponseEntity.accepted().build(); }
  @GetMapping("/me") @Operation(summary = "Get the authenticated user")
  public AuthDtos.UserResponse me(Authentication authentication) { return auth.currentUser((String) authentication.getPrincipal()); }
  private ResponseEntity<AuthDtos.AuthResponse> withSession(AuthService.AuthResult result, HttpStatus status) {
    return ResponseEntity.status(status).header(HttpHeaders.SET_COOKIE, refreshCookie(result).toString()).body(new AuthDtos.AuthResponse(result.accessToken(), result.user()));
  }
  private ResponseCookie refreshCookie(AuthService.AuthResult result) {
    ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(security.refreshCookieName(), result.refreshToken()).httpOnly(true).secure(security.secureCookies()).sameSite("Strict").path("/api/v1/auth");
    if (result.rememberMe()) builder.maxAge(Duration.between(Instant.now(), result.refreshExpiresAt()));
    return builder.build();
  }
  private ResponseCookie clearCookie() { return ResponseCookie.from(security.refreshCookieName(), "").httpOnly(true).secure(security.secureCookies()).sameSite("Strict").path("/api/v1/auth").maxAge(Duration.ZERO).build(); }
  private String readRefreshToken(HttpServletRequest request) { String token = readOptionalRefreshToken(request); if (token == null) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is missing"); return token; }
  private String readOptionalRefreshToken(HttpServletRequest request) { var cookie = WebUtils.getCookie(request, security.refreshCookieName()); return cookie == null ? null : cookie.getValue(); }
}

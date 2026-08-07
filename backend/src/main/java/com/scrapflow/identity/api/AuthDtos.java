package com.scrapflow.identity.api;

import com.scrapflow.identity.domain.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
  private AuthDtos() { }
  public record RegisterRequest(
      @NotBlank @Size(max = 120) String fullName,
      @NotBlank @Email @Size(max = 254) String email,
      @NotBlank @Size(min = 12, max = 128) String password,
      @NotBlank String confirmPassword,
      boolean rememberMe
  ) { @AssertTrue(message = "password and confirmPassword must match") public boolean isPasswordConfirmed() { return password.equals(confirmPassword); } }
  public record LoginRequest(@NotBlank @Email String email, @NotBlank String password, boolean rememberMe) { }
  public record RefreshRequest(String ignored) { }
  public record ForgotPasswordRequest(@NotBlank @Email String email) { }
  public record ResendVerificationRequest(@NotBlank @Email String email) { }
  public record ResetPasswordRequest(@NotBlank String token, @NotBlank @Size(min = 12, max = 128) String password, @NotBlank String confirmPassword) {
    @AssertTrue(message = "password and confirmPassword must match") public boolean isPasswordConfirmed() { return password.equals(confirmPassword); }
  }
  public record VerifyEmailRequest(@NotBlank String token) { }
  public record UserResponse(String id, String fullName, String email, Role role, boolean emailVerified) { }
  public record AuthResponse(String accessToken, UserResponse user) { }
}

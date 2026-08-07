package com.scrapflow.identity.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.security")
public record SecurityProperties(
    @NotBlank @Size(min = 32) String jwtSecret,
    Duration accessTokenTtl,
    Duration refreshTokenTtl,
    Duration rememberedRefreshTokenTtl,
    @NotBlank String refreshCookieName,
    boolean secureCookies,
    List<String> allowedOrigins
) { }

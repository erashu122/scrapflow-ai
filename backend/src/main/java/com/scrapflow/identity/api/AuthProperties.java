package com.scrapflow.identity.api;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("app.auth")
public record AuthProperties(
    Duration verificationTokenTtl,
    Duration passwordResetTokenTtl,
    String bootstrapAdminEmail,
    String bootstrapAdminPassword
) { }

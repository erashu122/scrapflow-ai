package com.scrapflow.buyer.api;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
@Validated
@ConfigurationProperties("app.buyer-storage")
public record BuyerStorageProperties(@NotBlank String rootPath) { }

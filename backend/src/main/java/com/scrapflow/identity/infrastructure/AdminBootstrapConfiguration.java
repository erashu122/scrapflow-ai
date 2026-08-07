package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.application.AuthService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBootstrapConfiguration {
  @Bean ApplicationRunner bootstrapAdmin(AuthService authService) { return arguments -> authService.bootstrapAdminIfConfigured(); }
}

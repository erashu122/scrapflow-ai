package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.api.SecurityProperties;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    return http.csrf(csrf -> csrf.disable()).cors(cors -> { }).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login", "/api/v1/auth/buyer/login", "/api/v1/auth/admin/login", "/api/v1/auth/refresh", "/api/v1/auth/logout", "/api/v1/auth/forgot-password", "/api/v1/auth/reset-password", "/api/v1/auth/verify-email", "/api/v1/auth/resend-verification").permitAll()
            .requestMatchers("/api/v1/health", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
            .requestMatchers("/api/v1/auth/me").authenticated().anyRequest().authenticated())
        .exceptionHandling(errors -> errors.authenticationEntryPoint((request, response, exception) -> { response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE); response.getWriter().write("{\"status\":401,\"title\":\"Unauthorized\"}"); })
            .accessDeniedHandler((request, response, exception) -> { response.setStatus(HttpServletResponse.SC_FORBIDDEN); response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE); response.getWriter().write("{\"status\":403,\"title\":\"Forbidden\"}"); }))
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
  }
  @Bean
  CorsConfigurationSource corsConfigurationSource(SecurityProperties properties) {
    CorsConfiguration configuration = new CorsConfiguration(); configuration.setAllowedOrigins(properties.allowedOrigins()); configuration.setAllowedMethods(List.of(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name())); configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Request-Id")); configuration.setAllowCredentials(true); configuration.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); source.registerCorsConfiguration("/api/**", configuration); return source;
  }
}

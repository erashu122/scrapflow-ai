package com.scrapflow.identity.application;

import com.scrapflow.identity.api.SecurityProperties;
import com.scrapflow.identity.domain.Role;
import com.scrapflow.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {
  private final SecurityProperties properties;
  private final SecretKey signingKey;
  public JwtTokenService(SecurityProperties properties) {
    this.properties = properties;
    this.signingKey = Keys.hmacShaKeyFor(properties.jwtSecret().getBytes(StandardCharsets.UTF_8));
  }
  public String createAccessToken(User user) { return create(user, "ACCESS", null, properties.accessTokenTtl().toSeconds()); }
  public RefreshToken createRefreshToken(User user, boolean rememberMe) {
    String id = UUID.randomUUID().toString();
    long seconds = (rememberMe ? properties.rememberedRefreshTokenTtl() : properties.refreshTokenTtl()).toSeconds();
    return new RefreshToken(id, create(user, "REFRESH", id, seconds), Instant.now().plusSeconds(seconds));
  }
  public JwtSubject parseAccessToken(String token) { return parse(token, "ACCESS"); }
  public JwtSubject parseRefreshToken(String token) { return parse(token, "REFRESH"); }
  private String create(User user, String type, String id, long expiresInSeconds) {
    Instant now = Instant.now();
    var builder = Jwts.builder().subject(user.getId()).issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(expiresInSeconds)))
        .claim("type", type).claim("email", user.getEmail()).claim("role", user.getRole().name()).signWith(signingKey);
    if (id != null) builder.id(id);
    return builder.compact();
  }
  private JwtSubject parse(String token, String expectedType) {
    Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    if (!expectedType.equals(claims.get("type", String.class))) throw new IllegalArgumentException("Unexpected token type");
    return new JwtSubject(claims.getSubject(), claims.get("email", String.class), Role.valueOf(claims.get("role", String.class)), claims.getId());
  }
  public record RefreshToken(String id, String value, Instant expiresAt) { }
  public record JwtSubject(String userId, String email, Role role, String tokenId) { }
}

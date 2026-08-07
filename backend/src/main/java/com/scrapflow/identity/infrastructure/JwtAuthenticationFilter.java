package com.scrapflow.identity.infrastructure;

import com.scrapflow.identity.application.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtTokenService jwt;
  public JwtAuthenticationFilter(JwtTokenService jwt) { this.jwt = jwt; }
  @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        JwtTokenService.JwtSubject subject = jwt.parseAccessToken(header.substring(7));
        var authentication = new UsernamePasswordAuthenticationToken(subject.userId(), null, List.of(new SimpleGrantedAuthority("ROLE_" + subject.role().name())));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (RuntimeException ignored) { }
    }
    chain.doFilter(request, response);
  }
}

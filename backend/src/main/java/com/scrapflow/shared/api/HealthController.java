package com.scrapflow.shared.api;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
  @GetMapping("/health")
  ResponseEntity<Map<String, Object>> health() {
    return ResponseEntity.ok(Map.of("status", "UP", "service", "scrapflow-api", "timestamp", Instant.now().toString()));
  }
}

package com.bloodbond.alertsystem.controller;

import com.bloodbond.alertsystem.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * LifePulse - Emergency Blood Alert System
 * Controller: HealthController
 * Provides /api/health endpoint for cloud platform liveness/readiness probes
 * (Railway, Render, Azure, Docker Compose, Kubernetes).
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;
    private final Instant startTime = Instant.now();

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealthStatus() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "LifePulse Emergency Blood Alert System");
        health.put("startedAt", startTime.toString());

        // Check database connectivity
        boolean dbConnected = false;
        try (Connection conn = dataSource.getConnection()) {
            dbConnected = conn.isValid(2);
        } catch (Exception ignored) {
        }
        health.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");

        return ResponseEntity.ok(ApiResponse.success("Service is healthy and ready.", health));
    }
}

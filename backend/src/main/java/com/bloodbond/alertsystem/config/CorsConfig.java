package com.bloodbond.alertsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * LifePulse - Emergency Blood Alert System
 * Configuration: CorsConfig
 * Centralized, production-grade CORS configuration.
 * Governed by 'cors.allowed-origins' property or 'CORS_ALLOWED_ORIGINS' environment variable.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        logger.info("Configuring CORS policy with allowed origins: {}", Arrays.toString(origins));

        boolean allowAny = origins.length == 1 && "*".equals(origins[0]);

        if (allowAny) {
            // Permissive mode for local development / testing
            registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .maxAge(3600);
        } else {
            // Production lockdown: pattern matching allows exact domains and wildcard subdomains
            registry.addMapping("/**")
                    .allowedOriginPatterns(origins)
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }
}

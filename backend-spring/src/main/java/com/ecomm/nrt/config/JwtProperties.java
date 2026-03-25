package com.ecomm.nrt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Strongly-typed binding for app.jwt.* properties in application.properties.
 * This eliminates the "unknown property" warnings from Spring Boot's config processor.
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /** Base64-encoded HMAC-SHA secret key */
    private String secret;

    /** Token validity in milliseconds (default: 24h) */
    private long expirationMs = 86_400_000L;
}

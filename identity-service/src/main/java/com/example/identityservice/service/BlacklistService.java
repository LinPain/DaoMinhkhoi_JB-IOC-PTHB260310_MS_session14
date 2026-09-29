package com.example.identityservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;

@Service
public class BlacklistService {

    private final StringRedisTemplate redisTemplate;
    private final SecretKey secretKey;

    public BlacklistService(
            StringRedisTemplate redisTemplate,
            @Value("${app.jwt.secret}") String secret
    ) {
        this.redisTemplate = redisTemplate;

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void logout(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String jti = claims.getId();

        Instant expiration =
                claims.getExpiration().toInstant();

        Instant now = Instant.now();

        long ttlSeconds =
                Duration.between(now, expiration)
                        .getSeconds();

        if (ttlSeconds <= 0) {
            return;
        }

        String key = "blacklist:" + jti;

        redisTemplate.opsForValue().set(
                key,
                "revoked",
                Duration.ofSeconds(ttlSeconds)
        );
    }
}
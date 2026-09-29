package com.example.identityservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class JwtBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public JwtBlacklistService(
            StringRedisTemplate redisTemplate
    ) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isBlacklisted(String jti) {

        String key = "blacklist:" + jti;

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }
}
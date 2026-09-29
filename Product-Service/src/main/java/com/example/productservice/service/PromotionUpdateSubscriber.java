package com.example.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromotionUpdateSubscriber {

    private final CacheManager cacheManager;

    public void handleMessage(String message) {

        Long productId = Long.parseLong(message);

        System.out.println(
                ">>> Received promotion update for product: "
                        + productId
        );

        Cache cache = cacheManager.getCache("products");

        if (cache != null) {
            cache.evict(productId);

            System.out.println(
                    ">>> Cache evicted for product: "
                            + productId
            );
        }
    }
}
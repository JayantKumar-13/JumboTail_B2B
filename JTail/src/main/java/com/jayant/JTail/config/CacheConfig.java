package com.jayant.JTail.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

// Configures caching for the application using Caffeine as the cache provider.

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "nearestWarehouse",
                "shippingCharge",
                "products"
        );

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(500)                       // Max 500 cached entries
                        .expireAfterWrite(10, TimeUnit.MINUTES) // Expire after 10 min
                        .recordStats()                          // Enable cache hit/miss metrics
        );

        return cacheManager;
    }
}

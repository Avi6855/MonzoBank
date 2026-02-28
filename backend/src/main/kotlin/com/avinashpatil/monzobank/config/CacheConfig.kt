package com.avinashpatil.monzobank.config

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.interceptor.CacheErrorHandler
import org.springframework.cache.interceptor.CacheResolver
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * Cache configuration for Redis-based caching
 * 
 * This configuration sets up Redis as the caching provider with custom
 * cache configurations for different cache regions.
 */
@Configuration
class CacheConfig : CachingConfigurerSupport() {

    companion object {
        // Cache names
        const val USER_CACHE = "users"
        const val ACCOUNT_CACHE = "accounts"
        const val TRANSACTION_CACHE = "transactions"
        const val CARD_CACHE = "cards"
        const val POT_CACHE = "pots"
        const val INVESTMENT_CACHE = "investments"
        const val LOAN_CACHE = "loans"
        const val MARKET_DATA_CACHE = "market-data"
        const val EXCHANGE_RATES_CACHE = "exchange-rates"
        const val NOTIFICATION_CACHE = "notifications"
    }

    /**
 * Redis cache manager with custom configurations for different cache regions
     */
    @Bean
    override fun cacheManager(): CacheManager {
        return RedisCacheManager.builder()
            .cacheDefaults(defaultCacheConfiguration())
            .withCacheConfiguration(USER_CACHE, userCacheConfiguration())
            .withCacheConfiguration(ACCOUNT_CACHE, accountCacheConfiguration())
            .withCacheConfiguration(TRANSACTION_CACHE, transactionCacheConfiguration())
            .withCacheConfiguration(CARD_CACHE, cardCacheConfiguration())
            .withCacheConfiguration(POT_CACHE, potCacheConfiguration())
            .withCacheConfiguration(INVESTMENT_CACHE, investmentCacheConfiguration())
            .withCacheConfiguration(LOAN_CACHE, loanCacheConfiguration())
            .withCacheConfiguration(MARKET_DATA_CACHE, marketDataCacheConfiguration())
            .withCacheConfiguration(EXCHANGE_RATES_CACHE, exchangeRatesCacheConfiguration())
            .withCacheConfiguration(NOTIFICATION_CACHE, notificationCacheConfiguration())
            .build()
    }

    /**
     * Default cache configuration
     */
    private fun defaultCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
            .disableCachingNullValues()
    }

    /**
     * User cache configuration - longer TTL for user data
     */
    private fun userCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofHours(1))
    }

    /**
     * Account cache configuration - medium TTL for account data
     */
    private fun accountCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(30))
    }

    /**
     * Transaction cache configuration - shorter TTL for transaction data
     */
    private fun transactionCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(5))
    }

    /**
     * Card cache configuration - longer TTL for card data
     */
    private fun cardCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofHours(2))
    }

    /**
     * Pot cache configuration - medium TTL for pot data
     */
    private fun potCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(15))
    }

    /**
     * Investment cache configuration - shorter TTL for investment data
     */
    private fun investmentCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(2))
    }

    /**
     * Loan cache configuration - longer TTL for loan data
     */
    private fun loanCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofHours(1))
    }

    /**
     * Market data cache configuration - very short TTL for real-time data
     */
    private fun marketDataCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(1))
    }

    /**
     * Exchange rates cache configuration - short TTL for currency data
     */
    private fun exchangeRatesCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(5))
    }

    /**
     * Notification cache configuration - short TTL for notification data
     */
    private fun notificationCacheConfiguration(): RedisCacheConfiguration {
        return defaultCacheConfiguration()
            .entryTtl(Duration.ofMinutes(3))
    }

    /**
     * Custom key generator for cache keys
     */
    @Bean
    override fun keyGenerator(): KeyGenerator {
        return KeyGenerator { target, method, params ->
            val className = target.javaClass.simpleName
            val methodName = method.name
            val paramString = params.joinToString(",") { it?.toString() ?: "null" }
            "$className:$methodName:$paramString"
        }
    }

    /**
     * Cache error handler to handle Redis connection issues gracefully
     */
    @Bean
    override fun errorHandler(): CacheErrorHandler {
        return object : CacheErrorHandler {
            override fun handleCacheGetError(exception: RuntimeException, cache: org.springframework.cache.Cache, key: Any) {
                // Log error and continue without cache
                println("Cache GET error for key $key: ${exception.message}")
            }

            override fun handleCachePutError(exception: RuntimeException, cache: org.springframework.cache.Cache, key: Any, value: Any?) {
                // Log error and continue without cache
                println("Cache PUT error for key $key: ${exception.message}")
            }

            override fun handleCacheEvictError(exception: RuntimeException, cache: org.springframework.cache.Cache, key: Any) {
                // Log error and continue without cache
                println("Cache EVICT error for key $key: ${exception.message}")
            }

            override fun handleCacheClearError(exception: RuntimeException, cache: org.springframework.cache.Cache) {
                // Log error and continue without cache
                println("Cache CLEAR error: ${exception.message}")
            }
        }
    }

    /**
     * Redis cache manager builder customizer
     */
    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        return RedisCacheManagerBuilderCustomizer { builder ->
            builder
                .transactionAware()
                .enableStatistics()
        }
    }
}
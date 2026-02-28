package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

data class CacheStats(
    val totalEntries: Long = 0,
    val totalSize: Long = 0,
    val hitCount: Long = 0,
    val missCount: Long = 0,
    val evictionCount: Long = 0,
    val loadCount: Long = 0,
    val loadExceptionCount: Long = 0,
    val totalLoadTime: Long = 0,
    val averageLoadPenalty: Double = 0.0,
    val hitRate: Double = 0.0,
    val missRate: Double = 0.0,
    val requestCount: Long = 0,
    val maxSize: Long = 0,
    val currentSize: Long = 0,
    val oldestEntry: LocalDateTime? = null,
    val newestEntry: LocalDateTime? = null,
    val lastCleanup: LocalDateTime? = null,
    val cacheType: CacheType = CacheType.MEMORY,
    val status: CacheStatus = CacheStatus.ACTIVE
)

enum class CacheType {
    MEMORY,
    DISK,
    HYBRID,
    DISTRIBUTED,
    REDIS,
    DATABASE
}

enum class CacheStatus {
    ACTIVE,
    INACTIVE,
    FULL,
    ERROR,
    CLEANING,
    DISABLED
}
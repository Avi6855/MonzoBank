package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.CacheStats
import java.time.LocalDateTime

data class CacheEntry<T>(
    val key: String,
    val value: T,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val accessCount: Long = 0,
    val lastAccessed: LocalDateTime = LocalDateTime.now()
)

data class CacheConfiguration(
    val maxSize: Long = 1000,
    val defaultTtlMinutes: Long = 60,
    val evictionPolicy: EvictionPolicy = EvictionPolicy.LRU,
    val enableStatistics: Boolean = true
)

enum class EvictionPolicy {
    LRU, // Least Recently Used
    LFU, // Least Frequently Used
    FIFO, // First In First Out
    TTL_ONLY // Time To Live only
}

interface CacheRepository {
    suspend fun <T> get(key: String): Result<T?>
    suspend fun <T> put(key: String, value: T, ttlMinutes: Long? = null): Result<Unit>
    suspend fun remove(key: String): Result<Unit>
    suspend fun clear(): Result<Unit>
    suspend fun exists(key: String): Result<Boolean>
    suspend fun getKeys(pattern: String? = null): Result<List<String>>
    suspend fun getSize(): Result<Long>
    suspend fun getStats(): Result<CacheStats>
    suspend fun evictExpired(): Result<Long>
    suspend fun getConfiguration(): Result<CacheConfiguration>
    suspend fun updateConfiguration(config: CacheConfiguration): Result<Unit>
    suspend fun warmUp(keys: List<String>): Result<Unit>
    suspend fun export(): Result<Map<String, Any>>
    suspend fun import(data: Map<String, Any>): Unit
}
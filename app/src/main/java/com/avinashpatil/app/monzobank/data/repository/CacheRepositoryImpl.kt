package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.model.CacheStats
import com.avinashpatil.app.monzobank.domain.model.CacheType
import com.avinashpatil.app.monzobank.domain.model.CacheStatus
import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheRepositoryImpl @Inject constructor() : CacheRepository {
    
    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()
    private var configuration = CacheConfiguration()
    private var hitCount = 0L
    private var missCount = 0L
    private var evictionCount = 0L
    private var loadCount = 0L
    private var totalLoadTime = 0L
    
    override suspend fun <T> get(key: String): Result<T?> {
        return try {
            val entry = cache[key]
            if (entry != null) {
                if (isExpired(entry)) {
                    cache.remove(key)
                    missCount++
                    Result.success(null)
                } else {
                    // Update access statistics
                    val updatedEntry = entry.copy(
                        accessCount = entry.accessCount + 1,
                        lastAccessed = LocalDateTime.now()
                    )
                    cache[key] = updatedEntry
                    hitCount++
                    @Suppress("UNCHECKED_CAST")
                    Result.success(entry.value as T?)
                }
            } else {
                missCount++
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun <T> put(key: String, value: T, ttlMinutes: Long?): Result<Unit> {
        return try {
            val ttl = ttlMinutes ?: configuration.defaultTtlMinutes
            val expiresAt = if (ttl > 0) {
                LocalDateTime.now().plusMinutes(ttl)
            } else null
            
            val entry = CacheEntry(
                key = key,
                value = value as Any,
                expiresAt = expiresAt
            )
            
            // Check if we need to evict entries
            if (cache.size >= configuration.maxSize) {
                evictEntries(1)
            }
            
            cache[key] = entry
            loadCount++
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun remove(key: String): Result<Unit> {
        return try {
            cache.remove(key)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clear(): Result<Unit> {
        return try {
            cache.clear()
            resetStatistics()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exists(key: String): Result<Boolean> {
        return try {
            val entry = cache[key]
            val exists = entry != null && !isExpired(entry)
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getKeys(pattern: String?): Result<List<String>> {
        return try {
            val keys = if (pattern != null) {
                cache.keys.filter { it.contains(pattern, ignoreCase = true) }
            } else {
                cache.keys.toList()
            }
            Result.success(keys)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSize(): Result<Long> {
        return try {
            Result.success(cache.size.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getStats(): Result<CacheStats> {
        return try {
            val totalRequests = hitCount + missCount
            val hitRate = if (totalRequests > 0) hitCount.toDouble() / totalRequests else 0.0
            val missRate = if (totalRequests > 0) missCount.toDouble() / totalRequests else 0.0
            val averageLoadPenalty = if (loadCount > 0) totalLoadTime.toDouble() / loadCount else 0.0
            
            val entries = cache.values.filter { !isExpired(it) }
            val oldestEntry = entries.minByOrNull { it.createdAt }?.createdAt
            val newestEntry = entries.maxByOrNull { it.createdAt }?.createdAt
            
            val stats = CacheStats(
                totalEntries = cache.size.toLong(),
                totalSize = cache.size.toLong(), // Simplified - in real implementation would calculate actual memory size
                hitCount = hitCount,
                missCount = missCount,
                evictionCount = evictionCount,
                loadCount = loadCount,
                loadExceptionCount = 0L, // Not tracked in this simple implementation
                totalLoadTime = totalLoadTime,
                averageLoadPenalty = averageLoadPenalty,
                hitRate = hitRate,
                missRate = missRate,
                requestCount = totalRequests,
                maxSize = configuration.maxSize,
                currentSize = cache.size.toLong(),
                oldestEntry = oldestEntry,
                newestEntry = newestEntry,
                lastCleanup = LocalDateTime.now(),
                cacheType = CacheType.MEMORY,
                status = CacheStatus.ACTIVE
            )
            
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun evictExpired(): Result<Long> {
        return try {
            val expiredKeys = cache.entries
                .filter { isExpired(it.value) }
                .map { it.key }
            
            expiredKeys.forEach { cache.remove(it) }
            evictionCount += expiredKeys.size
            
            Result.success(expiredKeys.size.toLong())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getConfiguration(): Result<CacheConfiguration> {
        return try {
            Result.success(configuration)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateConfiguration(config: CacheConfiguration): Result<Unit> {
        return try {
            this.configuration = config
            
            // If max size is reduced, evict entries if necessary
            if (cache.size > config.maxSize) {
                val entriesToEvict = cache.size - config.maxSize.toInt()
                evictEntries(entriesToEvict)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun warmUp(keys: List<String>): Result<Unit> {
        return try {
            // In a real implementation, this would pre-load data for the given keys
            // For now, we'll just ensure the keys exist in cache
            keys.forEach { key ->
                if (!cache.containsKey(key)) {
                    // Mock warm-up by putting a placeholder
                    put(key, "warmed_up_placeholder")
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun export(): Result<Map<String, Any>> {
        return try {
            val exportData = cache.entries.associate { (key, entry) ->
                key to mapOf(
                    "value" to entry.value,
                    "createdAt" to entry.createdAt.toString(),
                    "expiresAt" to entry.expiresAt?.toString(),
                    "accessCount" to entry.accessCount
                )
            }
            Result.success(exportData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun import(data: Map<String, Any>) {
        data.forEach { (key, entryData) ->
            if (entryData is Map<*, *>) {
                val value = entryData["value"]
                if (value != null) {
                    put(key, value)
                }
            }
        }
    }
    
    private fun isExpired(entry: CacheEntry<Any>): Boolean {
        return entry.expiresAt?.isBefore(LocalDateTime.now()) ?: false
    }
    
    private fun evictEntries(count: Int) {
        val entriesToEvict = when (configuration.evictionPolicy) {
            EvictionPolicy.LRU -> {
                cache.entries.sortedBy { it.value.lastAccessed }.take(count)
            }
            EvictionPolicy.LFU -> {
                cache.entries.sortedBy { it.value.accessCount }.take(count)
            }
            EvictionPolicy.FIFO -> {
                cache.entries.sortedBy { it.value.createdAt }.take(count)
            }
            EvictionPolicy.TTL_ONLY -> {
                cache.entries.filter { isExpired(it.value) }.take(count)
            }
        }
        
        entriesToEvict.forEach { 
            cache.remove(it.key)
            evictionCount++
        }
    }
    
    private fun resetStatistics() {
        hitCount = 0L
        missCount = 0L
        evictionCount = 0L
        loadCount = 0L
        totalLoadTime = 0L
    }
}
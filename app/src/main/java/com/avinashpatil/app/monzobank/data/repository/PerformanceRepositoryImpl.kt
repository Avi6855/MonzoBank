package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.PerformanceMetric
import com.avinashpatil.app.monzobank.domain.repository.PerformanceRepository
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceRepositoryImpl @Inject constructor() : PerformanceRepository {
    
    private val metrics = ConcurrentHashMap<String, MutableList<PerformanceMetric>>()
    
    override suspend fun recordMetric(metric: PerformanceMetric): Result<Unit> {
        return try {
            val metricList = metrics.getOrPut(metric.name) { mutableListOf() }
            metricList.add(metric)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMetrics(
        name: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<List<PerformanceMetric>> {
        return try {
            val metricList = metrics[name] ?: emptyList()
            val filteredMetrics = metricList.filter { metric ->
                metric.timestamp.isAfter(startTime) && metric.timestamp.isBefore(endTime)
            }
            Result.success(filteredMetrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAverageMetric(
        name: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<Double> {
        return try {
            val metricsResult = getMetrics(name, startTime, endTime).getOrThrow()
            val average = if (metricsResult.isNotEmpty()) {
                metricsResult.map { it.value }.average()
            } else {
                0.0
            }
            Result.success(average)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordAppStartTime(startTime: Long): Result<Unit> {
        return try {
            val metric = PerformanceMetric(
                id = UUID.randomUUID().toString(),
                name = "app_start_time",
                value = startTime.toDouble(),
                unit = "milliseconds",
                timestamp = LocalDateTime.now()
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordScreenLoadTime(screenName: String, loadTime: Long): Result<Unit> {
        return try {
            val metric = PerformanceMetric(
                id = UUID.randomUUID().toString(),
                name = "screen_load_time",
                value = loadTime.toDouble(),
                unit = "milliseconds",
                timestamp = LocalDateTime.now(),
                context = mapOf("screen" to screenName)
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordApiResponseTime(endpoint: String, responseTime: Long): Result<Unit> {
        return try {
            val metric = PerformanceMetric(
                id = UUID.randomUUID().toString(),
                name = "api_response_time",
                value = responseTime.toDouble(),
                unit = "milliseconds",
                timestamp = LocalDateTime.now(),
                context = mapOf("endpoint" to endpoint)
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordMemoryUsage(usage: Long): Result<Unit> {
        return try {
            val metric = PerformanceMetric(
                id = UUID.randomUUID().toString(),
                name = "memory_usage",
                value = usage.toDouble(),
                unit = "bytes",
                timestamp = LocalDateTime.now()
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordCpuUsage(usage: Double): Result<Unit> {
        return try {
            val metric = PerformanceMetric(
                id = UUID.randomUUID().toString(),
                name = "cpu_usage",
                value = usage,
                unit = "percentage",
                timestamp = LocalDateTime.now()
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPerformanceSummary(): Result<Map<String, Any>> {
        return try {
            val now = LocalDateTime.now()
            val oneHourAgo = now.minusHours(1)
            
            val summary = mutableMapOf<String, Any>()
            
            metrics.keys.forEach { metricName ->
                val recentMetrics = getMetrics(metricName, oneHourAgo, now).getOrThrow()
                if (recentMetrics.isNotEmpty()) {
                    val values = recentMetrics.map { it.value }
                    summary["${metricName}_count"] = values.size
                    summary["${metricName}_average"] = values.average()
                    summary["${metricName}_min"] = values.minOrNull() ?: 0.0
                    summary["${metricName}_max"] = values.maxOrNull() ?: 0.0
                }
            }
            
            Result.success(summary.toMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
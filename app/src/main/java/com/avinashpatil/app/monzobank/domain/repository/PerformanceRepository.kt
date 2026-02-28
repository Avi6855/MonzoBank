package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class PerformanceMetric(
    val id: String,
    val name: String,
    val value: Double,
    val unit: String,
    val timestamp: LocalDateTime,
    val context: Map<String, String> = emptyMap()
)

interface PerformanceRepository {
    suspend fun recordMetric(metric: PerformanceMetric): Result<Unit>
    suspend fun getMetrics(name: String, startTime: LocalDateTime, endTime: LocalDateTime): Result<List<PerformanceMetric>>
    suspend fun getAverageMetric(name: String, startTime: LocalDateTime, endTime: LocalDateTime): Result<Double>
    suspend fun recordAppStartTime(startTime: Long): Result<Unit>
    suspend fun recordScreenLoadTime(screenName: String, loadTime: Long): Result<Unit>
    suspend fun recordApiResponseTime(endpoint: String, responseTime: Long): Result<Unit>
    suspend fun recordMemoryUsage(usage: Long): Result<Unit>
    suspend fun recordCpuUsage(usage: Double): Result<Unit>
    suspend fun getPerformanceSummary(): Result<Map<String, Any>>
}
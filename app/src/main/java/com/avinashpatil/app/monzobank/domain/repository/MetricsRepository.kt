package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Metric(
    val id: String,
    val name: String,
    val value: Double,
    val unit: String,
    val tags: Map<String, String> = emptyMap(),
    val timestamp: LocalDateTime
)

data class MetricSummary(
    val name: String,
    val count: Long,
    val sum: Double,
    val average: Double,
    val min: Double,
    val max: Double,
    val unit: String,
    val period: String
)

data class Counter(
    val name: String,
    val value: Long,
    val tags: Map<String, String> = emptyMap(),
    val timestamp: LocalDateTime
)

data class Gauge(
    val name: String,
    val value: Double,
    val tags: Map<String, String> = emptyMap(),
    val timestamp: LocalDateTime
)

data class Timer(
    val name: String,
    val duration: Long, // in milliseconds
    val tags: Map<String, String> = emptyMap(),
    val timestamp: LocalDateTime
)

enum class MetricType {
    COUNTER,
    GAUGE,
    TIMER,
    HISTOGRAM,
    SUMMARY
}

interface MetricsRepository {
    suspend fun recordMetric(metric: Metric): Result<Unit>
    suspend fun recordCounter(counter: Counter): Result<Unit>
    suspend fun recordGauge(gauge: Gauge): Result<Unit>
    suspend fun recordTimer(timer: Timer): Result<Unit>
    suspend fun incrementCounter(name: String, tags: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun decrementCounter(name: String, tags: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun setGauge(name: String, value: Double, tags: Map<String, String> = emptyMap()): Result<Unit>
    suspend fun startTimer(name: String, tags: Map<String, String> = emptyMap()): Result<String> // Returns timer ID
    suspend fun stopTimer(timerId: String): Result<Unit>
    suspend fun getMetrics(name: String, startTime: LocalDateTime, endTime: LocalDateTime): Result<List<Metric>>
    suspend fun getMetricSummary(name: String, startTime: LocalDateTime, endTime: LocalDateTime): Result<MetricSummary>
    suspend fun getAllMetricNames(): Result<List<String>>
    suspend fun getMetricsByTag(tag: String, value: String): Result<List<Metric>>
    suspend fun deleteMetrics(name: String): Result<Unit>
    suspend fun exportMetrics(format: String): Result<String>
}
package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRepositoryImpl @Inject constructor() : MetricsRepository {
    
    private val metrics = ConcurrentHashMap<String, MutableList<Metric>>()
    private val counters = ConcurrentHashMap<String, Long>()
    private val gauges = ConcurrentHashMap<String, Double>()
    private val activeTimers = ConcurrentHashMap<String, Long>()
    
    override suspend fun recordMetric(metric: Metric): Result<Unit> {
        return try {
            val metricList = metrics.getOrPut(metric.name) { mutableListOf() }
            metricList.add(metric)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordCounter(counter: Counter): Result<Unit> {
        return try {
            val key = "${counter.name}_${counter.tags.hashCode()}"
            counters[key] = counter.value
            
            val metric = Metric(
                id = UUID.randomUUID().toString(),
                name = counter.name,
                value = counter.value.toDouble(),
                unit = "count",
                tags = counter.tags,
                timestamp = counter.timestamp
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordGauge(gauge: Gauge): Result<Unit> {
        return try {
            val key = "${gauge.name}_${gauge.tags.hashCode()}"
            gauges[key] = gauge.value
            
            val metric = Metric(
                id = UUID.randomUUID().toString(),
                name = gauge.name,
                value = gauge.value,
                unit = "gauge",
                tags = gauge.tags,
                timestamp = gauge.timestamp
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun recordTimer(timer: Timer): Result<Unit> {
        return try {
            val metric = Metric(
                id = UUID.randomUUID().toString(),
                name = timer.name,
                value = timer.duration.toDouble(),
                unit = "milliseconds",
                tags = timer.tags,
                timestamp = timer.timestamp
            )
            recordMetric(metric)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun incrementCounter(name: String, tags: Map<String, String>): Result<Unit> {
        return try {
            val key = "${name}_${tags.hashCode()}"
            val currentValue = counters.getOrDefault(key, 0L)
            val counter = Counter(
                name = name,
                value = currentValue + 1,
                tags = tags,
                timestamp = LocalDateTime.now()
            )
            recordCounter(counter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun decrementCounter(name: String, tags: Map<String, String>): Result<Unit> {
        return try {
            val key = "${name}_${tags.hashCode()}"
            val currentValue = counters.getOrDefault(key, 0L)
            val counter = Counter(
                name = name,
                value = maxOf(0L, currentValue - 1),
                tags = tags,
                timestamp = LocalDateTime.now()
            )
            recordCounter(counter)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setGauge(name: String, value: Double, tags: Map<String, String>): Result<Unit> {
        return try {
            val gauge = Gauge(
                name = name,
                value = value,
                tags = tags,
                timestamp = LocalDateTime.now()
            )
            recordGauge(gauge)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun startTimer(name: String, tags: Map<String, String>): Result<String> {
        return try {
            val timerId = UUID.randomUUID().toString()
            activeTimers[timerId] = System.currentTimeMillis()
            Result.success(timerId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun stopTimer(timerId: String): Result<Unit> {
        return try {
            val startTime = activeTimers.remove(timerId)
            if (startTime != null) {
                val duration = System.currentTimeMillis() - startTime
                val timer = Timer(
                    name = "timer_$timerId",
                    duration = duration,
                    tags = emptyMap(),
                    timestamp = LocalDateTime.now()
                )
                recordTimer(timer)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMetrics(
        name: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<List<Metric>> {
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
    
    override suspend fun getMetricSummary(
        name: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Result<MetricSummary> {
        return try {
            val metricsResult = getMetrics(name, startTime, endTime).getOrThrow()
            
            if (metricsResult.isEmpty()) {
                val summary = MetricSummary(
                    name = name,
                    count = 0,
                    sum = 0.0,
                    average = 0.0,
                    min = 0.0,
                    max = 0.0,
                    unit = "",
                    period = "${startTime}_${endTime}"
                )
                Result.success(summary)
            } else {
                val values = metricsResult.map { it.value }
                val summary = MetricSummary(
                    name = name,
                    count = values.size.toLong(),
                    sum = values.sum(),
                    average = values.average(),
                    min = values.minOrNull() ?: 0.0,
                    max = values.maxOrNull() ?: 0.0,
                    unit = metricsResult.first().unit,
                    period = "${startTime}_${endTime}"
                )
                Result.success(summary)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllMetricNames(): Result<List<String>> {
        return try {
            Result.success(metrics.keys.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMetricsByTag(tag: String, value: String): Result<List<Metric>> {
        return try {
            val allMetrics = metrics.values.flatten()
            val filteredMetrics = allMetrics.filter { metric ->
                metric.tags[tag] == value
            }
            Result.success(filteredMetrics)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteMetrics(name: String): Result<Unit> {
        return try {
            metrics.remove(name)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportMetrics(format: String): Result<String> {
        return try {
            when (format.lowercase()) {
                "json" -> {
                    val allMetrics = metrics.values.flatten()
                    val json = allMetrics.joinToString(
                        prefix = "[\n",
                        postfix = "\n]",
                        separator = ",\n"
                    ) { metric ->
                        """
                        {
                            "id": "${metric.id}",
                            "name": "${metric.name}",
                            "value": ${metric.value},
                            "unit": "${metric.unit}",
                            "timestamp": "${metric.timestamp}",
                            "tags": ${metric.tags}
                        }
                        """.trimIndent()
                    }
                    Result.success(json)
                }
                "csv" -> {
                    val allMetrics = metrics.values.flatten()
                    val csv = buildString {
                        appendLine("id,name,value,unit,timestamp,tags")
                        allMetrics.forEach { metric ->
                            appendLine("${metric.id},${metric.name},${metric.value},${metric.unit},${metric.timestamp},${metric.tags}")
                        }
                    }
                    Result.success(csv)
                }
                else -> Result.failure(Exception("Unsupported format: $format"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
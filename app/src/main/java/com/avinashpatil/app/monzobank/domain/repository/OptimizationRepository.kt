package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class OptimizationTask(
    val id: String,
    val name: String,
    val description: String,
    val type: OptimizationType,
    val status: OptimizationStatus,
    val priority: OptimizationPriority,
    val estimatedImpact: String,
    val scheduledAt: LocalDateTime?,
    val executedAt: LocalDateTime?,
    val executionTime: Long?, // in milliseconds
    val results: OptimizationResults?
)

data class OptimizationResults(
    val performanceImprovement: Double, // percentage
    val memoryReduction: Long, // in bytes
    val cpuReduction: Double, // percentage
    val batteryImprovement: Double, // percentage
    val details: Map<String, Any>
)

data class PerformanceMetrics(
    val cpuUsage: Double,
    val memoryUsage: Long,
    val batteryUsage: Double,
    val networkUsage: Long,
    val responseTime: Long,
    val timestamp: LocalDateTime
)

enum class OptimizationType {
    DATABASE_OPTIMIZATION,
    MEMORY_OPTIMIZATION,
    CPU_OPTIMIZATION,
    NETWORK_OPTIMIZATION,
    BATTERY_OPTIMIZATION,
    CACHE_OPTIMIZATION,
    IMAGE_OPTIMIZATION,
    CODE_OPTIMIZATION
}

enum class OptimizationStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum class OptimizationPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

interface OptimizationRepository {
    suspend fun getOptimizationTasks(): Result<List<OptimizationTask>>
    suspend fun scheduleOptimization(type: OptimizationType, priority: OptimizationPriority): Result<String>
    suspend fun executeOptimization(taskId: String): Result<Unit>
    suspend fun cancelOptimization(taskId: String): Result<Unit>
    suspend fun getPerformanceMetrics(): Result<PerformanceMetrics>
    suspend fun getOptimizationHistory(): Result<List<OptimizationTask>>
    suspend fun autoOptimize(): Result<List<OptimizationTask>>
    suspend fun getOptimizationRecommendations(): Result<List<OptimizationTask>>
}
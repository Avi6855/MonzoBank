package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class HealthCheck(
    val id: String,
    val name: String,
    val description: String,
    val status: HealthStatus,
    val responseTime: Long, // in milliseconds
    val lastChecked: LocalDateTime,
    val endpoint: String?,
    val errorMessage: String?,
    val metadata: Map<String, String> = emptyMap()
)

data class SystemHealth(
    val overallStatus: HealthStatus,
    val checks: List<HealthCheck>,
    val timestamp: LocalDateTime,
    val uptime: Long, // in seconds
    val version: String
)

enum class HealthStatus {
    HEALTHY,
    DEGRADED,
    UNHEALTHY,
    UNKNOWN
}

interface HealthCheckRepository {
    suspend fun performHealthCheck(): Result<SystemHealth>
    suspend fun getHealthStatus(): Result<HealthStatus>
    suspend fun getIndividualChecks(): Result<List<HealthCheck>>
    suspend fun checkDatabase(): Result<HealthCheck>
    suspend fun checkExternalServices(): Result<List<HealthCheck>>
    suspend fun checkMemoryUsage(): Result<HealthCheck>
    suspend fun checkDiskSpace(): Result<HealthCheck>
    suspend fun checkNetworkConnectivity(): Result<HealthCheck>
    suspend fun getSystemMetrics(): Result<Map<String, Any>>
    suspend fun getUptime(): Result<Long>
}
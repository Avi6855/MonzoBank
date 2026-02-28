package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class ServiceStatus(
    val serviceName: String,
    val status: ServiceHealth,
    val responseTime: Long, // in milliseconds
    val uptime: Double, // percentage
    val lastChecked: LocalDateTime,
    val incidents: List<ServiceIncident> = emptyList()
)

data class ServiceIncident(
    val id: String,
    val title: String,
    val description: String,
    val severity: IncidentSeverity,
    val status: IncidentStatus,
    val affectedServices: List<String>,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime?,
    val updates: List<IncidentUpdate> = emptyList()
)

data class IncidentUpdate(
    val id: String,
    val message: String,
    val timestamp: LocalDateTime,
    val author: String
)

data class SystemStatus(
    val overallStatus: ServiceHealth,
    val services: List<ServiceStatus>,
    val activeIncidents: List<ServiceIncident>,
    val lastUpdated: LocalDateTime
)

enum class ServiceHealth {
    OPERATIONAL,
    DEGRADED,
    PARTIAL_OUTAGE,
    MAJOR_OUTAGE,
    MAINTENANCE
}

enum class IncidentSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class IncidentStatus {
    INVESTIGATING,
    IDENTIFIED,
    MONITORING,
    RESOLVED
}

interface StatusRepository {
    suspend fun getSystemStatus(): Result<SystemStatus>
    suspend fun getServiceStatus(serviceName: String): Result<ServiceStatus?>
    suspend fun getAllServiceStatuses(): Result<List<ServiceStatus>>
    suspend fun getActiveIncidents(): Result<List<ServiceIncident>>
    suspend fun getIncident(incidentId: String): Result<ServiceIncident?>
    suspend fun getIncidentHistory(): Result<List<ServiceIncident>>
    suspend fun subscribeToStatusUpdates(userId: String): Result<Unit>
    suspend fun unsubscribeFromStatusUpdates(userId: String): Result<Unit>
}
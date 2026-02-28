package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class MaintenanceWindow(
    val id: String,
    val title: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val affectedServices: List<String>,
    val severity: MaintenanceSeverity,
    val status: MaintenanceStatus,
    val isPlanned: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

enum class MaintenanceSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class MaintenanceStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DELAYED
}

interface MaintenanceRepository {
    suspend fun getMaintenanceWindows(): Result<List<MaintenanceWindow>>
    suspend fun getCurrentMaintenance(): Result<List<MaintenanceWindow>>
    suspend fun getUpcomingMaintenance(): Result<List<MaintenanceWindow>>
    suspend fun getMaintenanceWindow(maintenanceId: String): Result<MaintenanceWindow?>
    suspend fun isServiceUnderMaintenance(serviceName: String): Result<Boolean>
    suspend fun getMaintenanceHistory(): Result<List<MaintenanceWindow>>
    suspend fun subscribeToMaintenanceUpdates(userId: String): Result<Unit>
    suspend fun unsubscribeFromMaintenanceUpdates(userId: String): Result<Unit>
}
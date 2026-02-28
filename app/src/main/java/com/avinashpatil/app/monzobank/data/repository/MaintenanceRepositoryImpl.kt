package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MaintenanceRepositoryImpl @Inject constructor() : MaintenanceRepository {
    
    private val maintenanceWindows = listOf(
        MaintenanceWindow(
            id = "maint1",
            title = "Scheduled Database Maintenance",
            description = "Routine database optimization and security updates",
            startTime = LocalDateTime.now().plusDays(2).withHour(2).withMinute(0),
            endTime = LocalDateTime.now().plusDays(2).withHour(4).withMinute(0),
            affectedServices = listOf("transactions", "balance_inquiry", "transfers"),
            severity = MaintenanceSeverity.MEDIUM,
            status = MaintenanceStatus.SCHEDULED,
            isPlanned = true,
            createdAt = LocalDateTime.now().minusDays(7),
            updatedAt = LocalDateTime.now().minusDays(1)
        ),
        MaintenanceWindow(
            id = "maint2",
            title = "Payment System Upgrade",
            description = "Upgrading payment processing infrastructure",
            startTime = LocalDateTime.now().minusHours(2),
            endTime = LocalDateTime.now().plusHours(1),
            affectedServices = listOf("payments", "card_transactions"),
            severity = MaintenanceSeverity.HIGH,
            status = MaintenanceStatus.IN_PROGRESS,
            isPlanned = true,
            createdAt = LocalDateTime.now().minusDays(14),
            updatedAt = LocalDateTime.now().minusMinutes(30)
        )
    )
    
    private val subscribers = mutableSetOf<String>()
    
    override suspend fun getMaintenanceWindows(): Result<List<MaintenanceWindow>> {
        return try {
            val sortedWindows = maintenanceWindows.sortedByDescending { it.startTime }
            Result.success(sortedWindows)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentMaintenance(): Result<List<MaintenanceWindow>> {
        return try {
            val now = LocalDateTime.now()
            val currentMaintenance = maintenanceWindows.filter { window ->
                window.status == MaintenanceStatus.IN_PROGRESS ||
                (window.startTime.isBefore(now) && window.endTime.isAfter(now))
            }
            Result.success(currentMaintenance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUpcomingMaintenance(): Result<List<MaintenanceWindow>> {
        return try {
            val now = LocalDateTime.now()
            val upcomingMaintenance = maintenanceWindows.filter { window ->
                window.startTime.isAfter(now) && window.status == MaintenanceStatus.SCHEDULED
            }.sortedBy { it.startTime }
            
            Result.success(upcomingMaintenance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMaintenanceWindow(maintenanceId: String): Result<MaintenanceWindow?> {
        return try {
            val window = maintenanceWindows.find { it.id == maintenanceId }
            Result.success(window)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isServiceUnderMaintenance(serviceName: String): Result<Boolean> {
        return try {
            val now = LocalDateTime.now()
            val isUnderMaintenance = maintenanceWindows.any { window ->
                window.affectedServices.contains(serviceName) &&
                window.startTime.isBefore(now) &&
                window.endTime.isAfter(now) &&
                window.status == MaintenanceStatus.IN_PROGRESS
            }
            
            Result.success(isUnderMaintenance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMaintenanceHistory(): Result<List<MaintenanceWindow>> {
        return try {
            val now = LocalDateTime.now()
            val history = maintenanceWindows.filter { window ->
                window.endTime.isBefore(now) && 
                (window.status == MaintenanceStatus.COMPLETED || window.status == MaintenanceStatus.CANCELLED)
            }.sortedByDescending { it.endTime }
            
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun subscribeToMaintenanceUpdates(userId: String): Result<Unit> {
        return try {
            subscribers.add(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unsubscribeFromMaintenanceUpdates(userId: String): Result<Unit> {
        return try {
            subscribers.remove(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
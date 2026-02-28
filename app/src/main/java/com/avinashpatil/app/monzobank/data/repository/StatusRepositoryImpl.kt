package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusRepositoryImpl @Inject constructor() : StatusRepository {
    
    private val services = listOf(
        ServiceStatus(
            serviceName = "Authentication",
            status = ServiceHealth.OPERATIONAL,
            responseTime = 120L,
            uptime = 99.9,
            lastChecked = LocalDateTime.now().minusMinutes(1)
        ),
        ServiceStatus(
            serviceName = "Transactions",
            status = ServiceHealth.OPERATIONAL,
            responseTime = 85L,
            uptime = 99.8,
            lastChecked = LocalDateTime.now().minusMinutes(1)
        ),
        ServiceStatus(
            serviceName = "Payments",
            status = ServiceHealth.DEGRADED,
            responseTime = 450L,
            uptime = 98.5,
            lastChecked = LocalDateTime.now().minusMinutes(1)
        ),
        ServiceStatus(
            serviceName = "Card Services",
            status = ServiceHealth.OPERATIONAL,
            responseTime = 95L,
            uptime = 99.7,
            lastChecked = LocalDateTime.now().minusMinutes(1)
        )
    )
    
    private val incidents = listOf(
        ServiceIncident(
            id = "inc1",
            title = "Payment Processing Delays",
            description = "Some users may experience delays in payment processing",
            severity = IncidentSeverity.MEDIUM,
            status = IncidentStatus.MONITORING,
            affectedServices = listOf("Payments"),
            startTime = LocalDateTime.now().minusHours(2),
            endTime = null,
            updates = listOf(
                IncidentUpdate(
                    id = "update1",
                    message = "We have identified the issue and are working on a fix",
                    timestamp = LocalDateTime.now().minusHours(1),
                    author = "Engineering Team"
                )
            )
        )
    )
    
    private val subscribers = mutableSetOf<String>()
    
    override suspend fun getSystemStatus(): Result<SystemStatus> {
        return try {
            val overallStatus = when {
                services.any { it.status == ServiceHealth.MAJOR_OUTAGE } -> ServiceHealth.MAJOR_OUTAGE
                services.any { it.status == ServiceHealth.PARTIAL_OUTAGE } -> ServiceHealth.PARTIAL_OUTAGE
                services.any { it.status == ServiceHealth.DEGRADED } -> ServiceHealth.DEGRADED
                services.any { it.status == ServiceHealth.MAINTENANCE } -> ServiceHealth.MAINTENANCE
                else -> ServiceHealth.OPERATIONAL
            }
            
            val activeIncidents = incidents.filter { it.status != IncidentStatus.RESOLVED }
            
            val systemStatus = SystemStatus(
                overallStatus = overallStatus,
                services = services,
                activeIncidents = activeIncidents,
                lastUpdated = LocalDateTime.now()
            )
            
            Result.success(systemStatus)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getServiceStatus(serviceName: String): Result<ServiceStatus?> {
        return try {
            val service = services.find { it.serviceName.equals(serviceName, ignoreCase = true) }
            Result.success(service)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAllServiceStatuses(): Result<List<ServiceStatus>> {
        return try {
            Result.success(services)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getActiveIncidents(): Result<List<ServiceIncident>> {
        return try {
            val activeIncidents = incidents.filter { it.status != IncidentStatus.RESOLVED }
                .sortedByDescending { it.startTime }
            Result.success(activeIncidents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getIncident(incidentId: String): Result<ServiceIncident?> {
        return try {
            val incident = incidents.find { it.id == incidentId }
            Result.success(incident)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getIncidentHistory(): Result<List<ServiceIncident>> {
        return try {
            val history = incidents.sortedByDescending { it.startTime }
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun subscribeToStatusUpdates(userId: String): Result<Unit> {
        return try {
            subscribers.add(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unsubscribeFromStatusUpdates(userId: String): Result<Unit> {
        return try {
            subscribers.remove(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
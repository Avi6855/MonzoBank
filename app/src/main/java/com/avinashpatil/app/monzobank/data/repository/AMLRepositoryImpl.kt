package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AMLRepositoryImpl @Inject constructor() : AMLRepository {
    
    private val alerts = mutableListOf<AMLAlert>()
    private val suspiciousActivities = mutableListOf<SuspiciousActivity>()
    private val profiles = mutableMapOf<String, AMLProfile>()
    
    override suspend fun createAlert(alert: AMLAlert): Result<String> {
        return try {
            alerts.add(alert)
            updateUserProfile(alert.userId)
            Result.success(alert.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAlerts(status: AlertStatus?): Result<List<AMLAlert>> {
        return try {
            val filteredAlerts = if (status != null) {
                alerts.filter { it.status == status }
            } else {
                alerts.toList()
            }
            Result.success(filteredAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateAlertStatus(alertId: String, status: AlertStatus, notes: String?): Result<Unit> {
        return try {
            val index = alerts.indexOfFirst { it.id == alertId }
            if (index != -1) {
                val updated = alerts[index].copy(
                    status = status,
                    resolvedAt = if (status == AlertStatus.RESOLVED || status == AlertStatus.FALSE_POSITIVE) 
                        LocalDateTime.now() else null,
                    notes = notes
                )
                alerts[index] = updated
                updateUserProfile(updated.userId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserAlerts(userId: String): Result<List<AMLAlert>> {
        return try {
            val userAlerts = alerts.filter { it.userId == userId }
            Result.success(userAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun reportSuspiciousActivity(activity: SuspiciousActivity): Result<String> {
        return try {
            suspiciousActivities.add(activity)
            
            // Create corresponding AML alert
            val alert = AMLAlert(
                id = UUID.randomUUID().toString(),
                transactionId = "txn_${activity.id}",
                userId = activity.userId,
                alertType = AMLAlertType.SUSPICIOUS_AMOUNT,
                riskScore = 75,
                status = AlertStatus.OPEN,
                createdAt = LocalDateTime.now(),
                resolvedAt = null,
                notes = activity.description
            )
            alerts.add(alert)
            updateUserProfile(activity.userId)
            
            Result.success(activity.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSuspiciousActivities(userId: String?): Result<List<SuspiciousActivity>> {
        return try {
            val activities = if (userId != null) {
                suspiciousActivities.filter { it.userId == userId }
            } else {
                suspiciousActivities.toList()
            }
            Result.success(activities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performAMLCheck(userId: String, transactionAmount: BigDecimal): Result<Int> {
        return try {
            var riskScore = 0
            
            // Check transaction amount
            when {
                transactionAmount >= BigDecimal("10000") -> riskScore += 50
                transactionAmount >= BigDecimal("5000") -> riskScore += 30
                transactionAmount >= BigDecimal("1000") -> riskScore += 10
            }
            
            // Check user history
            val userAlerts = alerts.filter { it.userId == userId }
            val openAlerts = userAlerts.count { it.status == AlertStatus.OPEN }
            riskScore += openAlerts * 20
            
            // Check frequency of transactions (mock)
            riskScore += 15
            
            // Create alert if risk score is high
            if (riskScore >= 70) {
                val alert = AMLAlert(
                    id = UUID.randomUUID().toString(),
                    transactionId = UUID.randomUUID().toString(),
                    userId = userId,
                    alertType = AMLAlertType.SUSPICIOUS_AMOUNT,
                    riskScore = riskScore,
                    status = AlertStatus.OPEN,
                    createdAt = LocalDateTime.now(),
                    resolvedAt = null,
                    notes = "High risk transaction detected"
                )
                alerts.add(alert)
                updateUserProfile(userId)
            }
            
            Result.success(riskScore)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getAMLProfile(userId: String): Result<AMLProfile> {
        return try {
            val profile = profiles[userId] ?: createDefaultProfile(userId)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateRiskRating(userId: String, rating: RiskRating): Result<Unit> {
        return try {
            val currentProfile = profiles[userId] ?: createDefaultProfile(userId)
            val updated = currentProfile.copy(
                riskRating = rating,
                lastAssessment = LocalDateTime.now(),
                isHighRisk = rating == RiskRating.HIGH || rating == RiskRating.VERY_HIGH
            )
            profiles[userId] = updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateSAR(activityId: String): Result<String> {
        return try {
            val activity = suspiciousActivities.find { it.id == activityId }
            if (activity != null) {
                val sarId = UUID.randomUUID().toString()
                // Mark as reported to authorities
                val index = suspiciousActivities.indexOfFirst { it.id == activityId }
                if (index != -1) {
                    val updated = activity.copy(reportedToAuthorities = true)
                    suspiciousActivities[index] = updated
                }
                Result.success(sarId)
            } else {
                Result.failure(Exception("Suspicious activity not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getHighRiskUsers(): Result<List<String>> {
        return try {
            val highRiskUsers = profiles.values
                .filter { it.isHighRisk }
                .map { it.userId }
            Result.success(highRiskUsers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performPeriodicReview(): Result<List<AMLAlert>> {
        return try {
            val openAlerts = alerts.filter { it.status == AlertStatus.OPEN }
            val oldAlerts = openAlerts.filter { 
                it.createdAt.isBefore(LocalDateTime.now().minusDays(30)) 
            }
            Result.success(oldAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createDefaultProfile(userId: String): AMLProfile {
        return AMLProfile(
            userId = userId,
            riskRating = RiskRating.LOW,
            lastAssessment = LocalDateTime.now(),
            totalAlerts = 0,
            resolvedAlerts = 0,
            isHighRisk = false
        )
    }
    
    private fun updateUserProfile(userId: String) {
        val userAlerts = alerts.filter { it.userId == userId }
        val resolvedAlerts = userAlerts.count { 
            it.status == AlertStatus.RESOLVED || it.status == AlertStatus.FALSE_POSITIVE 
        }
        
        val riskRating = when {
            userAlerts.count { it.status == AlertStatus.OPEN } >= 3 -> RiskRating.VERY_HIGH
            userAlerts.count { it.status == AlertStatus.OPEN } >= 2 -> RiskRating.HIGH
            userAlerts.count { it.status == AlertStatus.OPEN } >= 1 -> RiskRating.MEDIUM
            else -> RiskRating.LOW
        }
        
        val profile = AMLProfile(
            userId = userId,
            riskRating = riskRating,
            lastAssessment = LocalDateTime.now(),
            totalAlerts = userAlerts.size,
            resolvedAlerts = resolvedAlerts,
            isHighRisk = riskRating == RiskRating.HIGH || riskRating == RiskRating.VERY_HIGH
        )
        
        profiles[userId] = profile
    }
}
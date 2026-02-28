package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class AMLAlert(
    val id: String,
    val transactionId: String,
    val userId: String,
    val alertType: AMLAlertType,
    val riskScore: Int,
    val status: AlertStatus,
    val createdAt: LocalDateTime,
    val resolvedAt: LocalDateTime?,
    val notes: String?
)

enum class AMLAlertType {
    SUSPICIOUS_AMOUNT, UNUSUAL_PATTERN, HIGH_RISK_COUNTRY, SANCTIONS_MATCH, STRUCTURING
}

enum class AlertStatus {
    OPEN, INVESTIGATING, RESOLVED, FALSE_POSITIVE
}

data class SuspiciousActivity(
    val id: String,
    val userId: String,
    val activityType: String,
    val description: String,
    val amount: BigDecimal?,
    val detectedAt: LocalDateTime,
    val reportedToAuthorities: Boolean = false
)

data class AMLProfile(
    val userId: String,
    val riskRating: RiskRating,
    val lastAssessment: LocalDateTime,
    val totalAlerts: Int,
    val resolvedAlerts: Int,
    val isHighRisk: Boolean
)

enum class RiskRating {
    VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
}

interface AMLRepository {
    suspend fun createAlert(alert: AMLAlert): Result<String>
    suspend fun getAlerts(status: AlertStatus?): Result<List<AMLAlert>>
    suspend fun updateAlertStatus(alertId: String, status: AlertStatus, notes: String?): Result<Unit>
    suspend fun getUserAlerts(userId: String): Result<List<AMLAlert>>
    suspend fun reportSuspiciousActivity(activity: SuspiciousActivity): Result<String>
    suspend fun getSuspiciousActivities(userId: String?): Result<List<SuspiciousActivity>>
    suspend fun performAMLCheck(userId: String, transactionAmount: BigDecimal): Result<Int>
    suspend fun getAMLProfile(userId: String): Result<AMLProfile>
    suspend fun updateRiskRating(userId: String, rating: RiskRating): Result<Unit>
    suspend fun generateSAR(activityId: String): Result<String>
    suspend fun getHighRiskUsers(): Result<List<String>>
    suspend fun performPeriodicReview(): Result<List<AMLAlert>>
}
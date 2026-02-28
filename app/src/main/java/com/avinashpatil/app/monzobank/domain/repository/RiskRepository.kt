package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class RiskAssessment(
    val id: String,
    val userId: String,
    val riskScore: Int,
    val riskLevel: RiskLevel,
    val factors: List<RiskFactor>,
    val assessedAt: LocalDateTime,
    val validUntil: LocalDateTime,
    val assessedBy: String
)

data class RiskFactor(
    val factor: String,
    val weight: Double,
    val score: Int,
    val description: String
)

enum class RiskLevel {
    VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH, CRITICAL
}

data class RiskProfile(
    val userId: String,
    val currentRiskLevel: RiskLevel,
    val creditScore: Int?,
    val incomeLevel: IncomeLevel,
    val employmentStatus: EmploymentStatus,
    val accountAge: Int,
    val transactionHistory: TransactionRiskProfile,
    val lastUpdated: LocalDateTime
)

enum class IncomeLevel {
    VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH
}

enum class EmploymentStatus {
    UNEMPLOYED, PART_TIME, FULL_TIME, SELF_EMPLOYED, RETIRED, STUDENT
}

data class TransactionRiskProfile(
    val averageMonthlyVolume: BigDecimal,
    val largestTransaction: BigDecimal,
    val internationalTransactions: Int,
    val failedTransactions: Int,
    val suspiciousPatterns: Int
)

data class RiskAlert(
    val id: String,
    val userId: String,
    val alertType: RiskAlertType,
    val severity: RiskAlertSeverity,
    val message: String,
    val triggeredAt: LocalDateTime,
    val isResolved: Boolean = false
)

enum class RiskAlertType {
    CREDIT_LIMIT_EXCEEDED, UNUSUAL_SPENDING, HIGH_RISK_MERCHANT, FRAUD_INDICATOR, COMPLIANCE_VIOLATION
}

enum class RiskAlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

interface RiskRepository {
    suspend fun assessUserRisk(userId: String): Result<RiskAssessment>
    suspend fun getRiskProfile(userId: String): Result<RiskProfile>
    suspend fun updateRiskProfile(userId: String, profile: RiskProfile): Result<Unit>
    suspend fun createRiskAlert(alert: RiskAlert): Result<String>
    suspend fun getRiskAlerts(userId: String, isResolved: Boolean?): Result<List<RiskAlert>>
    suspend fun resolveRiskAlert(alertId: String): Result<Unit>
    suspend fun calculateTransactionRisk(userId: String, amount: BigDecimal, merchantType: String): Result<Int>
    suspend fun getHighRiskUsers(): Result<List<String>>
    suspend fun performPeriodicRiskReview(): Result<List<RiskAssessment>>
    suspend fun updateRiskFactors(factors: List<RiskFactor>): Result<Unit>
    suspend fun getRiskTrends(userId: String, days: Int): Result<List<RiskAssessment>>
    suspend fun generateRiskReport(userId: String): Result<String>
}
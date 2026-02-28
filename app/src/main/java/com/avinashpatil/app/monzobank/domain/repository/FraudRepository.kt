package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.FraudRiskLevel

/**
 * Repository interface for fraud detection operations
 */
interface FraudRepository {
    
    /**
     * Analyze transaction for fraud
     */
    suspend fun analyzeTransaction(transactionId: String): Result<FraudAnalysis>
    
    /**
     * Report fraud
     */
    suspend fun reportFraud(transactionId: String, reason: String): Result<Unit>
    
    /**
     * Get fraud alerts
     */
    suspend fun getFraudAlerts(userId: String): Result<List<FraudAlert>>
    
    /**
     * Update fraud rules
     */
    suspend fun updateFraudRules(rules: List<FraudRule>): Result<Unit>
}

data class FraudAnalysis(
    val transactionId: String,
    val riskLevel: FraudRiskLevel,
    val riskScore: Double,
    val reasons: List<String>
)

data class FraudAlert(
    val id: String,
    val transactionId: String,
    val riskLevel: FraudRiskLevel,
    val message: String
)

data class FraudRule(
    val id: String,
    val name: String,
    val condition: String,
    val action: String,
    val isActive: Boolean
)
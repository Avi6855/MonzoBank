package com.avinashpatil.app.monzobank.data.service

import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.domain.model.FraudAlert
import com.avinashpatil.app.monzobank.domain.model.FraudRiskLevel
import com.avinashpatil.app.monzobank.domain.model.FraudRule
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface FraudDetectionService {
    suspend fun analyzeTransaction(transaction: Transaction): FraudAnalysisResult
    suspend fun createFraudAlert(alert: FraudAlert): Result<String>
    suspend fun getFraudAlerts(userId: String): Flow<List<FraudAlert>>
    suspend fun updateFraudRules(rules: List<FraudRule>): Result<Unit>
    suspend fun blockSuspiciousTransaction(transactionId: String, reason: String): Result<Unit>
    suspend fun whitelistMerchant(userId: String, merchantId: String): Result<Unit>
    suspend fun getTransactionRiskScore(transaction: Transaction): Double
    suspend fun validateDeviceFingerprint(deviceId: String, userId: String): Boolean
    suspend fun checkVelocityLimits(userId: String, amount: BigDecimal, timeWindow: Int): Boolean
    suspend fun detectAnomalousSpending(userId: String, transaction: Transaction): Boolean
}

class FraudDetectionServiceImpl : FraudDetectionService {
    
    companion object {
        private const val HIGH_RISK_THRESHOLD = 0.7
        private const val MEDIUM_RISK_THRESHOLD = 0.4
        private const val MAX_DAILY_TRANSACTIONS = 50
        private const val MAX_HOURLY_AMOUNT = 1000.0
        private const val SUSPICIOUS_LOCATION_RADIUS_KM = 100
    }
    
    // Mock data for demonstration
    private val fraudRules = mutableListOf(
        FraudRule(
            id = "rule_1",
            name = "High Amount Transaction",
            description = "Transactions over £500",
            condition = "amount > 500",
            riskScore = 0.6,
            isActive = true
        ),
        FraudRule(
            id = "rule_2",
            name = "Multiple Transactions",
            description = "More than 10 transactions in 1 hour",
            condition = "count > 10 AND timeWindow < 1h",
            riskScore = 0.8,
            isActive = true
        ),
        FraudRule(
            id = "rule_3",
            name = "Unusual Location",
            description = "Transaction from unusual location",
            condition = "location != usual",
            riskScore = 0.5,
            isActive = true
        )
    )
    
    private val whitelistedMerchants = mutableSetOf<String>()
    private val deviceFingerprints = mutableMapOf<String, Set<String>>()
    private val userTransactionHistory = mutableMapOf<String, MutableList<Transaction>>()
    
    override suspend fun analyzeTransaction(transaction: Transaction): FraudAnalysisResult {
        return try {
            val riskScore = calculateRiskScore(transaction)
            val riskLevel = determineRiskLevel(riskScore)
            val triggeredRules = getTriggeredRules(transaction)
            val recommendations = generateRecommendations(riskLevel, triggeredRules)
            
            FraudAnalysisResult(
                transactionId = transaction.id,
                riskScore = riskScore,
                riskLevel = riskLevel,
                triggeredRules = triggeredRules,
                recommendations = recommendations,
                shouldBlock = riskScore > HIGH_RISK_THRESHOLD,
                requiresManualReview = riskScore > MEDIUM_RISK_THRESHOLD,
                analysisTimestamp = LocalDateTime.now()
            )
        } catch (e: Exception) {
            FraudAnalysisResult(
                transactionId = transaction.id,
                riskScore = 0.0,
                riskLevel = FraudRiskLevel.LOW,
                triggeredRules = emptyList(),
                recommendations = listOf("Analysis failed: ${e.message}"),
                shouldBlock = false,
                requiresManualReview = false,
                analysisTimestamp = LocalDateTime.now()
            )
        }
    }
    
    override suspend fun createFraudAlert(alert: FraudAlert): Result<String> {
        return try {
            // Mock implementation - in real app, save to database
            val alertId = "alert_${System.currentTimeMillis()}"
            Result.success(alertId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getFraudAlerts(userId: String): Flow<List<FraudAlert>> {
        return kotlinx.coroutines.flow.flow {
            // Mock fraud alerts
            val alerts = listOf(
                FraudAlert(
                    id = "alert_1",
                    userId = userId,
                    transactionId = "txn_suspicious_1",
                    riskLevel = FraudRiskLevel.HIGH,
                    message = "Unusual spending pattern detected",
                    timestamp = LocalDateTime.now().minusHours(2),
                    isResolved = false,
                    actionTaken = null
                ),
                FraudAlert(
                    id = "alert_2",
                    userId = userId,
                    transactionId = "txn_suspicious_2",
                    riskLevel = FraudRiskLevel.MEDIUM,
                    message = "Transaction from new location",
                    timestamp = LocalDateTime.now().minusDays(1),
                    isResolved = true,
                    actionTaken = "User confirmed transaction"
                )
            )
            emit(alerts)
        }
    }
    
    override suspend fun updateFraudRules(rules: List<FraudRule>): Result<Unit> {
        return try {
            fraudRules.clear()
            fraudRules.addAll(rules)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun blockSuspiciousTransaction(
        transactionId: String,
        reason: String
    ): Result<Unit> {
        return try {
            // Mock implementation - in real app, update transaction status
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun whitelistMerchant(userId: String, merchantId: String): Result<Unit> {
        return try {
            whitelistedMerchants.add("${userId}_$merchantId")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getTransactionRiskScore(transaction: Transaction): Double {
        var riskScore = 0.0
        
        // Amount-based risk
        riskScore += when {
            transaction.amount > BigDecimal(1000) -> 0.4
            transaction.amount > BigDecimal(500) -> 0.2
            transaction.amount > BigDecimal(100) -> 0.1
            else -> 0.0
        }
        
        // Time-based risk (late night transactions)
        val hour = LocalDateTime.now().hour
        if (hour < 6 || hour > 23) {
            riskScore += 0.2
        }
        
        // Frequency-based risk
        val recentTransactions = getUserRecentTransactions(transaction.accountId, 1)
        if (recentTransactions.size > 10) {
            riskScore += 0.3
        }
        
        // Merchant whitelist check
        val merchantKey = "${transaction.accountId}_${transaction.merchant?.id ?: "unknown"}"
        if (transaction.merchant?.id != null && !whitelistedMerchants.contains(merchantKey)) {
            riskScore += 0.1
        }
        
        return minOf(riskScore, 1.0)
    }
    
    override suspend fun validateDeviceFingerprint(deviceId: String, userId: String): Boolean {
        val userDevices = deviceFingerprints[userId] ?: emptySet()
        return userDevices.contains(deviceId)
    }
    
    override suspend fun checkVelocityLimits(
        userId: String,
        amount: BigDecimal,
        timeWindow: Int
    ): Boolean {
        val recentTransactions = getUserRecentTransactions(userId, timeWindow)
        val totalAmount = recentTransactions.sumOf { it.amount }
        
        return when (timeWindow) {
            1 -> { // 1 hour
                recentTransactions.size <= 20 && totalAmount <= BigDecimal(MAX_HOURLY_AMOUNT)
            }
            24 -> { // 24 hours
                recentTransactions.size <= MAX_DAILY_TRANSACTIONS && totalAmount <= BigDecimal(5000)
            }
            else -> true
        }
    }
    
    override suspend fun detectAnomalousSpending(
        userId: String,
        transaction: Transaction
    ): Boolean {
        val historicalTransactions = getUserTransactionHistory(userId)
        if (historicalTransactions.isEmpty()) return false
        
        val averageAmount = historicalTransactions.map { it.amount.toDouble() }.average()
        val standardDeviation = calculateStandardDeviation(
            historicalTransactions.map { it.amount.toDouble() }
        )
        
        // Transaction is anomalous if it's more than 2 standard deviations from the mean
        val threshold = averageAmount + (2 * standardDeviation)
        return transaction.amount.toDouble() > threshold
    }
    
    private fun calculateRiskScore(transaction: Transaction): Double {
        var score = 0.0
        
        // Apply each active fraud rule
        fraudRules.filter { it.isActive }.forEach { rule ->
            if (evaluateRule(rule, transaction)) {
                score += rule.riskScore
            }
        }
        
        // Additional risk factors
        score += getAmountRisk(transaction.amount)
        score += getTimeRisk()
        score += getFrequencyRisk(transaction.accountId)
        score += getLocationRisk(transaction)
        
        return minOf(score, 1.0)
    }
    
    private fun determineRiskLevel(riskScore: Double): FraudRiskLevel {
        return when {
            riskScore >= HIGH_RISK_THRESHOLD -> FraudRiskLevel.HIGH
            riskScore >= MEDIUM_RISK_THRESHOLD -> FraudRiskLevel.MEDIUM
            else -> FraudRiskLevel.LOW
        }
    }
    
    private fun getTriggeredRules(transaction: Transaction): List<FraudRule> {
        return fraudRules.filter { rule ->
            rule.isActive && evaluateRule(rule, transaction)
        }
    }
    
    private fun evaluateRule(rule: FraudRule, transaction: Transaction): Boolean {
        // Simplified rule evaluation - in real implementation, use a proper rule engine
        return when (rule.id) {
            "rule_1" -> transaction.amount > BigDecimal(500)
            "rule_2" -> {
                val recentCount = getUserRecentTransactions(transaction.accountId, 1).size
                recentCount > 10
            }
            "rule_3" -> {
                // Mock location check
                transaction.location?.let { location ->
                    // Simulate unusual location detection
                    val address = location.address ?: ""
                    val city = location.city ?: ""
                    val country = location.country ?: ""
                    address.contains("Unknown") || city.contains("Unknown") || 
                    country.contains("Foreign") || country != "UK"
                } ?: false
            }
            else -> false
        }
    }
    
    private fun generateRecommendations(
        riskLevel: FraudRiskLevel,
        triggeredRules: List<FraudRule>
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        when (riskLevel) {
            FraudRiskLevel.HIGH -> {
                recommendations.add("Block transaction immediately")
                recommendations.add("Send fraud alert to user")
                recommendations.add("Require additional authentication")
            }
            FraudRiskLevel.MEDIUM -> {
                recommendations.add("Flag for manual review")
                recommendations.add("Send notification to user")
                recommendations.add("Monitor subsequent transactions")
            }
            FraudRiskLevel.LOW -> {
                recommendations.add("Allow transaction")
                recommendations.add("Log for analysis")
            }
        }
        
        // Add specific recommendations based on triggered rules
        triggeredRules.forEach { rule ->
            when (rule.id) {
                "rule_1" -> recommendations.add("Verify high-amount transaction with user")
                "rule_2" -> recommendations.add("Implement velocity controls")
                "rule_3" -> recommendations.add("Verify location with user")
            }
        }
        
        return recommendations.distinct()
    }
    
    private fun getAmountRisk(amount: BigDecimal): Double {
        return when {
            amount > BigDecimal(2000) -> 0.5
            amount > BigDecimal(1000) -> 0.3
            amount > BigDecimal(500) -> 0.2
            amount > BigDecimal(100) -> 0.1
            else -> 0.0
        }
    }
    
    private fun getTimeRisk(): Double {
        val hour = LocalDateTime.now().hour
        return when {
            hour < 6 || hour > 22 -> 0.3 // Late night/early morning
            hour in 9..17 -> 0.0 // Business hours
            else -> 0.1 // Evening
        }
    }
    
    private fun getFrequencyRisk(accountId: String): Double {
        val recentTransactions = getUserRecentTransactions(accountId, 1)
        return when {
            recentTransactions.size > 20 -> 0.4
            recentTransactions.size > 10 -> 0.2
            recentTransactions.size > 5 -> 0.1
            else -> 0.0
        }
    }
    
    private fun getLocationRisk(transaction: Transaction): Double {
        // Mock location risk assessment
        return transaction.location?.let { location ->
            val address = location.address ?: ""
            val city = location.city ?: ""
            val country = location.country ?: ""
            when {
                country.contains("Foreign") || country != "UK" -> 0.4
                address.contains("Unknown") || city.contains("Unknown") -> 0.3
                address.contains("ATM") -> 0.1
                else -> 0.0
            }
        } ?: 0.1
    }
    
    private fun getUserRecentTransactions(accountId: String, hoursBack: Int): List<Transaction> {
        val cutoffTime = LocalDateTime.now().minusHours(hoursBack.toLong())
        return userTransactionHistory[accountId]?.filter { transaction ->
            // Convert Date to LocalDateTime for comparison
            val transactionTime = transaction.createdAt.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime()
            transactionTime.isAfter(cutoffTime)
        } ?: emptyList()
    }
    
    private fun getUserTransactionHistory(userId: String): List<Transaction> {
        return userTransactionHistory[userId] ?: emptyList()
    }
    
    private fun calculateStandardDeviation(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }
}

data class FraudAnalysisResult(
    val transactionId: String,
    val riskScore: Double,
    val riskLevel: FraudRiskLevel,
    val triggeredRules: List<FraudRule>,
    val recommendations: List<String>,
    val shouldBlock: Boolean,
    val requiresManualReview: Boolean,
    val analysisTimestamp: LocalDateTime
)

// PCI DSS Compliance utilities
object PCIDSSUtils {
    
    // Mask sensitive data for logging
    fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 4) return "****"
        return "****" + cardNumber.takeLast(4)
    }
    
    fun maskAccountNumber(accountNumber: String): String {
        if (accountNumber.length < 4) return "****"
        return "****" + accountNumber.takeLast(4)
    }
    
    // Validate data encryption requirements
    fun validateEncryption(data: String): Boolean {
        // Mock validation - in real implementation, check encryption standards
        return data.startsWith("ENC_") || data.startsWith("HASH_")
    }
    
    // Generate secure audit log entry
    fun createAuditLogEntry(
        userId: String,
        action: String,
        resource: String,
        timestamp: LocalDateTime = LocalDateTime.now()
    ): String {
        return "[${timestamp}] User: ${maskUserId(userId)} Action: $action Resource: $resource"
    }
    
    private fun maskUserId(userId: String): String {
        if (userId.length < 4) return "****"
        return userId.take(2) + "****" + userId.takeLast(2)
    }
    
    // Validate password strength for PCI DSS compliance
    fun validatePasswordStrength(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }
    
    // Generate secure session token
    fun generateSecureToken(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..32)
            .map { chars.random() }
            .joinToString("")
    }
}
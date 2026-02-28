package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor() : CreditRepository {
    
    private val applications = mutableListOf<CreditApplication>()
    private val creditScores = mutableMapOf<String, CreditScore>()
    private val creditLimits = mutableMapOf<String, MutableList<CreditLimit>>()
    private val creditHistories = mutableMapOf<String, CreditHistory>()
    private val paymentRecords = mutableListOf<PaymentRecord>()
    
    override suspend fun submitCreditApplication(application: CreditApplication): Result<String> {
        return try {
            applications.add(application)
            Result.success(application.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCreditApplications(userId: String): Result<List<CreditApplication>> {
        return try {
            val userApplications = applications.filter { it.userId == userId }
            Result.success(userApplications)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateApplicationStatus(applicationId: String, status: CreditApplicationStatus): Result<Unit> {
        return try {
            val index = applications.indexOfFirst { it.id == applicationId }
            if (index != -1) {
                val updated = applications[index].copy(
                    status = status,
                    processedAt = LocalDateTime.now(),
                    approvedAmount = if (status == CreditApplicationStatus.APPROVED) 
                        applications[index].requestedAmount else null,
                    interestRate = if (status == CreditApplicationStatus.APPROVED) 
                        BigDecimal("12.5") else null,
                    termMonths = if (status == CreditApplicationStatus.APPROVED) 36 else null
                )
                applications[index] = updated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Application not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCreditScore(userId: String): Result<CreditScore> {
        return try {
            val score = creditScores[userId] ?: createDefaultCreditScore(userId)
            Result.success(score)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCreditScore(userId: String, score: CreditScore): Result<Unit> {
        return try {
            creditScores[userId] = score
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCreditLimits(userId: String): Result<List<CreditLimit>> {
        return try {
            val limits = creditLimits[userId] ?: mutableListOf()
            if (limits.isEmpty()) {
                // Create default credit limits
                val defaultLimits = listOf(
                    CreditLimit(
                        userId = userId,
                        creditType = CreditType.CREDIT_CARD,
                        currentLimit = BigDecimal("5000"),
                        availableCredit = BigDecimal("4500"),
                        utilizationRate = 0.1,
                        lastUpdated = LocalDateTime.now()
                    )
                )
                creditLimits[userId] = defaultLimits.toMutableList()
                Result.success(defaultLimits)
            } else {
                Result.success(limits.toList())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCreditLimit(userId: String, creditType: CreditType, newLimit: BigDecimal): Result<Unit> {
        return try {
            val userLimits = creditLimits.getOrPut(userId) { mutableListOf() }
            val index = userLimits.indexOfFirst { it.creditType == creditType }
            
            if (index != -1) {
                val existing = userLimits[index]
                val updated = existing.copy(
                    currentLimit = newLimit,
                    availableCredit = newLimit - (existing.currentLimit - existing.availableCredit),
                    lastUpdated = LocalDateTime.now()
                )
                userLimits[index] = updated
            } else {
                val newCreditLimit = CreditLimit(
                    userId = userId,
                    creditType = creditType,
                    currentLimit = newLimit,
                    availableCredit = newLimit,
                    utilizationRate = 0.0,
                    lastUpdated = LocalDateTime.now()
                )
                userLimits.add(newCreditLimit)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCreditHistory(userId: String): Result<CreditHistory> {
        return try {
            val history = creditHistories[userId] ?: createDefaultCreditHistory(userId)
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addPaymentRecord(record: PaymentRecord): Result<Unit> {
        return try {
            paymentRecords.add(record)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun performCreditCheck(userId: String): Result<CreditScore> {
        return try {
            // Simulate credit check by analyzing payment history and utilization
            val userPayments = paymentRecords.filter { record ->
                // In real implementation, would link payments to user accounts
                true // Mock filter
            }
            
            val onTimePayments = userPayments.count { it.status == CreditPaymentStatus.ON_TIME }
            val totalPayments = userPayments.size
            val paymentRatio = if (totalPayments > 0) onTimePayments.toDouble() / totalPayments else 1.0
            
            // Calculate utilization
            val utilization = calculateCreditUtilization(userId).getOrNull() ?: 0.0
            
            // Calculate score based on factors
            var score = 650 // Base score
            score += (paymentRatio * 150).toInt() // Payment history impact
            score -= (utilization * 200).toInt() // Utilization impact
            score = score.coerceIn(300, 850)
            
            val factors = listOf(
                CreditFactor(
                    "Payment History", 
                    if (paymentRatio > 0.95) CreditImpact.VERY_POSITIVE else CreditImpact.NEUTRAL,
                    "${(paymentRatio * 100).toInt()}% on-time payments"
                ),
                CreditFactor(
                    "Credit Utilization", 
                    when {
                        utilization < 0.1 -> CreditImpact.VERY_POSITIVE
                        utilization < 0.3 -> CreditImpact.POSITIVE
                        utilization < 0.5 -> CreditImpact.NEUTRAL
                        utilization < 0.7 -> CreditImpact.NEGATIVE
                        else -> CreditImpact.VERY_NEGATIVE
                    },
                    "${(utilization * 100).toInt()}% utilization"
                )
            )
            
            val creditScore = CreditScore(
                userId = userId,
                score = score,
                provider = "Internal Credit Bureau",
                lastUpdated = LocalDateTime.now(),
                factors = factors
            )
            
            creditScores[userId] = creditScore
            Result.success(creditScore)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateCreditUtilization(userId: String): Result<Double> {
        return try {
            val limits = getCreditLimits(userId).getOrNull() ?: emptyList()
            if (limits.isEmpty()) {
                Result.success(0.0)
            } else {
                val totalLimit = limits.sumOf { it.currentLimit }
                val totalUsed = limits.sumOf { it.currentLimit - it.availableCredit }
                val utilization = if (totalLimit > BigDecimal.ZERO) {
                    totalUsed.divide(totalLimit, 4, java.math.RoundingMode.HALF_UP).toDouble()
                } else {
                    0.0
                }
                Result.success(utilization)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPrequalifiedOffers(userId: String): Result<List<CreditApplication>> {
        return try {
            val creditScore = getCreditScore(userId).getOrNull()?.score ?: 650
            val offers = mutableListOf<CreditApplication>()
            
            // Generate offers based on credit score
            if (creditScore >= 700) {
                offers.add(
                    CreditApplication(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        creditType = CreditType.CREDIT_CARD,
                        requestedAmount = BigDecimal("10000"),
                        purpose = "Premium Credit Card Offer",
                        status = CreditApplicationStatus.SUBMITTED,
                        submittedAt = LocalDateTime.now(),
                        processedAt = null,
                        approvedAmount = null,
                        interestRate = BigDecimal("9.99"),
                        termMonths = null
                    )
                )
            }
            
            if (creditScore >= 650) {
                offers.add(
                    CreditApplication(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        creditType = CreditType.PERSONAL_LOAN,
                        requestedAmount = BigDecimal("15000"),
                        purpose = "Personal Loan Offer",
                        status = CreditApplicationStatus.SUBMITTED,
                        submittedAt = LocalDateTime.now(),
                        processedAt = null,
                        approvedAmount = null,
                        interestRate = BigDecimal("12.99"),
                        termMonths = 36
                    )
                )
            }
            
            Result.success(offers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createDefaultCreditScore(userId: String): CreditScore {
        return CreditScore(
            userId = userId,
            score = 650,
            provider = "Internal Credit Bureau",
            lastUpdated = LocalDateTime.now(),
            factors = listOf(
                CreditFactor("Payment History", CreditImpact.NEUTRAL, "Limited history"),
                CreditFactor("Credit Utilization", CreditImpact.POSITIVE, "Low utilization"),
                CreditFactor("Credit Age", CreditImpact.NEUTRAL, "Average age")
            )
        )
    }
    
    private fun createDefaultCreditHistory(userId: String): CreditHistory {
        return CreditHistory(
            userId = userId,
            accounts = listOf(
                CreditAccount(
                    accountId = UUID.randomUUID().toString(),
                    accountType = CreditType.CREDIT_CARD,
                    balance = BigDecimal("500"),
                    limit = BigDecimal("5000"),
                    openedDate = LocalDateTime.now().minusYears(2),
                    status = CreditAccountStatus.ACTIVE
                )
            ),
            paymentHistory = emptyList(),
            inquiries = emptyList()
        )
    }
}
package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class SavingsAccount(
    val id: String,
    val userId: String,
    val accountName: String,
    val accountNumber: String,
    val balance: BigDecimal,
    val interestRate: BigDecimal,
    val accountType: SavingsAccountType,
    val minimumBalance: BigDecimal,
    val status: AccountStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class SavingsGoal(
    val id: String,
    val userId: String,
    val accountId: String,
    val goalName: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val targetDate: LocalDateTime,
    val monthlyContribution: BigDecimal,
    val status: SavingsGoalStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class InterestPayment(
    val id: String,
    val accountId: String,
    val amount: BigDecimal,
    val interestRate: BigDecimal,
    val paymentDate: LocalDateTime,
    val period: String
)

enum class SavingsAccountType {
    REGULAR_SAVINGS,
    HIGH_YIELD_SAVINGS,
    MONEY_MARKET,
    CERTIFICATE_OF_DEPOSIT,
    RETIREMENT_SAVINGS,
    EMERGENCY_FUND
}

enum class AccountStatus {
    ACTIVE,
    INACTIVE,
    CLOSED,
    FROZEN
}

enum class SavingsGoalStatus {
    ACTIVE,
    COMPLETED,
    PAUSED,
    CANCELLED
}

interface SavingsRepository {
    suspend fun getSavingsAccounts(userId: String): Result<List<SavingsAccount>>
    suspend fun getSavingsAccount(accountId: String): Result<SavingsAccount?>
    suspend fun createSavingsAccount(account: SavingsAccount): Result<String>
    suspend fun updateSavingsAccount(account: SavingsAccount): Result<Unit>
    suspend fun closeSavingsAccount(accountId: String): Result<Unit>
    
    suspend fun getSavingsGoals(userId: String): Result<List<SavingsGoal>>
    suspend fun getSavingsGoal(goalId: String): Result<SavingsGoal?>
    suspend fun createSavingsGoal(goal: SavingsGoal): Result<String>
    suspend fun updateSavingsGoal(goal: SavingsGoal): Result<Unit>
    suspend fun deleteSavingsGoal(goalId: String): Result<Unit>
    
    suspend fun calculateInterest(accountId: String): Result<BigDecimal>
    suspend fun getInterestHistory(accountId: String): Result<List<InterestPayment>>
    suspend fun processInterestPayments(): Result<List<InterestPayment>>
    
    suspend fun getRecommendedSavingsAmount(userId: String): Result<BigDecimal>
    suspend fun getSavingsInsights(userId: String): Result<Map<String, Any>>
}
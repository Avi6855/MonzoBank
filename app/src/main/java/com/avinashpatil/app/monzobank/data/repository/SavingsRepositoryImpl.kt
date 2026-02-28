package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingsRepositoryImpl @Inject constructor() : SavingsRepository {
    
    private val savingsAccounts = mutableListOf<SavingsAccount>()
    private val savingsGoals = mutableListOf<SavingsGoal>()
    private val interestPayments = mutableListOf<InterestPayment>()
    
    override suspend fun getSavingsAccounts(userId: String): Result<List<SavingsAccount>> {
        return try {
            val userAccounts = savingsAccounts.filter { it.userId == userId }
            Result.success(userAccounts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSavingsAccount(accountId: String): Result<SavingsAccount?> {
        return try {
            val account = savingsAccounts.find { it.id == accountId }
            Result.success(account)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createSavingsAccount(account: SavingsAccount): Result<String> {
        return try {
            savingsAccounts.add(account)
            Result.success(account.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSavingsAccount(account: SavingsAccount): Result<Unit> {
        return try {
            val index = savingsAccounts.indexOfFirst { it.id == account.id }
            if (index != -1) {
                savingsAccounts[index] = account
                Result.success(Unit)
            } else {
                Result.failure(Exception("Savings account not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun closeSavingsAccount(accountId: String): Result<Unit> {
        return try {
            val index = savingsAccounts.indexOfFirst { it.id == accountId }
            if (index != -1) {
                val account = savingsAccounts[index]
                val closedAccount = account.copy(
                    status = AccountStatus.CLOSED,
                    updatedAt = LocalDateTime.now()
                )
                savingsAccounts[index] = closedAccount
                Result.success(Unit)
            } else {
                Result.failure(Exception("Savings account not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSavingsGoals(userId: String): Result<List<SavingsGoal>> {
        return try {
            val userGoals = savingsGoals.filter { it.userId == userId }
            Result.success(userGoals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSavingsGoal(goalId: String): Result<SavingsGoal?> {
        return try {
            val goal = savingsGoals.find { it.id == goalId }
            Result.success(goal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createSavingsGoal(goal: SavingsGoal): Result<String> {
        return try {
            savingsGoals.add(goal)
            Result.success(goal.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateSavingsGoal(goal: SavingsGoal): Result<Unit> {
        return try {
            val index = savingsGoals.indexOfFirst { it.id == goal.id }
            if (index != -1) {
                val updatedGoal = goal.copy(
                    status = if (goal.currentAmount >= goal.targetAmount) SavingsGoalStatus.COMPLETED else goal.status,
                    updatedAt = LocalDateTime.now()
                )
                savingsGoals[index] = updatedGoal
                Result.success(Unit)
            } else {
                Result.failure(Exception("Savings goal not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteSavingsGoal(goalId: String): Result<Unit> {
        return try {
            val removed = savingsGoals.removeIf { it.id == goalId }
            if (removed) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Savings goal not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateInterest(accountId: String): Result<BigDecimal> {
        return try {
            val account = savingsAccounts.find { it.id == accountId }
                ?: return Result.failure(Exception("Account not found"))
            
            // Calculate monthly interest
            val monthlyRate = account.interestRate.divide(BigDecimal("12"), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal("100"), 10, RoundingMode.HALF_UP)
            
            val interestAmount = account.balance.multiply(monthlyRate)
                .setScale(2, RoundingMode.HALF_UP)
            
            Result.success(interestAmount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getInterestHistory(accountId: String): Result<List<InterestPayment>> {
        return try {
            val accountInterestPayments = interestPayments.filter { it.accountId == accountId }
                .sortedByDescending { it.paymentDate }
            Result.success(accountInterestPayments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processInterestPayments(): Result<List<InterestPayment>> {
        return try {
            val processedPayments = mutableListOf<InterestPayment>()
            
            savingsAccounts.filter { it.status == AccountStatus.ACTIVE }.forEach { account ->
                val interestAmount = calculateInterest(account.id).getOrNull()
                if (interestAmount != null && interestAmount > BigDecimal.ZERO) {
                    val payment = InterestPayment(
                        id = UUID.randomUUID().toString(),
                        accountId = account.id,
                        amount = interestAmount,
                        interestRate = account.interestRate,
                        paymentDate = LocalDateTime.now(),
                        period = "${LocalDateTime.now().month} ${LocalDateTime.now().year}"
                    )
                    
                    interestPayments.add(payment)
                    processedPayments.add(payment)
                    
                    // Update account balance
                    val updatedAccount = account.copy(
                        balance = account.balance.add(interestAmount),
                        updatedAt = LocalDateTime.now()
                    )
                    val index = savingsAccounts.indexOfFirst { it.id == account.id }
                    if (index != -1) {
                        savingsAccounts[index] = updatedAccount
                    }
                }
            }
            
            Result.success(processedPayments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecommendedSavingsAmount(userId: String): Result<BigDecimal> {
        return try {
            // Mock recommendation based on user's total balance
            val userAccounts = savingsAccounts.filter { it.userId == userId }
            val totalBalance = userAccounts.sumOf { it.balance }
            
            // Recommend 20% of current balance as additional savings
            val recommendedAmount = totalBalance.multiply(BigDecimal("0.20"))
                .setScale(2, RoundingMode.HALF_UP)
            
            Result.success(recommendedAmount.max(BigDecimal("100")))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSavingsInsights(userId: String): Result<Map<String, Any>> {
        return try {
            val userAccounts = savingsAccounts.filter { it.userId == userId }
            val userGoals = savingsGoals.filter { it.userId == userId }
            
            val totalBalance = userAccounts.sumOf { it.balance }
            val totalGoalAmount = userGoals.sumOf { it.targetAmount }
            val totalCurrentGoalAmount = userGoals.sumOf { it.currentAmount }
            val completedGoals = userGoals.count { it.status == SavingsGoalStatus.COMPLETED }
            val activeGoals = userGoals.count { it.status == SavingsGoalStatus.ACTIVE }
            
            val averageInterestRate = if (userAccounts.isNotEmpty()) {
                userAccounts.map { it.interestRate.toDouble() }.average()
            } else {
                0.0
            }
            
            val goalProgress = if (totalGoalAmount > BigDecimal.ZERO) {
                totalCurrentGoalAmount.divide(totalGoalAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal("100"))
            } else {
                BigDecimal.ZERO
            }
            
            val insights = mapOf(
                "totalBalance" to totalBalance,
                "totalAccounts" to userAccounts.size,
                "averageInterestRate" to averageInterestRate,
                "totalGoals" to userGoals.size,
                "completedGoals" to completedGoals,
                "activeGoals" to activeGoals,
                "goalProgress" to goalProgress,
                "monthlyInterestEarnings" to userAccounts.sumOf { 
                    calculateInterest(it.id).getOrNull() ?: BigDecimal.ZERO 
                }
            )
            
            Result.success(insights)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
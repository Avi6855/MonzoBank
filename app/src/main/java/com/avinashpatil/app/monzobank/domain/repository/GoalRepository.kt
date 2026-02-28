package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Goal(
    val id: String,
    val userId: String,
    val name: String,
    val description: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val targetDate: LocalDateTime,
    val goalType: GoalType,
    val priority: GoalPriority,
    val status: GoalStatus,
    val autoSaveEnabled: Boolean = false,
    val autoSaveAmount: BigDecimal = BigDecimal.ZERO,
    val linkedAccountId: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class GoalContribution(
    val id: String,
    val goalId: String,
    val amount: BigDecimal,
    val contributionDate: LocalDateTime,
    val contributionType: ContributionType,
    val notes: String? = null
)

data class GoalMilestone(
    val id: String,
    val goalId: String,
    val milestoneAmount: BigDecimal,
    val achievedDate: LocalDateTime?,
    val description: String,
    val isAchieved: Boolean = false
)

enum class GoalType {
    SAVINGS,
    EMERGENCY_FUND,
    VACATION,
    HOME_PURCHASE,
    CAR_PURCHASE,
    EDUCATION,
    RETIREMENT,
    DEBT_PAYOFF,
    INVESTMENT,
    OTHER
}

enum class GoalPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class GoalStatus {
    ACTIVE,
    COMPLETED,
    PAUSED,
    CANCELLED,
    OVERDUE
}

enum class ContributionType {
    MANUAL,
    AUTOMATIC,
    BONUS,
    TRANSFER
}

interface GoalRepository {
    suspend fun getGoals(userId: String): Result<List<Goal>>
    suspend fun getGoal(goalId: String): Result<Goal?>
    suspend fun createGoal(goal: Goal): Result<String>
    suspend fun updateGoal(goal: Goal): Result<Unit>
    suspend fun deleteGoal(goalId: String): Result<Unit>
    
    suspend fun addContribution(contribution: GoalContribution): Result<String>
    suspend fun getContributions(goalId: String): Result<List<GoalContribution>>
    suspend fun getContributionHistory(userId: String): Result<List<GoalContribution>>
    
    suspend fun getMilestones(goalId: String): Result<List<GoalMilestone>>
    suspend fun createMilestone(milestone: GoalMilestone): Result<String>
    suspend fun updateMilestone(milestone: GoalMilestone): Result<Unit>
    
    suspend fun calculateProgress(goalId: String): Result<BigDecimal>
    suspend fun getGoalInsights(userId: String): Result<Map<String, Any>>
    suspend fun getRecommendedContribution(goalId: String): Result<BigDecimal>
    suspend fun processAutoSave(userId: String): Result<List<GoalContribution>>
}
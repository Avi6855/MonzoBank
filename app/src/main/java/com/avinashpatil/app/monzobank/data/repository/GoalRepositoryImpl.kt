package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor() : GoalRepository {
    
    private val goals = mutableListOf<Goal>()
    private val contributions = mutableListOf<GoalContribution>()
    private val milestones = mutableListOf<GoalMilestone>()
    
    override suspend fun getGoals(userId: String): Result<List<Goal>> {
        return try {
            val userGoals = goals.filter { it.userId == userId }
            Result.success(userGoals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getGoal(goalId: String): Result<Goal?> {
        return try {
            val goal = goals.find { it.id == goalId }
            Result.success(goal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createGoal(goal: Goal): Result<String> {
        return try {
            goals.add(goal)
            
            // Create default milestones
            val milestonePercentages = listOf(25, 50, 75, 100)
            milestonePercentages.forEach { percentage ->
                val milestoneAmount = goal.targetAmount.multiply(BigDecimal(percentage))
                    .divide(BigDecimal("100"), 2, RoundingMode.HALF_UP)
                
                val milestone = GoalMilestone(
                    id = UUID.randomUUID().toString(),
                    goalId = goal.id,
                    milestoneAmount = milestoneAmount,
                    achievedDate = null,
                    description = "${percentage}% of goal achieved",
                    isAchieved = goal.currentAmount >= milestoneAmount
                )
                milestones.add(milestone)
            }
            
            Result.success(goal.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateGoal(goal: Goal): Result<Unit> {
        return try {
            val index = goals.indexOfFirst { it.id == goal.id }
            if (index != -1) {
                val updatedGoal = goal.copy(
                    status = when {
                        goal.currentAmount >= goal.targetAmount -> GoalStatus.COMPLETED
                        goal.targetDate.isBefore(LocalDateTime.now()) && goal.status == GoalStatus.ACTIVE -> GoalStatus.OVERDUE
                        else -> goal.status
                    },
                    updatedAt = LocalDateTime.now()
                )
                goals[index] = updatedGoal
                
                // Update milestone achievements
                updateMilestoneAchievements(goal.id, goal.currentAmount)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Goal not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteGoal(goalId: String): Result<Unit> {
        return try {
            val removed = goals.removeIf { it.id == goalId }
            if (removed) {
                // Remove associated contributions and milestones
                contributions.removeIf { it.goalId == goalId }
                milestones.removeIf { it.goalId == goalId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Goal not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun addContribution(contribution: GoalContribution): Result<String> {
        return try {
            contributions.add(contribution)
            
            // Update goal current amount
            val goalIndex = goals.indexOfFirst { it.id == contribution.goalId }
            if (goalIndex != -1) {
                val goal = goals[goalIndex]
                val updatedGoal = goal.copy(
                    currentAmount = goal.currentAmount.add(contribution.amount),
                    updatedAt = LocalDateTime.now()
                )
                goals[goalIndex] = updatedGoal
                
                // Update milestone achievements
                updateMilestoneAchievements(contribution.goalId, updatedGoal.currentAmount)
            }
            
            Result.success(contribution.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getContributions(goalId: String): Result<List<GoalContribution>> {
        return try {
            val goalContributions = contributions.filter { it.goalId == goalId }
                .sortedByDescending { it.contributionDate }
            Result.success(goalContributions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getContributionHistory(userId: String): Result<List<GoalContribution>> {
        return try {
            val userContributions = contributions.filter { contribution ->
                val goal = goals.find { it.id == contribution.goalId }
                goal?.userId == userId
            }.sortedByDescending { it.contributionDate }
            Result.success(userContributions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMilestones(goalId: String): Result<List<GoalMilestone>> {
        return try {
            val goalMilestones = milestones.filter { it.goalId == goalId }
                .sortedBy { it.milestoneAmount }
            Result.success(goalMilestones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createMilestone(milestone: GoalMilestone): Result<String> {
        return try {
            milestones.add(milestone)
            Result.success(milestone.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateMilestone(milestone: GoalMilestone): Result<Unit> {
        return try {
            val index = milestones.indexOfFirst { it.id == milestone.id }
            if (index != -1) {
                milestones[index] = milestone
                Result.success(Unit)
            } else {
                Result.failure(Exception("Milestone not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun calculateProgress(goalId: String): Result<BigDecimal> {
        return try {
            val goal = goals.find { it.id == goalId }
                ?: return Result.failure(Exception("Goal not found"))
            
            val progress = if (goal.targetAmount > BigDecimal.ZERO) {
                goal.currentAmount.divide(goal.targetAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal("100"))
                    .min(BigDecimal("100"))
            } else {
                BigDecimal.ZERO
            }
            
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getGoalInsights(userId: String): Result<Map<String, Any>> {
        return try {
            val userGoals = goals.filter { it.userId == userId }
            val userContributions = contributions.filter { contribution ->
                userGoals.any { it.id == contribution.goalId }
            }
            
            val totalGoals = userGoals.size
            val activeGoals = userGoals.count { it.status == GoalStatus.ACTIVE }
            val completedGoals = userGoals.count { it.status == GoalStatus.COMPLETED }
            val overdueGoals = userGoals.count { it.status == GoalStatus.OVERDUE }
            
            val totalTargetAmount = userGoals.sumOf { it.targetAmount }
            val totalCurrentAmount = userGoals.sumOf { it.currentAmount }
            val totalContributions = userContributions.sumOf { it.amount }
            
            val averageProgress = if (userGoals.isNotEmpty()) {
                userGoals.map { goal ->
                    if (goal.targetAmount > BigDecimal.ZERO) {
                        goal.currentAmount.divide(goal.targetAmount, 4, RoundingMode.HALF_UP)
                    } else {
                        BigDecimal.ZERO
                    }
                }.fold(BigDecimal.ZERO) { acc, progress -> acc.add(progress) }
                    .divide(BigDecimal(userGoals.size), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal("100"))
            } else {
                BigDecimal.ZERO
            }
            
            val monthlyContributions = userContributions
                .filter { it.contributionDate.isAfter(LocalDateTime.now().minusMonths(1)) }
                .sumOf { it.amount }
            
            val insights = mapOf(
                "totalGoals" to totalGoals,
                "activeGoals" to activeGoals,
                "completedGoals" to completedGoals,
                "overdueGoals" to overdueGoals,
                "totalTargetAmount" to totalTargetAmount,
                "totalCurrentAmount" to totalCurrentAmount,
                "totalContributions" to totalContributions,
                "averageProgress" to averageProgress,
                "monthlyContributions" to monthlyContributions,
                "completionRate" to if (totalGoals > 0) (completedGoals.toDouble() / totalGoals * 100) else 0.0
            )
            
            Result.success(insights)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getRecommendedContribution(goalId: String): Result<BigDecimal> {
        return try {
            val goal = goals.find { it.id == goalId }
                ?: return Result.failure(Exception("Goal not found"))
            
            if (goal.status != GoalStatus.ACTIVE) {
                return Result.success(BigDecimal.ZERO)
            }
            
            val remainingAmount = goal.targetAmount.subtract(goal.currentAmount)
            val now = LocalDateTime.now()
            val monthsRemaining = java.time.temporal.ChronoUnit.MONTHS.between(now, goal.targetDate)
            
            val recommendedContribution = if (monthsRemaining > 0) {
                remainingAmount.divide(BigDecimal(monthsRemaining), 2, RoundingMode.HALF_UP)
            } else {
                remainingAmount // Need to contribute all remaining amount
            }
            
            Result.success(recommendedContribution.max(BigDecimal.ZERO))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processAutoSave(userId: String): Result<List<GoalContribution>> {
        return try {
            val autoSaveContributions = mutableListOf<GoalContribution>()
            val userGoals = goals.filter { 
                it.userId == userId && 
                it.status == GoalStatus.ACTIVE && 
                it.autoSaveEnabled && 
                it.autoSaveAmount > BigDecimal.ZERO 
            }
            
            userGoals.forEach { goal ->
                val contribution = GoalContribution(
                    id = UUID.randomUUID().toString(),
                    goalId = goal.id,
                    amount = goal.autoSaveAmount,
                    contributionDate = LocalDateTime.now(),
                    contributionType = ContributionType.AUTOMATIC,
                    notes = "Automatic contribution"
                )
                
                contributions.add(contribution)
                autoSaveContributions.add(contribution)
                
                // Update goal current amount
                val goalIndex = goals.indexOfFirst { it.id == goal.id }
                if (goalIndex != -1) {
                    val updatedGoal = goal.copy(
                        currentAmount = goal.currentAmount.add(goal.autoSaveAmount),
                        updatedAt = LocalDateTime.now()
                    )
                    goals[goalIndex] = updatedGoal
                    
                    // Update milestone achievements
                    updateMilestoneAchievements(goal.id, updatedGoal.currentAmount)
                }
            }
            
            Result.success(autoSaveContributions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun updateMilestoneAchievements(goalId: String, currentAmount: BigDecimal) {
        val goalMilestones = milestones.filter { it.goalId == goalId }
        goalMilestones.forEach { milestone ->
            if (!milestone.isAchieved && currentAmount >= milestone.milestoneAmount) {
                val index = milestones.indexOfFirst { it.id == milestone.id }
                if (index != -1) {
                    milestones[index] = milestone.copy(
                    isAchieved = true,
                    achievedDate = LocalDateTime.now()
                )
            }
        }
    }
}
                }
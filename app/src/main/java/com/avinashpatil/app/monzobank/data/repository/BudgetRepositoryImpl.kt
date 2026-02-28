package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor() : BudgetRepository {
    
    private val budgets = mutableListOf<Budget>()
    private val budgetCategories = mutableListOf<BudgetCategory>()
    private val budgetAlerts = mutableListOf<BudgetAlert>()
    
    override suspend fun getBudgets(userId: String): Result<List<Budget>> {
        return try {
            val userBudgets = budgets.filter { it.userId == userId }
            Result.success(userBudgets)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBudget(budgetId: String): Result<Budget?> {
        return try {
            val budget = budgets.find { it.id == budgetId }
            Result.success(budget)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createBudget(budget: Budget): Result<String> {
        return try {
            budgets.add(budget)
            // Add budget categories
            budgetCategories.addAll(budget.categories)
            Result.success(budget.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBudget(budget: Budget): Result<Unit> {
        return try {
            val index = budgets.indexOfFirst { it.id == budget.id }
            if (index != -1) {
                budgets[index] = budget
                
                // Update categories
                budgetCategories.removeIf { it.budgetId == budget.id }
                budgetCategories.addAll(budget.categories)
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Budget not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteBudget(budgetId: String): Result<Unit> {
        return try {
            val removed = budgets.removeIf { it.id == budgetId }
            if (removed) {
                // Remove associated categories and alerts
                budgetCategories.removeIf { it.budgetId == budgetId }
                budgetAlerts.removeIf { it.budgetId == budgetId }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Budget not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBudgetCategories(budgetId: String): Result<List<BudgetCategory>> {
        return try {
            val categories = budgetCategories.filter { it.budgetId == budgetId }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateBudgetCategory(category: BudgetCategory): Result<Unit> {
        return try {
            val index = budgetCategories.indexOfFirst { it.id == category.id }
            if (index != -1) {
                budgetCategories[index] = category
                
                // Check if alert should be triggered
                val thresholdAmount = category.allocatedAmount.multiply(category.alertThreshold)
                if (category.spentAmount >= thresholdAmount) {
                    val alert = BudgetAlert(
                        id = UUID.randomUUID().toString(),
                        budgetId = category.budgetId,
                        categoryId = category.id,
                        alertType = if (category.spentAmount >= category.allocatedAmount) BudgetAlertType.CATEGORY_EXCEEDED else BudgetAlertType.THRESHOLD_REACHED,
                        message = "Category '${category.categoryName}' has ${if (category.spentAmount >= category.allocatedAmount) "exceeded" else "reached threshold for"} budget limit",
                        threshold = thresholdAmount,
                        currentAmount = category.spentAmount,
                        createdAt = LocalDateTime.now()
                    )
                    budgetAlerts.add(alert)
                }
                
                Result.success(Unit)
            } else {
                Result.failure(Exception("Budget category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBudgetAlerts(userId: String): Result<List<BudgetAlert>> {
        return try {
            val userAlerts = budgetAlerts.filter { alert ->
                val budget = budgets.find { it.id == alert.budgetId }
                budget?.userId == userId
            }.sortedByDescending { it.createdAt }
            Result.success(userAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createBudgetAlert(alert: BudgetAlert): Result<String> {
        return try {
            budgetAlerts.add(alert)
            Result.success(alert.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markAlertAsRead(alertId: String): Result<Unit> {
        return try {
            val index = budgetAlerts.indexOfFirst { it.id == alertId }
            if (index != -1) {
                val alert = budgetAlerts[index]
                budgetAlerts[index] = alert.copy(isRead = true)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkBudgetLimits(userId: String): Result<List<BudgetAlert>> {
        return try {
            val newAlerts = mutableListOf<BudgetAlert>()
            val userBudgets = budgets.filter { it.userId == userId && it.status == BudgetStatus.ACTIVE }
            
            userBudgets.forEach { budget ->
                // Check overall budget limit
                val budgetThreshold = budget.totalAmount.multiply(BigDecimal("0.90")) // 90% threshold
                if (budget.spentAmount >= budgetThreshold) {
                    val alert = BudgetAlert(
                        id = UUID.randomUUID().toString(),
                        budgetId = budget.id,
                        categoryId = null,
                        alertType = if (budget.spentAmount >= budget.totalAmount) BudgetAlertType.BUDGET_EXCEEDED else BudgetAlertType.THRESHOLD_REACHED,
                        message = "Budget '${budget.name}' has ${if (budget.spentAmount >= budget.totalAmount) "exceeded" else "reached 90% of"} limit",
                        threshold = budgetThreshold,
                        currentAmount = budget.spentAmount,
                        createdAt = LocalDateTime.now()
                    )
                    budgetAlerts.add(alert)
                    newAlerts.add(alert)
                }
                
                // Check category limits
                budget.categories.forEach { category ->
                    val categoryThreshold = category.allocatedAmount.multiply(category.alertThreshold)
                    if (category.spentAmount >= categoryThreshold) {
                        val alert = BudgetAlert(
                            id = UUID.randomUUID().toString(),
                            budgetId = budget.id,
                            categoryId = category.id,
                            alertType = if (category.spentAmount >= category.allocatedAmount) BudgetAlertType.CATEGORY_EXCEEDED else BudgetAlertType.THRESHOLD_REACHED,
                            message = "Category '${category.categoryName}' has ${if (category.spentAmount >= category.allocatedAmount) "exceeded" else "reached threshold for"} budget limit",
                            threshold = categoryThreshold,
                            currentAmount = category.spentAmount,
                            createdAt = LocalDateTime.now()
                        )
                        budgetAlerts.add(alert)
                        newAlerts.add(alert)
                    }
                }
            }
            
            Result.success(newAlerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBudgetInsights(userId: String): Result<Map<String, Any>> {
        return try {
            val userBudgets = budgets.filter { it.userId == userId }
            val activeBudgets = userBudgets.filter { it.status == BudgetStatus.ACTIVE }
            
            val totalBudgeted = userBudgets.sumOf { it.totalAmount }
            val totalSpent = userBudgets.sumOf { it.spentAmount }
            val totalRemaining = userBudgets.sumOf { it.remainingAmount }
            
            val averageSpendingRate = if (totalBudgeted > BigDecimal.ZERO) {
                totalSpent.divide(totalBudgeted, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal("100"))
            } else {
                BigDecimal.ZERO
            }
            
            val exceededBudgets = userBudgets.count { it.spentAmount >= it.totalAmount }
            val onTrackBudgets = activeBudgets.count { it.spentAmount < it.totalAmount.multiply(BigDecimal("0.90")) }
            
            val insights = mapOf(
                "totalBudgets" to userBudgets.size,
                "activeBudgets" to activeBudgets.size,
                "totalBudgeted" to totalBudgeted,
                "totalSpent" to totalSpent,
                "totalRemaining" to totalRemaining,
                "averageSpendingRate" to averageSpendingRate,
                "exceededBudgets" to exceededBudgets,
                "onTrackBudgets" to onTrackBudgets,
                "unreadAlerts" to budgetAlerts.count { !it.isRead }
            )
            
            Result.success(insights)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSpendingTrends(budgetId: String): Result<Map<String, BigDecimal>> {
        return try {
            val budget = budgets.find { it.id == budgetId }
                ?: return Result.failure(Exception("Budget not found"))
            
            // Mock spending trends by category
            val trends = budget.categories.associate { category ->
                val spendingRate = if (category.allocatedAmount > BigDecimal.ZERO) {
                    category.spentAmount.divide(category.allocatedAmount, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal("100"))
                } else {
                    BigDecimal.ZERO
                }
                category.categoryName to spendingRate
            }
            
            Result.success(trends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime
import java.math.BigDecimal

data class Budget(
    val id: String,
    val userId: String,
    val name: String,
    val totalAmount: BigDecimal,
    val spentAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val period: BudgetPeriod,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val categories: List<BudgetCategory>,
    val status: BudgetStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class BudgetCategory(
    val id: String,
    val budgetId: String,
    val categoryName: String,
    val allocatedAmount: BigDecimal,
    val spentAmount: BigDecimal,
    val remainingAmount: BigDecimal,
    val alertThreshold: BigDecimal = BigDecimal("0.80") // 80% threshold
)

data class BudgetAlert(
    val id: String,
    val budgetId: String,
    val categoryId: String?,
    val alertType: BudgetAlertType,
    val message: String,
    val threshold: BigDecimal,
    val currentAmount: BigDecimal,
    val createdAt: LocalDateTime,
    val isRead: Boolean = false
)

enum class BudgetPeriod {
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY,
    CUSTOM
}

enum class BudgetStatus {
    ACTIVE,
    COMPLETED,
    EXCEEDED,
    PAUSED
}

enum class BudgetAlertType {
    THRESHOLD_REACHED,
    BUDGET_EXCEEDED,
    CATEGORY_EXCEEDED,
    LOW_REMAINING
}

interface BudgetRepository {
    suspend fun getBudgets(userId: String): Result<List<Budget>>
    suspend fun getBudget(budgetId: String): Result<Budget?>
    suspend fun createBudget(budget: Budget): Result<String>
    suspend fun updateBudget(budget: Budget): Result<Unit>
    suspend fun deleteBudget(budgetId: String): Result<Unit>
    
    suspend fun getBudgetCategories(budgetId: String): Result<List<BudgetCategory>>
    suspend fun updateBudgetCategory(category: BudgetCategory): Result<Unit>
    
    suspend fun getBudgetAlerts(userId: String): Result<List<BudgetAlert>>
    suspend fun createBudgetAlert(alert: BudgetAlert): Result<String>
    suspend fun markAlertAsRead(alertId: String): Result<Unit>
    
    suspend fun checkBudgetLimits(userId: String): Result<List<BudgetAlert>>
    suspend fun getBudgetInsights(userId: String): Result<Map<String, Any>>
    suspend fun getSpendingTrends(budgetId: String): Result<Map<String, BigDecimal>>
}
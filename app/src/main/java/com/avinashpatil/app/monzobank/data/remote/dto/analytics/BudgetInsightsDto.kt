package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class BudgetInsightsDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "period")
    val period: String,
    
    @Json(name = "budget_categories")
    val budgetCategories: List<BudgetCategoryDto>,
    
    @Json(name = "total_budget")
    val totalBudget: BigDecimal,
    
    @Json(name = "total_spent")
    val totalSpent: BigDecimal,
    
    @Json(name = "remaining_budget")
    val remainingBudget: BigDecimal,
    
    @Json(name = "budget_utilization_percentage")
    val budgetUtilizationPercentage: Double,
    
    @Json(name = "projected_end_of_period_spending")
    val projectedEndOfPeriodSpending: BigDecimal,
    
    @Json(name = "recommendations")
    val recommendations: List<BudgetRecommendationDto>,
    
    @Json(name = "alerts")
    val alerts: List<BudgetAlertDto>,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class BudgetCategoryDto(
    @Json(name = "category")
    val category: String,
    
    @Json(name = "budget_amount")
    val budgetAmount: BigDecimal,
    
    @Json(name = "spent_amount")
    val spentAmount: BigDecimal,
    
    @Json(name = "remaining_amount")
    val remainingAmount: BigDecimal,
    
    @Json(name = "utilization_percentage")
    val utilizationPercentage: Double,
    
    @Json(name = "status")
    val status: String, // "on_track", "over_budget", "approaching_limit"
    
    @Json(name = "projected_spending")
    val projectedSpending: BigDecimal
)

@JsonClass(generateAdapter = true)
data class BudgetRecommendationDto(
    @Json(name = "type")
    val type: String,
    
    @Json(name = "category")
    val category: String?,
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "priority")
    val priority: String, // "high", "medium", "low"
    
    @Json(name = "potential_savings")
    val potentialSavings: BigDecimal?
)

@JsonClass(generateAdapter = true)
data class BudgetAlertDto(
    @Json(name = "type")
    val type: String, // "overspend", "approaching_limit", "budget_exceeded"
    
    @Json(name = "category")
    val category: String,
    
    @Json(name = "message")
    val message: String,
    
    @Json(name = "severity")
    val severity: String, // "critical", "warning", "info"
    
    @Json(name = "amount_over")
    val amountOver: BigDecimal?
)
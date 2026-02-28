package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for card spending insights and analytics
 */
data class CardSpendingInsightsDto(
    @SerializedName("total_spent")
    val totalSpent: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("category_breakdown")
    val categoryBreakdown: Map<String, Double>,
    
    @SerializedName("monthly_trend")
    val monthlyTrend: List<MonthlySpendingDto>,
    
    @SerializedName("period")
    val period: String, // WEEK, MONTH, YEAR
    
    @SerializedName("comparison_previous_period")
    val comparisonPreviousPeriod: Double? = null
)

/**
 * DTO for monthly spending data
 */
data class MonthlySpendingDto(
    @SerializedName("month")
    val month: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("transaction_count")
    val transactionCount: Int
)
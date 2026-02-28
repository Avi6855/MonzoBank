package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class SpendingAnalyticsDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "period")
    val period: String,
    
    @Json(name = "total_spent")
    val totalSpent: BigDecimal,
    
    @Json(name = "total_income")
    val totalIncome: BigDecimal,
    
    @Json(name = "net_flow")
    val netFlow: BigDecimal,
    
    @Json(name = "transaction_count")
    val transactionCount: Int,
    
    @Json(name = "average_transaction_amount")
    val averageTransactionAmount: BigDecimal,
    
    @Json(name = "largest_transaction")
    val largestTransaction: BigDecimal,
    
    @Json(name = "smallest_transaction")
    val smallestTransaction: BigDecimal,
    
    @Json(name = "spending_by_category")
    val spendingByCategory: Map<String, BigDecimal>,
    
    @Json(name = "spending_trend")
    val spendingTrend: String, // "increasing", "decreasing", "stable"
    
    @Json(name = "comparison_previous_period")
    val comparisonPreviousPeriod: BigDecimal,
    
    @Json(name = "percentage_change")
    val percentageChange: Double,
    
    @Json(name = "generated_at")
    val generatedAt: String
)
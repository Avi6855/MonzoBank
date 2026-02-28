package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class MonthlyTrendsDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "months")
    val months: List<MonthlyDataDto>,
    
    @Json(name = "overall_trend")
    val overallTrend: String, // "increasing", "decreasing", "stable"
    
    @Json(name = "average_monthly_spending")
    val averageMonthlySpending: BigDecimal,
    
    @Json(name = "highest_month")
    val highestMonth: MonthlyDataDto,
    
    @Json(name = "lowest_month")
    val lowestMonth: MonthlyDataDto,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class MonthlyDataDto(
    @Json(name = "year")
    val year: Int,
    
    @Json(name = "month")
    val month: Int,
    
    @Json(name = "month_name")
    val monthName: String,
    
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
    
    @Json(name = "growth_rate")
    val growthRate: Double, // Percentage change from previous month
    
    @Json(name = "days_in_month")
    val daysInMonth: Int,
    
    @Json(name = "daily_average")
    val dailyAverage: BigDecimal
)
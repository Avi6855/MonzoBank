package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CategoryBreakdownDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "period")
    val period: String,
    
    @Json(name = "categories")
    val categories: List<CategorySpendingDto>,
    
    @Json(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class CategorySpendingDto(
    @Json(name = "category")
    val category: String,
    
    @Json(name = "amount")
    val amount: BigDecimal,
    
    @Json(name = "transaction_count")
    val transactionCount: Int,
    
    @Json(name = "percentage")
    val percentage: Double,
    
    @Json(name = "average_amount")
    val averageAmount: BigDecimal,
    
    @Json(name = "trend")
    val trend: String, // "up", "down", "stable"
    
    @Json(name = "comparison_previous_period")
    val comparisonPreviousPeriod: BigDecimal
)
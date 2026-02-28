package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class MerchantAnalyticsDto(
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "period")
    val period: String,
    
    @Json(name = "merchants")
    val merchants: List<MerchantSpendingDto>,
    
    @Json(name = "total_merchants")
    val totalMerchants: Int,
    
    @Json(name = "total_amount")
    val totalAmount: BigDecimal,
    
    @Json(name = "generated_at")
    val generatedAt: String
)

@JsonClass(generateAdapter = true)
data class MerchantSpendingDto(
    @Json(name = "merchant_id")
    val merchantId: String,
    
    @Json(name = "merchant_name")
    val merchantName: String,
    
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
    
    @Json(name = "first_transaction_date")
    val firstTransactionDate: String,
    
    @Json(name = "last_transaction_date")
    val lastTransactionDate: String,
    
    @Json(name = "frequency")
    val frequency: String, // "daily", "weekly", "monthly", "occasional"
    
    @Json(name = "trend")
    val trend: String // "increasing", "decreasing", "stable"
)
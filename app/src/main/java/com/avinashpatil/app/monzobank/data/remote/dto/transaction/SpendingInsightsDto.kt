package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.avinashpatil.app.monzobank.data.remote.dto.common.MonthlySpendingDto
import com.avinashpatil.app.monzobank.data.remote.dto.common.MerchantSpendingDto
import com.google.gson.annotations.SerializedName

data class SpendingInsightsDto(
    @SerializedName("total_spent")
    val totalSpent: Double,
    @SerializedName("category_breakdown")
    val categoryBreakdown: Map<String, Double>,
    @SerializedName("monthly_trend")
    val monthlyTrend: List<MonthlySpendingDto>,
    @SerializedName("top_merchants")
    val topMerchants: List<MerchantSpendingDto>,
    @SerializedName("average_transaction")
    val averageTransaction: Double
)
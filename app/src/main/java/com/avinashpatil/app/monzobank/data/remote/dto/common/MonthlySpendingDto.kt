package com.avinashpatil.app.monzobank.data.remote.dto.common

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object for monthly spending information
 */
data class MonthlySpendingDto(
    @SerializedName("month")
    val month: String,
    
    @SerializedName("year")
    val year: Int,
    
    @SerializedName("total_spent")
    val totalSpent: Double,
    
    @SerializedName("transaction_count")
    val transactionCount: Int,
    
    @SerializedName("average_transaction")
    val averageTransaction: Double,
    
    @SerializedName("currency")
    val currency: String
)
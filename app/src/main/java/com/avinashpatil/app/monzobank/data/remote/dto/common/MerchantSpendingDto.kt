package com.avinashpatil.app.monzobank.data.remote.dto.common

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object for merchant spending information
 */
data class MerchantSpendingDto(
    @SerializedName("merchant_name")
    val merchantName: String,
    
    @SerializedName("merchant_id")
    val merchantId: String,
    
    @SerializedName("total_spent")
    val totalSpent: Double,
    
    @SerializedName("transaction_count")
    val transactionCount: Int,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("last_transaction")
    val lastTransaction: String,
    
    @SerializedName("currency")
    val currency: String
)
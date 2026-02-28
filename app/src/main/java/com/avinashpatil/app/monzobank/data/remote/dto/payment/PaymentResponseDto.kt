package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for payment operation response
 */
data class PaymentResponseDto(
    @SerializedName("payment_id")
    val paymentId: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("transaction_id")
    val transactionId: String?,
    
    @SerializedName("message")
    val message: String?,
    
    @SerializedName("amount")
    val amount: Double? = null,
    
    @SerializedName("currency")
    val currency: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null
)
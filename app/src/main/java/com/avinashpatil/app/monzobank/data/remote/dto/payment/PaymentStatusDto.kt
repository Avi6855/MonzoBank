package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for payment status information
 */
data class PaymentStatusDto(
    @SerializedName("payment_id")
    val paymentId: String,
    
    @SerializedName("status")
    val status: String, // PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("completed_at")
    val completedAt: String?,
    
    @SerializedName("failure_reason")
    val failureReason: String? = null,
    
    @SerializedName("estimated_completion")
    val estimatedCompletion: String?
)
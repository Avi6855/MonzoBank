package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for payment request information
 */
data class PaymentRequestDto(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val amount: Double,
    val description: String,
    val status: String, // PENDING, ACCEPTED, DECLINED, EXPIRED
    val createdAt: String,
    val expiresAt: String? = null
)
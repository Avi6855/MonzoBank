package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for creating payment requests
 */
data class CreatePaymentRequestDto(
    val fromUserId: String,
    val amount: Double,
    val description: String,
    val dueDate: String? = null,
    val currency: String = "GBP"
)
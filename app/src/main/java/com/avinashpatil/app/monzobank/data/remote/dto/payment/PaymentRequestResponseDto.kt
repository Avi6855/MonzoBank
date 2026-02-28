package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for payment request response
 */
data class PaymentRequestResponseDto(
    val accepted: Boolean,
    val paymentId: String? = null,
    val message: String? = null,
    val transactionId: String? = null
)
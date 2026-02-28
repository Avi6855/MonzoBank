package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for split bill response
 */
data class SplitBillResponseDto(
    val accepted: Boolean,
    val participantId: String,
    val amount: Double,
    val message: String? = null,
    val paymentId: String? = null
)
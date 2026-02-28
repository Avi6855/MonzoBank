package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for updating scheduled payments
 */
data class UpdateScheduledPaymentDto(
    val amount: Double? = null,
    val frequency: String? = null,
    val endDate: String? = null,
    val reference: String? = null
)
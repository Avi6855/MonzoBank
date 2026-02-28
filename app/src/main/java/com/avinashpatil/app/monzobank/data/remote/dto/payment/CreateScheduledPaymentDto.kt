package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for creating scheduled payments
 */
data class CreateScheduledPaymentDto(
    val payeeId: String,
    val amount: Double,
    val frequency: String, // WEEKLY, MONTHLY, YEARLY
    val startDate: String,
    val endDate: String? = null,
    val reference: String,
    val description: String? = null
)
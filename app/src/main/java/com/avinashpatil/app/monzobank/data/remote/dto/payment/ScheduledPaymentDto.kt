package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for scheduled payment information
 */
data class ScheduledPaymentDto(
    val id: String,
    val payeeId: String,
    val amount: Double,
    val frequency: String,
    val nextPaymentDate: String,
    val status: String,
    val reference: String,
    val description: String? = null,
    val createdAt: String,
    val updatedAt: String? = null
)
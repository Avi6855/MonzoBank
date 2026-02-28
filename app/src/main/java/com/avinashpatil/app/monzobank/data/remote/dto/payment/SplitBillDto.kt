package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for split bill information
 */
data class SplitBillDto(
    val id: String,
    val totalAmount: Double,
    val description: String,
    val participants: List<SplitParticipantDto>,
    val status: String, // ACTIVE, COMPLETED, CANCELLED
    val createdAt: String,
    val dueDate: String,
    val currency: String
)
package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for creating split bills
 */
data class CreateSplitBillDto(
    val totalAmount: Double,
    val description: String,
    val participants: List<SplitParticipantDto>,
    val dueDate: String,
    val currency: String = "GBP"
)
package com.avinashpatil.app.monzobank.data.remote.dto.payment

/**
 * Data transfer object for split bill participants
 */
data class SplitParticipantDto(
    val userId: String,
    val amount: Double,
    val status: String, // PENDING, ACCEPTED, DECLINED, PAID
    val userName: String? = null,
    val email: String? = null
)
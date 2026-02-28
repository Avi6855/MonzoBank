package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Bulk Payment Item domain model
 * Represents a single payment item within a bulk payment
 */
data class BulkPaymentItem(
    val id: String,
    val payeeId: String,
    val payeeName: String,
    val accountNumber: String? = null,
    val sortCode: String? = null,
    val iban: String? = null,
    val amount: BigDecimal,
    val currency: String = "GBP",
    val reference: String? = null,
    val description: String? = null,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val failureReason: String? = null,
    val processedAt: LocalDateTime? = null
)
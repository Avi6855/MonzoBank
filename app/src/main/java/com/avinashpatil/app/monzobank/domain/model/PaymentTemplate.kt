package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment Template domain model
 * Represents a saved payment template for quick reuse
 */
data class PaymentTemplate(
    val id: String,
    val userId: String,
    val name: String,
    val description: String? = null,
    val payeeId: String? = null,
    val payeeName: String,
    val accountNumber: String? = null,
    val sortCode: String? = null,
    val iban: String? = null,
    val amount: BigDecimal? = null,
    val currency: String = "GBP",
    val reference: String? = null,
    val paymentType: PaymentType,
    val frequency: PaymentFrequency? = null,
    val isActive: Boolean = true,
    val usageCount: Int = 0,
    val lastUsed: LocalDateTime? = null,
    val tags: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
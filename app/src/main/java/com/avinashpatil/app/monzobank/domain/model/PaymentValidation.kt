package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment Validation domain model
 * Represents the result of payment validation
 */
data class PaymentValidation(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val estimatedFee: BigDecimal = BigDecimal.ZERO,
    val estimatedArrival: LocalDateTime? = null,
    val riskScore: Double? = null,
    val complianceChecks: Map<String, Boolean> = emptyMap(),
    val validatedAt: LocalDateTime = LocalDateTime.now()
)
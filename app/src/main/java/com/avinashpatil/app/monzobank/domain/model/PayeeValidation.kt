package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

/**
 * Payee Validation domain model
 * Represents the result of payee validation
 */
data class PayeeValidation(
    val isValid: Boolean,
    val accountExists: Boolean = false,
    val accountName: String? = null,
    val bankName: String? = null,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val validatedAt: LocalDateTime = LocalDateTime.now(),
    val confidence: Double? = null
)
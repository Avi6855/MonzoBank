package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime

/**
 * Payee domain model
 * Represents a person or entity that can receive payments
 */
data class Payee(
    val id: String,
    val name: String,
    val accountNumber: String? = null,
    val sortCode: String? = null,
    val iban: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val address: Address? = null,
    val bankName: String? = null,
    val reference: String? = null,
    val isVerified: Boolean = false,
    val isFavorite: Boolean = false,
    val lastPaymentDate: LocalDateTime? = null,
    val totalPayments: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val metadata: Map<String, Any> = emptyMap()
) {
    val displayName: String
        get() = name
    
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
    
    val hasAccountDetails: Boolean
        get() = !accountNumber.isNullOrBlank() && !sortCode.isNullOrBlank()
    
    val hasIban: Boolean
        get() = !iban.isNullOrBlank()
    
    val canReceivePayments: Boolean
        get() = hasAccountDetails || hasIban
}
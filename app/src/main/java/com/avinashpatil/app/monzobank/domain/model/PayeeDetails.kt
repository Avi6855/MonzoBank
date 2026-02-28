package com.avinashpatil.app.monzobank.domain.model

/**
 * Payee Details domain model
 * Represents the details of a payment recipient
 */
data class PayeeDetails(
    val name: String,
    val accountNumber: String? = null,
    val sortCode: String? = null,
    val iban: String? = null,
    val bic: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val bankName: String? = null,
    val country: String? = null,
    val currency: String = "GBP"
)
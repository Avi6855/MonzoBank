package com.avinashpatil.app.monzobank.data.model

import java.util.Date

// Simple data models for UI screens
data class Transaction(
    val id: String,
    val accountId: String,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Date,
    val merchant: String? = null,
    val type: String,
    val location: String? = null,
    val reference: String? = null,
    val balance: Double = 0.0
)

data class Account(
    val id: String,
    val name: String,
    val type: String,
    val balance: Double,
    val currency: String = "GBP",
    val accountNumber: String,
    val sortCode: String,
    val isActive: Boolean = true
)

data class Card(
    val id: String,
    val accountId: String,
    val cardNumber: String,
    val cardholderName: String,
    val expiryDate: String,
    val cardType: String,
    val provider: String,
    val isActive: Boolean = true,
    val isFrozen: Boolean = false,
    val spendingLimit: Double = 1000.0,
    val currentSpending: Double = 0.0
)
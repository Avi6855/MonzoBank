package com.avinashpatil.app.monzobank.domain.model

import java.time.LocalDateTime
import java.util.Date

data class Contact(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val accountNumber: String? = null,
    val sortCode: String? = null,
    val iban: String? = null,
    val bic: String? = null,
    val nickname: String? = null,
    val avatar: String? = null,
    val isFavorite: Boolean = false,
    val isBlocked: Boolean = false,
    val lastTransactionDate: LocalDateTime? = null,
    val lastUsed: Date? = null,
    val totalTransactions: Int = 0,
    val totalAmount: Double = 0.0,
    val currency: String = "GBP",
    val contactType: ContactType = ContactType.PERSONAL,
    val verificationStatus: ContactVerificationStatus = ContactVerificationStatus.UNVERIFIED,
    val tags: List<String> = emptyList(),
    val notes: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ContactType {
    PERSONAL,
    BUSINESS,
    MERCHANT,
    BANK,
    GOVERNMENT,
    CHARITY,
    OTHER
}

enum class ContactVerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED,
    FAILED,
    EXPIRED
}
package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment domain model
 * Represents a payment transaction in the banking system
 */
data class Payment(
    val id: String,
    val userId: String = "",
    val fromAccountId: String,
    val toAccountId: String? = null,
    val recipientName: String? = null,
    val recipientEmail: String? = null,
    val recipientPhone: String? = null,
    val amount: BigDecimal,
    val currency: String = "GBP",
    val description: String? = null,
    val reference: String? = null,
    val type: PaymentType,
    val status: PaymentStatus,
    val paymentMethod: String? = null, // Card, Bank Transfer, etc.
    val merchantId: String? = null,
    val merchantName: String? = null,
    val merchantCategory: String? = null,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val exchangeRate: BigDecimal? = null,
    val originalAmount: BigDecimal? = null,
    val originalCurrency: String? = null,
    val fees: BigDecimal = BigDecimal.ZERO,
    val feeDescription: String? = null,
    val scheduledDate: LocalDateTime? = null,
    val processedDate: LocalDateTime? = null,
    val settledDate: LocalDateTime? = null,
    val failureReason: String? = null,
    val authorizationCode: String? = null,
    val transactionId: String? = null,
    val externalReference: String? = null,
    val metadata: Map<String, Any> = emptyMap(),
    val tags: List<String> = emptyList(),
    val isRecurring: Boolean = false,
    val recurringPaymentId: String? = null,
    val parentPaymentId: String? = null,
    val childPaymentIds: List<String> = emptyList(),
    val riskScore: Double? = null,
    val riskFlags: List<String> = emptyList(),
    val complianceChecks: Map<String, Boolean> = emptyMap(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
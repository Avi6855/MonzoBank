package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Standing Order domain model
 * Represents a recurring payment instruction
 */
data class StandingOrder(
    val id: String,
    val userId: String,
    val fromAccountId: String,
    val toAccountId: String?,
    val recipientName: String,
    val recipientAccountNumber: String?,
    val recipientSortCode: String?,
    val recipientIban: String?,
    val recipientBic: String?,
    val amount: BigDecimal,
    val currency: String = "GBP",
    val reference: String?,
    val description: String?,
    val frequency: PaymentFrequency,
    val status: StandingOrderStatus,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val nextPaymentDate: LocalDate?,
    val lastPaymentDate: LocalDate?,
    val totalPayments: Int?,
    val remainingPayments: Int?,
    val completedPayments: Int = 0,
    val failedPayments: Int = 0,
    val totalAmountPaid: BigDecimal = BigDecimal.ZERO,
    val isActive: Boolean = true,
    val isPaused: Boolean = false,
    val pausedUntil: LocalDate?,
    val pauseReason: String?,
    val cancellationReason: String?,
    val failureReason: String?,
    val lastFailureDate: LocalDate?,
    val consecutiveFailures: Int = 0,
    val maxRetries: Int = 3,
    val retryCount: Int = 0,
    val notificationPreferences: Map<String, Boolean> = emptyMap(),
    val metadata: Map<String, Any> = emptyMap(),
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val createdBy: String?,
    val lastModifiedBy: String?
)
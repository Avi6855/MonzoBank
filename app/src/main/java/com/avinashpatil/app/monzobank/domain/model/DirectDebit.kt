package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Direct Debit domain model
 * Represents a direct debit mandate and its associated payments
 */
data class DirectDebit(
    val id: String,
    val userId: String,
    val accountId: String,
    val mandateId: String,
    val mandateReference: String,
    val originatorId: String,
    val originatorName: String,
    val originatorAddress: String?,
    val serviceUserNumber: String?,
    val description: String?,
    val category: String?,
    val status: DirectDebitStatus,
    val mandateType: DirectDebitMandateType,
    val frequency: PaymentFrequency?,
    val amount: BigDecimal?,
    val currency: String = "GBP",
    val isVariableAmount: Boolean = false,
    val minimumAmount: BigDecimal?,
    val maximumAmount: BigDecimal?,
    val advanceNotice: Int?, // Days of advance notice required
    val mandateDate: LocalDate,
    val firstCollectionDate: LocalDate?,
    val lastCollectionDate: LocalDate?,
    val nextCollectionDate: LocalDate?,
    val finalCollectionDate: LocalDate?,
    val totalCollections: Int = 0,
    val successfulCollections: Int = 0,
    val failedCollections: Int = 0,
    val totalAmountCollected: BigDecimal = BigDecimal.ZERO,
    val lastCollectionAmount: BigDecimal?,
    val lastCollectionStatus: PaymentStatus?,
    val lastFailureReason: String?,
    val consecutiveFailures: Int = 0,
    val isActive: Boolean = true,
    val isCancelled: Boolean = false,
    val cancellationDate: LocalDate?,
    val cancellationReason: String?,
    val guaranteeApplies: Boolean = true,
    val protectionScheme: String = "Direct Debit Guarantee",
    val notificationPreferences: Map<String, Boolean> = emptyMap(),
    val metadata: Map<String, Any> = emptyMap(),
    val tags: List<String> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val createdBy: String?,
    val lastModifiedBy: String?
)

/**
 * Direct Debit status enumeration
 */
enum class DirectDebitStatus {
    ACTIVE,            // Direct debit is active
    PENDING,           // Direct debit pending activation
    SUSPENDED,         // Direct debit suspended
    CANCELLED,         // Direct debit cancelled
    EXPIRED,           // Direct debit expired
    FAILED,            // Direct debit failed
    UNDER_REVIEW,      // Under review
    BLOCKED,           // Direct debit blocked
    DORMANT,           // Direct debit dormant
    UNKNOWN            // Unknown status
}

/**
 * Direct Debit mandate type enumeration
 */
enum class DirectDebitMandateType {
    FIXED_AMOUNT,      // Fixed amount collections
    VARIABLE_AMOUNT,   // Variable amount collections
    ONE_OFF,           // One-off collection
    RECURRING,         // Recurring collections
    BULK,              // Bulk collections
    CORPORATE,         // Corporate mandate
    PERSONAL,          // Personal mandate
    BUSINESS,          // Business mandate
    GOVERNMENT,        // Government mandate
    CHARITY,           // Charity mandate
    UNKNOWN            // Unknown type
}
package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model for scheduled payments
 */
data class ScheduledPayment(
    val id: String,
    val userId: String,
    val payeeId: String,
    val payeeName: String,
    val amount: BigDecimal,
    val frequency: PaymentFrequency,
    val nextPaymentDate: LocalDate,
    val endDate: LocalDate? = null,
    val status: ScheduledPaymentStatus,
    val reference: String,
    val description: String? = null,
    val accountId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val lastPaymentDate: LocalDate? = null,
    val totalPaymentsMade: Int = 0,
    val isActive: Boolean = true
) {
    val isRecurring: Boolean
        get() = endDate == null || nextPaymentDate.isBefore(endDate)
    
    val displayFrequency: String
        get() = when (frequency) {
            PaymentFrequency.ONCE -> "One-time"
            PaymentFrequency.DAILY -> "Daily"
            PaymentFrequency.WEEKLY -> "Weekly"
            PaymentFrequency.FORTNIGHTLY -> "Fortnightly"
            PaymentFrequency.MONTHLY -> "Monthly"
            PaymentFrequency.QUARTERLY -> "Quarterly"
            PaymentFrequency.SEMI_ANNUALLY -> "Semi-annually"
            PaymentFrequency.ANNUALLY -> "Annually"
            PaymentFrequency.BUSINESS_DAILY -> "Business Daily"
            PaymentFrequency.BUSINESS_WEEKLY -> "Business Weekly"
            PaymentFrequency.BUSINESS_MONTHLY -> "Business Monthly"
            PaymentFrequency.FIRST_OF_MONTH -> "First of Month"
            PaymentFrequency.LAST_OF_MONTH -> "Last of Month"
            PaymentFrequency.FIFTEENTH_OF_MONTH -> "15th of Month"
            else -> frequency.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
        }
}

/**
 * Status of scheduled payments
 */
enum class ScheduledPaymentStatus {
    ACTIVE,
    PAUSED,
    CANCELLED,
    COMPLETED,
    FAILED,
    PENDING_APPROVAL
}
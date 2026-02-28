package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Payment Request domain model
 * Represents a request for payment from one user to another
 */
data class PaymentRequest(
    val id: String,
    val requesterId: String,
    val requesterName: String,
    val requesterEmail: String?,
    val requesterPhone: String?,
    val payerId: String?,
    val payerName: String?,
    val payerEmail: String?,
    val payerPhone: String?,
    val amount: BigDecimal,
    val currency: String = "GBP",
    val description: String?,
    val reference: String?,
    val category: String?,
    val status: PaymentRequestStatus,
    val type: PaymentRequestType,
    val priority: PaymentRequestPriority = PaymentRequestPriority.NORMAL,
    val dueDate: LocalDateTime?,
    val expiryDate: LocalDateTime?,
    val reminderFrequency: PaymentFrequency?,
    val maxReminders: Int = 3,
    val remindersSent: Int = 0,
    val lastReminderSent: LocalDateTime?,
    val nextReminderDate: LocalDateTime?,
    val paymentId: String?, // ID of the actual payment when fulfilled
    val paymentDate: LocalDateTime?,
    val partialPaymentsAllowed: Boolean = false,
    val partialPayments: List<PartialPayment> = emptyList(),
    val totalPaidAmount: BigDecimal = BigDecimal.ZERO,
    val remainingAmount: BigDecimal = amount,
    val attachments: List<String> = emptyList(), // File URLs or IDs
    val notes: String?,
    val publicNotes: String?, // Notes visible to payer
    val privateNotes: String?, // Notes visible only to requester
    val tags: List<String> = emptyList(),
    val metadata: Map<String, Any> = emptyMap(),
    val notificationPreferences: Map<String, Boolean> = emptyMap(),
    val isRecurring: Boolean = false,
    val recurringSchedule: PaymentFrequency?,
    val recurringEndDate: LocalDateTime?,
    val parentRequestId: String?, // For recurring requests
    val childRequestIds: List<String> = emptyList(),
    val groupId: String?, // For group payment requests
    val splitDetails: List<SplitDetail> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val acceptedAt: LocalDateTime?,
    val rejectedAt: LocalDateTime?,
    val cancelledAt: LocalDateTime?,
    val fulfilledAt: LocalDateTime?
) {
    // Computed properties for UI compatibility
    val fromUserName: String get() = requesterName
    val fromUserInitials: String get() = requesterName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
    val toUserName: String? get() = payerName
    val toUserInitials: String? get() = payerName?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")
    val isReceived: Boolean get() = status == PaymentRequestStatus.PAID || status == PaymentRequestStatus.PARTIALLY_PAID
}

/**
 * Partial payment information
 */
data class PartialPayment(
    val id: String,
    val amount: BigDecimal,
    val paymentId: String,
    val paidAt: LocalDateTime,
    val notes: String?
)

/**
 * Split payment detail for group requests
 */
data class SplitDetail(
    val userId: String,
    val userName: String,
    val amount: BigDecimal,
    val status: PaymentRequestStatus,
    val paymentId: String?,
    val paidAt: LocalDateTime?
)

/**
 * Payment Request status enumeration
 */
enum class PaymentRequestStatus {
    DRAFT,             // Request being created
    PENDING,           // Request sent, awaiting response
    ACCEPTED,          // Request accepted by payer
    REJECTED,          // Request rejected by payer
    PAID,              // Request fully paid
    PARTIALLY_PAID,    // Request partially paid
    EXPIRED,           // Request expired
    CANCELLED,         // Request cancelled by requester
    OVERDUE,           // Request overdue
    DISPUTED,          // Request disputed
    REFUNDED,          // Payment refunded
    FAILED,            // Payment failed
    UNKNOWN            // Unknown status
}

/**
 * Payment Request type enumeration
 */
enum class PaymentRequestType {
    PERSONAL,          // Personal payment request
    BUSINESS,          // Business payment request
    INVOICE,           // Invoice payment request
    BILL_SPLIT,        // Bill splitting request
    GROUP_PAYMENT,     // Group payment request
    SUBSCRIPTION,      // Subscription payment request
    DONATION,          // Donation request
    REFUND,            // Refund request
    EXPENSE_CLAIM,     // Expense claim request
    LOAN_REPAYMENT,    // Loan repayment request
    RENT,              // Rent payment request
    UTILITIES,         // Utilities payment request
    SERVICES,          // Services payment request
    GOODS,             // Goods payment request
    GIFT,              // Gift payment request
    OTHER,             // Other type
    UNKNOWN            // Unknown type
}

/**
 * Payment Request priority enumeration
 */
enum class PaymentRequestPriority {
    LOW,               // Low priority
    NORMAL,            // Normal priority
    HIGH,              // High priority
    URGENT,            // Urgent priority
    CRITICAL           // Critical priority
}
package com.avinashpatil.app.monzobank.domain.model

/**
 * Bulk Payment Status enumeration
 * Defines the different states a bulk payment can be in
 */
enum class BulkPaymentStatus {
    PENDING,        // Bulk payment created but not yet processed
    PROCESSING,     // Bulk payment is being processed
    COMPLETED,      // All payments in bulk completed successfully
    PARTIALLY_COMPLETED, // Some payments completed, some failed
    FAILED,         // All payments in bulk failed
    CANCELLED,      // Bulk payment was cancelled
    SCHEDULED,      // Bulk payment is scheduled for future processing
    VALIDATING,     // Bulk payment is being validated
    APPROVED,       // Bulk payment has been approved
    REJECTED        // Bulk payment has been rejected
}
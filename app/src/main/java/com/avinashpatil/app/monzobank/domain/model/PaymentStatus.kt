package com.avinashpatil.app.monzobank.domain.model

/**
 * Payment status enumeration
 * Defines the different states a payment can be in
 */
enum class PaymentStatus {
    // Initial states
    PENDING,           // Payment created but not yet processed
    INITIATED,         // Payment process has started
    AUTHORIZED,        // Payment has been authorized
    
    // Processing states
    PROCESSING,        // Payment is being processed
    VALIDATING,        // Payment is being validated
    AUTHENTICATING,    // Payment is being authenticated
    CLEARING,          // Payment is in clearing process
    SETTLING,          // Payment is being settled
    
    // Success states
    COMPLETED,         // Payment successfully completed
    SETTLED,           // Payment has been settled
    CONFIRMED,         // Payment confirmed by recipient
    
    // Failure states
    FAILED,            // Payment failed
    DECLINED,          // Payment declined by bank/processor
    REJECTED,          // Payment rejected due to validation
    CANCELLED,         // Payment cancelled by user
    EXPIRED,           // Payment expired (timeout)
    INSUFFICIENT_FUNDS, // Insufficient funds in account
    BLOCKED,           // Payment blocked by fraud detection
    
    // Hold states
    ON_HOLD,           // Payment on hold for review
    FROZEN,            // Payment frozen due to compliance
    QUARANTINED,       // Payment quarantined for investigation
    
    // Reversal states
    REVERSING,         // Payment reversal in progress
    REVERSED,          // Payment has been reversed
    REFUNDING,         // Refund in progress
    REFUNDED,          // Payment has been refunded
    DISPUTED,          // Payment is disputed
    CHARGEBACK,        // Chargeback initiated
    
    // Scheduled states
    SCHEDULED,         // Payment scheduled for future
    RECURRING,         // Recurring payment active
    PAUSED,            // Recurring payment paused
    
    // Error states
    ERROR,             // System error occurred
    TIMEOUT,           // Payment timed out
    NETWORK_ERROR,     // Network error occurred
    SYSTEM_ERROR,      // System error occurred
    
    // Compliance states
    COMPLIANCE_REVIEW, // Under compliance review
    AML_REVIEW,        // Under AML review
    SANCTIONS_CHECK,   // Under sanctions screening
    
    // Unknown state
    UNKNOWN            // Status unknown or not mapped
}
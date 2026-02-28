package com.avinashpatil.app.monzobank.domain.model

/**
 * Standing Order status enumeration
 * Defines the different states a standing order can be in
 */
enum class StandingOrderStatus {
    // Active states
    ACTIVE,            // Standing order is active and processing
    PENDING,           // Standing order created but not yet active
    SCHEDULED,         // Standing order scheduled to start
    
    // Processing states
    PROCESSING,        // Payment is being processed
    AWAITING_FUNDS,    // Waiting for sufficient funds
    
    // Paused states
    PAUSED,            // Standing order temporarily paused
    SUSPENDED,         // Standing order suspended by system
    ON_HOLD,           // Standing order on hold for review
    
    // Completed states
    COMPLETED,         // Standing order completed (all payments made)
    EXPIRED,           // Standing order expired (end date reached)
    
    // Cancelled states
    CANCELLED,         // Standing order cancelled by user
    TERMINATED,        // Standing order terminated by system
    REVOKED,           // Standing order revoked by bank
    
    // Failed states
    FAILED,            // Standing order failed
    INSUFFICIENT_FUNDS, // Insufficient funds for payment
    ACCOUNT_CLOSED,    // Source account closed
    INVALID_RECIPIENT, // Recipient account invalid
    BLOCKED,           // Standing order blocked
    
    // Review states
    UNDER_REVIEW,      // Under manual review
    COMPLIANCE_REVIEW, // Under compliance review
    FRAUD_REVIEW,      // Under fraud review
    
    // Error states
    ERROR,             // System error occurred
    TECHNICAL_FAILURE, // Technical failure
    NETWORK_ERROR,     // Network error
    
    // Dormant states
    DORMANT,           // Standing order dormant (no activity)
    INACTIVE,          // Standing order inactive
    
    // Unknown state
    UNKNOWN            // Status unknown or not mapped
}
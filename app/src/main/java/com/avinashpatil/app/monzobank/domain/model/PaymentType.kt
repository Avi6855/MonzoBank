package com.avinashpatil.app.monzobank.domain.model

/**
 * Payment type enumeration
 * Defines the different types of payments supported
 */
enum class PaymentType {
    // Basic transfer types
    TRANSFER,             // General transfer
    BANK_TRANSFER,        // Standard bank transfer
    INSTANT_TRANSFER,     // Instant bank transfer
    INTERNAL_TRANSFER,    // Transfer between own accounts
    EXTERNAL_TRANSFER,    // Transfer to external account
    
    // Card payments
    CARD_PAYMENT,         // General card payment
    DEBIT_CARD,          // Debit card payment
    CREDIT_CARD,         // Credit card payment
    CONTACTLESS,         // Contactless card payment
    CHIP_AND_PIN,        // Chip and PIN payment
    MAGNETIC_STRIPE,     // Magnetic stripe payment
    
    // Digital payments
    MOBILE_PAYMENT,       // Mobile payment (Apple Pay, Google Pay)
    DIGITAL_WALLET,       // Digital wallet payment
    QR_CODE,             // QR code payment
    NFC_PAYMENT,         // NFC payment
    
    // Online payments
    ONLINE_PAYMENT,       // Online payment
    E_COMMERCE,          // E-commerce payment
    SUBSCRIPTION,        // Subscription payment
    RECURRING_PAYMENT,   // Recurring payment
    
    // P2P payments
    PEER_TO_PEER,        // P2P payment
    SPLIT_PAYMENT,       // Split payment among multiple people
    REQUEST_PAYMENT,     // Payment request
    
    // Standing orders and direct debits
    STANDING_ORDER,      // Standing order payment
    DIRECT_DEBIT,        // Direct debit payment
    MANDATE_PAYMENT,     // Mandate-based payment
    
    // International payments
    INTERNATIONAL,       // International payment
    SWIFT_TRANSFER,      // SWIFT wire transfer
    CROSS_BORDER,        // Cross-border payment
    REMITTANCE,          // Remittance payment
    
    // Business payments
    PAYROLL,             // Payroll payment
    SUPPLIER_PAYMENT,    // Payment to supplier
    INVOICE_PAYMENT,     // Invoice payment
    BULK_PAYMENT,        // Bulk payment
    BATCH_PAYMENT,       // Batch payment
    
    // Government and utilities
    BILL_PAYMENT,        // General bill payment
    TAX_PAYMENT,         // Tax payment
    UTILITY_BILL,        // Utility bill payment
    GOVERNMENT_FEE,      // Government fee payment
    FINE_PAYMENT,        // Fine or penalty payment
    
    // Investment and savings
    INVESTMENT,          // Investment payment
    SAVINGS_DEPOSIT,     // Savings deposit
    PENSION_CONTRIBUTION, // Pension contribution
    INSURANCE_PREMIUM,   // Insurance premium payment
    
    // Loan and credit
    LOAN_PAYMENT,        // Loan payment
    MORTGAGE_PAYMENT,    // Mortgage payment
    CREDIT_PAYMENT,      // Credit payment
    INSTALLMENT,         // Installment payment
    
    // Refunds and reversals
    REFUND,              // Refund payment
    REVERSAL,            // Payment reversal
    CHARGEBACK,          // Chargeback payment
    DISPUTE_RESOLUTION,  // Dispute resolution payment
    
    // Cash and ATM
    CASH_DEPOSIT,        // Cash deposit
    CASH_WITHDRAWAL,     // Cash withdrawal
    ATM_WITHDRAWAL,      // ATM withdrawal
    ATM_DEPOSIT,         // ATM deposit
    
    // Fees and charges
    SERVICE_FEE,         // Service fee payment
    TRANSACTION_FEE,     // Transaction fee
    OVERDRAFT_FEE,       // Overdraft fee
    MAINTENANCE_FEE,     // Account maintenance fee
    
    // Other types
    DONATION,            // Donation payment
    GIFT,                // Gift payment
    REWARD,              // Reward payment
    CASHBACK,            // Cashback payment
    LOYALTY_POINTS,      // Loyalty points redemption
    
    // System types
    ADJUSTMENT,          // Account adjustment
    CORRECTION,          // Payment correction
    RECONCILIATION,      // Reconciliation payment
    
    // Unknown or custom
    OTHER,               // Other payment type
    UNKNOWN              // Unknown payment type
}
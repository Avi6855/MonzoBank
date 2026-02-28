package com.avinashpatil.app.monzobank.data.local.entity

/**
 * Enum representing different types of transactions
 */
enum class TransactionType {
    DEBIT,
    CREDIT,
    TRANSFER,
    TRANSFER_OUT,
    TRANSFER_IN,
    PAYMENT,
    WITHDRAWAL,
    DEPOSIT,
    DIRECT_DEBIT,
    STANDING_ORDER,
    CARD_PAYMENT,
    ATM_WITHDRAWAL,
    REFUND,
    FEE,
    INTEREST
}
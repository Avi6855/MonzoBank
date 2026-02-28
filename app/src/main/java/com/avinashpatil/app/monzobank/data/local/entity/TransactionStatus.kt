package com.avinashpatil.app.monzobank.data.local.entity

/**
 * Enum representing different transaction statuses
 */
enum class TransactionStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    DECLINED,
    CANCELLED,
    REVERSED,
    DISPUTED
}
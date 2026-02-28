package com.avinashpatil.app.monzobank.domain.model

/**
 * Domain enum representing different card statuses
 */
enum class CardStatus {
    ACTIVE,
    INACTIVE,
    FROZEN,
    BLOCKED,
    EXPIRED,
    CANCELLED,
    PENDING_ACTIVATION,
    SUSPENDED
}
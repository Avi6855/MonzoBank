package com.avinashpatil.app.monzobank.data.local.entity

/**
 * Enum representing different card delivery statuses
 */
enum class DeliveryStatus {
    PENDING,
    ORDERED,
    PROCESSING,
    DISPATCHED,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    FAILED_DELIVERY,
    RETURNED,
    CANCELLED
}
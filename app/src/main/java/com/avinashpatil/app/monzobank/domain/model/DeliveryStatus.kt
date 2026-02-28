package com.avinashpatil.app.monzobank.domain.model

/**
 * Domain enum representing different card delivery statuses
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
    DELIVERY_FAILED, // Alias for FAILED_DELIVERY
    RETURNED,
    CANCELLED
}
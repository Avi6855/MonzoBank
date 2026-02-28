package com.avinashpatil.app.monzobank.domain.model

/**
 * Payment Request Action enumeration
 * Defines the actions that can be taken on a payment request
 */
enum class PaymentRequestAction {
    ACCEPT,     // Accept the payment request
    DECLINE,    // Decline the payment request
    CANCEL,     // Cancel the payment request (by requester)
    MODIFY      // Modify the payment request
}
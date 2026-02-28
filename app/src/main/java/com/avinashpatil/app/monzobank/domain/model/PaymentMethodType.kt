package com.avinashpatil.app.monzobank.domain.model

/**
 * Domain enum representing different payment method types
 */
enum class PaymentMethodType {
    CARD,
    BANK_TRANSFER,
    DIRECT_DEBIT,
    STANDING_ORDER,
    CASH,
    CHEQUE,
    MOBILE_PAYMENT,
    ONLINE_PAYMENT,
    CONTACTLESS,
    CHIP_AND_PIN,
    APPLE_PAY,
    GOOGLE_PAY,
    PAYPAL,
    OTHER
}
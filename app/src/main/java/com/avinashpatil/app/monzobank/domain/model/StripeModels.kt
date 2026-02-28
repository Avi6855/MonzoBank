package com.avinashpatil.app.monzobank.domain.model

import java.math.BigDecimal
import android.util.Patterns

data class StripeCustomer(
    val id: String,
    val email: String,
    val name: String,
    val phone: String? = null,
    val created: Long,
    val defaultPaymentMethod: String? = null,
    val invoiceSettings: InvoiceSettings? = null
) {
    data class InvoiceSettings(
        val defaultPaymentMethod: String?
    )
}

data class PaymentIntent(
    val id: String,
    val amount: Long, // Amount in smallest currency unit (pence for GBP)
    val currency: String,
    val customerId: String,
    val description: String?,
    val status: String, // requires_payment_method, requires_confirmation, requires_action, processing, requires_capture, canceled, succeeded
    val clientSecret: String,
    val paymentMethodId: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val created: Long,
    val lastPaymentError: PaymentError? = null
) {
    val amountInPounds: BigDecimal
        get() = BigDecimal(amount).divide(BigDecimal(100))
    
    val isSuccessful: Boolean
        get() = status == "succeeded"
    
    val requiresAction: Boolean
        get() = status == "requires_action"
    
    val isCanceled: Boolean
        get() = status == "canceled"
}

data class PaymentMethod(
    val id: String,
    val type: String, // card, bank_account, etc.
    val customerId: String?,
    val card: Card?,
    val billingDetails: BillingDetails,
    val created: Long
) {
    data class Card(
        val brand: String, // visa, mastercard, amex, etc.
        val last4: String,
        val expiryMonth: Int,
        val expiryYear: Int,
        val fingerprint: String,
        val funding: String = "credit", // credit, debit, prepaid
        val country: String? = null
    ) {
        val displayBrand: String
            get() = when (brand.lowercase()) {
                "visa" -> "Visa"
                "mastercard" -> "Mastercard"
                "amex" -> "American Express"
                "discover" -> "Discover"
                "diners" -> "Diners Club"
                "jcb" -> "JCB"
                "unionpay" -> "UnionPay"
                else -> brand.replaceFirstChar { it.uppercase() }
            }
        
        val isExpired: Boolean
            get() {
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
                return expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)
            }
        
        val expiryDisplay: String
            get() = String.format("%02d/%02d", expiryMonth, expiryYear % 100)
    }
    
    data class BillingDetails(
        val name: String?,
        val email: String? = null,
        val phone: String? = null,
        val address: Address? = null
    )
    
    data class Address(
        val line1: String?,
        val line2: String? = null,
        val city: String?,
        val state: String? = null,
        val postalCode: String?,
        val country: String?
    )
}

data class PaymentError(
    val code: String,
    val message: String,
    val type: String, // card_error, invalid_request_error, etc.
    val declineCode: String? = null,
    val param: String? = null
) {
    val isCardError: Boolean
        get() = type == "card_error"
    
    val isDeclined: Boolean
        get() = declineCode != null
    
    val userFriendlyMessage: String
        get() = when (code) {
            "card_declined" -> "Your card was declined. Please try a different payment method."
            "expired_card" -> "Your card has expired. Please use a different card."
            "incorrect_cvc" -> "The security code is incorrect. Please check and try again."
            "insufficient_funds" -> "Insufficient funds. Please check your account balance."
            "invalid_expiry_month" -> "The expiry month is invalid."
            "invalid_expiry_year" -> "The expiry year is invalid."
            "invalid_number" -> "The card number is invalid."
            "processing_error" -> "An error occurred while processing your card. Please try again."
            "rate_limit" -> "Too many requests. Please wait a moment and try again."
            else -> message
        }
}

data class SetupIntent(
    val id: String,
    val clientSecret: String,
    val customerId: String,
    val status: String, // requires_payment_method, requires_confirmation, requires_action, processing, canceled, succeeded
    val paymentMethodId: String? = null,
    val created: Long,
    val lastSetupError: PaymentError? = null
) {
    val isSuccessful: Boolean
        get() = status == "succeeded"
    
    val requiresAction: Boolean
        get() = status == "requires_action"
}

data class Refund(
    val id: String,
    val paymentIntentId: String,
    val amount: Long,
    val currency: String,
    val reason: String?, // duplicate, fraudulent, requested_by_customer
    val status: String, // pending, succeeded, failed, canceled
    val created: Long,
    val metadata: Map<String, String> = emptyMap()
) {
    val amountInPounds: BigDecimal
        get() = BigDecimal(amount).divide(BigDecimal(100))
    
    val isSuccessful: Boolean
        get() = status == "succeeded"
    
    val isPending: Boolean
        get() = status == "pending"
}

data class StripeWebhookEvent(
    val id: String,
    val type: String, // payment_intent.succeeded, payment_method.attached, etc.
    val data: Map<String, Any>,
    val created: Long,
    val livemode: Boolean = false
)

// Enums for better type safety
enum class PaymentIntentStatus {
    REQUIRES_PAYMENT_METHOD,
    REQUIRES_CONFIRMATION,
    REQUIRES_ACTION,
    PROCESSING,
    REQUIRES_CAPTURE,
    CANCELED,
    SUCCEEDED;
    
    companion object {
        fun fromString(status: String): PaymentIntentStatus {
            return when (status.lowercase()) {
                "requires_payment_method" -> REQUIRES_PAYMENT_METHOD
                "requires_confirmation" -> REQUIRES_CONFIRMATION
                "requires_action" -> REQUIRES_ACTION
                "processing" -> PROCESSING
                "requires_capture" -> REQUIRES_CAPTURE
                "canceled" -> CANCELED
                "succeeded" -> SUCCEEDED
                else -> REQUIRES_PAYMENT_METHOD
            }
        }
    }
}

enum class CardBrand(val displayName: String) {
    VISA("Visa"),
    MASTERCARD("Mastercard"),
    AMEX("American Express"),
    DISCOVER("Discover"),
    DINERS("Diners Club"),
    JCB("JCB"),
    UNIONPAY("UnionPay"),
    UNKNOWN("Unknown");
    
    companion object {
        fun fromString(brand: String): CardBrand {
            return when (brand.lowercase()) {
                "visa" -> VISA
                "mastercard" -> MASTERCARD
                "amex" -> AMEX
                "discover" -> DISCOVER
                "diners" -> DINERS
                "jcb" -> JCB
                "unionpay" -> UNIONPAY
                else -> UNKNOWN
            }
        }
    }
}

// PaymentMethodType enum is defined in PaymentMethodType.kt to avoid conflicts

// Request/Response DTOs for API calls
data class CreatePaymentIntentRequest(
    val amount: Long,
    val currency: String = "gbp",
    val customerId: String,
    val description: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val paymentMethodTypes: List<String> = listOf("card"),
    val captureMethod: String = "automatic",
    val confirmationMethod: String = "automatic"
)

data class CreateCustomerRequest(
    val email: String,
    val name: String,
    val phone: String? = null,
    val description: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

data class CreatePaymentMethodRequest(
    val type: String = "card",
    val card: CardDetails,
    val billingDetails: BillingDetailsRequest? = null
) {
    data class CardDetails(
        val number: String,
        val expMonth: Int,
        val expYear: Int,
        val cvc: String
    )
    
    data class BillingDetailsRequest(
        val name: String?,
        val email: String? = null,
        val phone: String? = null,
        val address: AddressRequest? = null
    )
    
    data class AddressRequest(
        val line1: String?,
        val line2: String? = null,
        val city: String?,
        val state: String? = null,
        val postalCode: String?,
        val country: String?
    )
}

data class ConfirmPaymentIntentRequest(
    val paymentMethodId: String,
    val returnUrl: String? = null
)

data class CreateRefundRequest(
    val paymentIntentId: String,
    val amount: Long? = null,
    val reason: String? = null,
    val metadata: Map<String, String> = emptyMap()
)

// Utility classes for validation and formatting
object StripeUtils {
    fun formatAmount(amountInPence: Long): String {
        val pounds = BigDecimal(amountInPence).divide(BigDecimal(100))
        return "£${pounds.setScale(2)}"
    }
    
    fun parseAmount(amountString: String): Long? {
        return try {
            val cleanAmount = amountString.replace("£", "").replace(",", "")
            val pounds = BigDecimal(cleanAmount)
            (pounds * BigDecimal(100)).toLong()
        } catch (e: Exception) {
            null
        }
    }
    
    fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 4) return cardNumber
        val last4 = cardNumber.takeLast(4)
        val masked = "*".repeat(cardNumber.length - 4)
        return "$masked$last4"
    }
    
    fun formatCardNumber(cardNumber: String): String {
        val clean = cardNumber.replace("\\s".toRegex(), "")
        return clean.chunked(4).joinToString(" ")
    }
    
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

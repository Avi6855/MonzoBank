package com.avinashpatil.app.monzobank.data.service

import com.avinashpatil.app.monzobank.domain.model.PaymentIntent
import com.avinashpatil.app.monzobank.domain.model.PaymentMethod
import com.avinashpatil.app.monzobank.domain.model.StripeCustomer
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface StripeService {
    suspend fun createCustomer(
        email: String,
        name: String,
        phone: String? = null
    ): Result<StripeCustomer>
    
    suspend fun createPaymentIntent(
        amount: BigDecimal,
        currency: String = "gbp",
        customerId: String,
        description: String? = null,
        metadata: Map<String, String> = emptyMap()
    ): Result<PaymentIntent>
    
    suspend fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentMethodId: String
    ): Result<PaymentIntent>
    
    suspend fun createPaymentMethod(
        customerId: String,
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardholderName: String
    ): Result<PaymentMethod>
    
    suspend fun attachPaymentMethod(
        paymentMethodId: String,
        customerId: String
    ): Result<PaymentMethod>
    
    suspend fun detachPaymentMethod(
        paymentMethodId: String
    ): Result<PaymentMethod>
    
    suspend fun getCustomerPaymentMethods(
        customerId: String
    ): Result<List<PaymentMethod>>
    
    suspend fun processRefund(
        paymentIntentId: String,
        amount: BigDecimal? = null,
        reason: String? = null
    ): Result<String>
    
    suspend fun getPaymentIntent(
        paymentIntentId: String
    ): Result<PaymentIntent>
    
    suspend fun cancelPaymentIntent(
        paymentIntentId: String
    ): Result<PaymentIntent>
    
    suspend fun createSetupIntent(
        customerId: String,
        paymentMethodTypes: List<String> = listOf("card")
    ): Result<String>
    
    suspend fun validateCard(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String
    ): Result<Boolean>
}

class StripeServiceImpl : StripeService {
    
    companion object {
        private const val BASE_URL = "https://api.stripe.com/v1/"
        private const val PUBLISHABLE_KEY = "pk_test_your_publishable_key_here"
        private const val SECRET_KEY = "sk_test_your_secret_key_here"
    }
    
    override suspend fun createCustomer(
        email: String,
        name: String,
        phone: String?
    ): Result<StripeCustomer> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val customer = StripeCustomer(
                id = "cus_${System.currentTimeMillis()}",
                email = email,
                name = name,
                phone = phone,
                created = System.currentTimeMillis() / 1000
            )
            Result.success(customer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPaymentIntent(
        amount: BigDecimal,
        currency: String,
        customerId: String,
        description: String?,
        metadata: Map<String, String>
    ): Result<PaymentIntent> {
        return try {
            // Convert amount to smallest currency unit (pence for GBP)
            val amountInPence = (amount * BigDecimal(100)).toLong()
            
            // Mock implementation - replace with actual Stripe API call
            val paymentIntent = PaymentIntent(
                id = "pi_${System.currentTimeMillis()}",
                amount = amountInPence,
                currency = currency,
                customerId = customerId,
                description = description,
                status = "requires_payment_method",
                clientSecret = "pi_${System.currentTimeMillis()}_secret_${System.currentTimeMillis()}",
                metadata = metadata,
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun confirmPaymentIntent(
        paymentIntentId: String,
        paymentMethodId: String
    ): Result<PaymentIntent> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentIntent = PaymentIntent(
                id = paymentIntentId,
                amount = 1000L, // Mock amount
                currency = "gbp",
                customerId = "cus_mock",
                description = "Payment confirmation",
                status = "succeeded",
                clientSecret = "${paymentIntentId}_secret",
                metadata = emptyMap(),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createPaymentMethod(
        customerId: String,
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String,
        cardholderName: String
    ): Result<PaymentMethod> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentMethod = PaymentMethod(
                id = "pm_${System.currentTimeMillis()}",
                type = "card",
                customerId = customerId,
                card = PaymentMethod.Card(
                    brand = detectCardBrand(cardNumber),
                    last4 = cardNumber.takeLast(4),
                    expiryMonth = expiryMonth,
                    expiryYear = expiryYear,
                    fingerprint = "fp_${System.currentTimeMillis()}"
                ),
                billingDetails = PaymentMethod.BillingDetails(
                    name = cardholderName
                ),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentMethod)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun attachPaymentMethod(
        paymentMethodId: String,
        customerId: String
    ): Result<PaymentMethod> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentMethod = PaymentMethod(
                id = paymentMethodId,
                type = "card",
                customerId = customerId,
                card = PaymentMethod.Card(
                    brand = "visa",
                    last4 = "4242",
                    expiryMonth = 12,
                    expiryYear = 2025,
                    fingerprint = "fp_mock"
                ),
                billingDetails = PaymentMethod.BillingDetails(
                    name = "Mock Cardholder"
                ),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentMethod)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun detachPaymentMethod(
        paymentMethodId: String
    ): Result<PaymentMethod> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentMethod = PaymentMethod(
                id = paymentMethodId,
                type = "card",
                customerId = null,
                card = PaymentMethod.Card(
                    brand = "visa",
                    last4 = "4242",
                    expiryMonth = 12,
                    expiryYear = 2025,
                    fingerprint = "fp_mock"
                ),
                billingDetails = PaymentMethod.BillingDetails(
                    name = "Mock Cardholder"
                ),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentMethod)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCustomerPaymentMethods(
        customerId: String
    ): Result<List<PaymentMethod>> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentMethods = listOf(
                PaymentMethod(
                    id = "pm_mock_1",
                    type = "card",
                    customerId = customerId,
                    card = PaymentMethod.Card(
                        brand = "visa",
                        last4 = "4242",
                        expiryMonth = 12,
                        expiryYear = 2025,
                        fingerprint = "fp_mock_1"
                    ),
                    billingDetails = PaymentMethod.BillingDetails(
                        name = "John Doe"
                    ),
                    created = System.currentTimeMillis() / 1000
                ),
                PaymentMethod(
                    id = "pm_mock_2",
                    type = "card",
                    customerId = customerId,
                    card = PaymentMethod.Card(
                        brand = "mastercard",
                        last4 = "5555",
                        expiryMonth = 6,
                        expiryYear = 2026,
                        fingerprint = "fp_mock_2"
                    ),
                    billingDetails = PaymentMethod.BillingDetails(
                        name = "John Doe"
                    ),
                    created = System.currentTimeMillis() / 1000
                )
            )
            Result.success(paymentMethods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun processRefund(
        paymentIntentId: String,
        amount: BigDecimal?,
        reason: String?
    ): Result<String> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val refundId = "re_${System.currentTimeMillis()}"
            Result.success(refundId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPaymentIntent(
        paymentIntentId: String
    ): Result<PaymentIntent> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentIntent = PaymentIntent(
                id = paymentIntentId,
                amount = 1000L,
                currency = "gbp",
                customerId = "cus_mock",
                description = "Mock payment",
                status = "succeeded",
                clientSecret = "${paymentIntentId}_secret",
                metadata = emptyMap(),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun cancelPaymentIntent(
        paymentIntentId: String
    ): Result<PaymentIntent> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val paymentIntent = PaymentIntent(
                id = paymentIntentId,
                amount = 1000L,
                currency = "gbp",
                customerId = "cus_mock",
                description = "Cancelled payment",
                status = "canceled",
                clientSecret = "${paymentIntentId}_secret",
                metadata = emptyMap(),
                created = System.currentTimeMillis() / 1000
            )
            Result.success(paymentIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createSetupIntent(
        customerId: String,
        paymentMethodTypes: List<String>
    ): Result<String> {
        return try {
            // Mock implementation - replace with actual Stripe API call
            val setupIntentId = "seti_${System.currentTimeMillis()}"
            Result.success(setupIntentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateCard(
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        cvc: String
    ): Result<Boolean> {
        return try {
            // Basic validation logic
            val isValidNumber = isValidCardNumber(cardNumber)
            val isValidExpiry = isValidExpiry(expiryMonth, expiryYear)
            val isValidCvc = isValidCvc(cvc)
            
            val isValid = isValidNumber && isValidExpiry && isValidCvc
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun detectCardBrand(cardNumber: String): String {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        return when {
            cleanNumber.startsWith("4") -> "visa"
            cleanNumber.startsWith("5") || cleanNumber.startsWith("2") -> "mastercard"
            cleanNumber.startsWith("3") -> "amex"
            cleanNumber.startsWith("6") -> "discover"
            else -> "unknown"
        }
    }
    
    private fun isValidCardNumber(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace("\\s".toRegex(), "")
        if (cleanNumber.length < 13 || cleanNumber.length > 19) return false
        
        // Luhn algorithm
        var sum = 0
        var alternate = false
        
        for (i in cleanNumber.length - 1 downTo 0) {
            var n = cleanNumber[i].toString().toInt()
            if (alternate) {
                n *= 2
                if (n > 9) n = (n % 10) + 1
            }
            sum += n
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
    
    private fun isValidExpiry(month: Int, year: Int): Boolean {
        if (month < 1 || month > 12) return false
        
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1
        
        return when {
            year < currentYear -> false
            year == currentYear -> month >= currentMonth
            else -> true
        }
    }
    
    private fun isValidCvc(cvc: String): Boolean {
        return cvc.length in 3..4 && cvc.all { it.isDigit() }
    }
}
package com.avinashpatil.app.monzobank.domain.model

import com.avinashpatil.app.monzobank.domain.model.CardStatus
import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import java.math.BigDecimal
import java.util.Date

data class Card(
    val id: String,
    val userId: String,
    val accountId: String,
    val cardNumber: String,
    val cardType: CardType,
    val expiryDate: Date,
    val cvv: String,
    val isActive: Boolean = true,
    val isFrozen: Boolean = false,
    val contactlessEnabled: Boolean = true,
    val dailyLimit: BigDecimal = BigDecimal("500.00"),
    val monthlyLimit: BigDecimal = BigDecimal("2000.00"),
    val internationalEnabled: Boolean = false,
    val onlineEnabled: Boolean = true,
    val atmEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val cardHolderName: String,
    val cardName: String? = null,
    val lastUsedAt: Date? = null,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.PENDING,
    val cardDesign: String? = null,
    val pinHash: String? = null,
    val trackingNumber: String? = null,
    val estimatedDelivery: Date? = null,
    val deliveryAddress: String? = null,
    val isLocked: Boolean = false,
    val lockReason: String? = null,
    val status: CardStatus = CardStatus.ACTIVE
) {
    val maskedCardNumber: String
        get() = "**** **** **** ${cardNumber.takeLast(4)}"
    
    val displayName: String
        get() = cardName ?: when (cardType) {
            CardType.DEBIT -> "Debit Card"
            CardType.CREDIT -> "Credit Card"
            CardType.VIRTUAL -> "Virtual Card"
            CardType.PREPAID -> "Prepaid Card"
            CardType.BUSINESS_DEBIT -> "Business Debit Card"
            CardType.BUSINESS_CREDIT -> "Business Credit Card"
            CardType.PREMIUM -> "Premium Card"
        }
    
    val statusText: String
        get() = when {
            !isActive -> "Inactive"
            isFrozen -> "Frozen"
            deliveryStatus == DeliveryStatus.PENDING -> "Being Prepared"
            deliveryStatus == DeliveryStatus.DISPATCHED -> "On the Way"
            deliveryStatus == DeliveryStatus.DELIVERED -> "Active"
            else -> "Active"
        }
    
    val statusColor: String
        get() = when {
            !isActive -> "#8E8E93"
            isFrozen -> "#FF9500"
            deliveryStatus == DeliveryStatus.DELIVERED -> "#00D924"
            deliveryStatus in listOf(DeliveryStatus.PENDING, DeliveryStatus.DISPATCHED) -> "#007AFF"
            else -> "#00D924"
        }
    
    val canBeUsed: Boolean
        get() = isActive && !isFrozen && deliveryStatus == DeliveryStatus.DELIVERED
    
    val isExpired: Boolean
        get() = expiryDate.before(Date())
    
    val expiryText: String
        get() {
            val calendar = java.util.Calendar.getInstance()
            calendar.time = expiryDate
            val month = String.format("%02d", calendar.get(java.util.Calendar.MONTH) + 1)
            val year = calendar.get(java.util.Calendar.YEAR).toString().takeLast(2)
            return "$month/$year"
        }
    
    val cardIcon: String
        get() = when (cardType) {
            CardType.DEBIT -> "💳"
            CardType.CREDIT -> "💎"
            CardType.VIRTUAL -> "📱"
            CardType.PREPAID -> "🎫"
            CardType.BUSINESS_DEBIT -> "🏢"
            CardType.BUSINESS_CREDIT -> "💼"
            CardType.PREMIUM -> "⭐"
        }
    
    val formattedDailyLimit: String
        get() = "£${dailyLimit.setScale(2)}"
    
    val formattedMonthlyLimit: String
        get() = "£${monthlyLimit.setScale(2)}"
}

// CardType, CardStatus, and DeliveryStatus enums moved to data.local.entity package to avoid conflicts
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.CardType
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.CardStatus
// Import them from: com.avinashpatil.app.monzobank.data.local.entity.DeliveryStatus

data class CardUsage(
    val cardId: String,
    val dailySpent: BigDecimal = BigDecimal.ZERO,
    val monthlySpent: BigDecimal = BigDecimal.ZERO,
    val transactionCount: Int = 0,
    val lastTransactionDate: Date? = null
)
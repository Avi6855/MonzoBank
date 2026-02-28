package com.avinashpatil.app.monzobank.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.Date

data class CardDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("card_number")
    val cardNumber: String,
    @SerializedName("card_type")
    val cardType: String,
    @SerializedName("expiry_date")
    val expiryDate: Date,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("is_frozen")
    val isFrozen: Boolean = false,
    @SerializedName("contactless_enabled")
    val contactlessEnabled: Boolean = true,
    @SerializedName("daily_limit")
    val dailyLimit: BigDecimal = BigDecimal("500.00"),
    @SerializedName("monthly_limit")
    val monthlyLimit: BigDecimal = BigDecimal("2000.00"),
    @SerializedName("international_enabled")
    val internationalEnabled: Boolean = false,
    @SerializedName("online_enabled")
    val onlineEnabled: Boolean = true,
    @SerializedName("atm_enabled")
    val atmEnabled: Boolean = true,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date,
    @SerializedName("card_holder_name")
    val cardHolderName: String,
    @SerializedName("card_name")
    val cardName: String? = null,
    @SerializedName("last_used_at")
    val lastUsedAt: Date? = null,
    @SerializedName("delivery_status")
    val deliveryStatus: String = "PENDING",
    @SerializedName("card_design")
    val cardDesign: String? = null,
    @SerializedName("tracking_number")
    val trackingNumber: String? = null,
    @SerializedName("estimated_delivery")
    val estimatedDelivery: Date? = null,
    @SerializedName("delivery_address")
    val deliveryAddress: String? = null,
    @SerializedName("is_locked")
    val isLocked: Boolean = false,
    @SerializedName("lock_reason")
    val lockReason: String? = null
)

data class CreateCardRequestDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("card_type")
    val cardType: String,
    @SerializedName("card_holder_name")
    val cardHolderName: String,
    @SerializedName("card_name")
    val cardName: String? = null,
    @SerializedName("card_design")
    val cardDesign: String? = null,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("pin")
    val pin: String? = null
)

data class UpdateCardRequestDto(
    @SerializedName("card_name")
    val cardName: String? = null,
    @SerializedName("daily_limit")
    val dailyLimit: BigDecimal? = null,
    @SerializedName("monthly_limit")
    val monthlyLimit: BigDecimal? = null,
    @SerializedName("contactless_enabled")
    val contactlessEnabled: Boolean? = null,
    @SerializedName("international_enabled")
    val internationalEnabled: Boolean? = null,
    @SerializedName("online_enabled")
    val onlineEnabled: Boolean? = null,
    @SerializedName("atm_enabled")
    val atmEnabled: Boolean? = null
)

data class CardControlsDto(
    @SerializedName("card_id")
    val cardId: String,
    @SerializedName("is_frozen")
    val isFrozen: Boolean,
    @SerializedName("is_locked")
    val isLocked: Boolean,
    @SerializedName("contactless_enabled")
    val contactlessEnabled: Boolean,
    @SerializedName("international_enabled")
    val internationalEnabled: Boolean,
    @SerializedName("online_enabled")
    val onlineEnabled: Boolean,
    @SerializedName("atm_enabled")
    val atmEnabled: Boolean,
    @SerializedName("daily_limit")
    val dailyLimit: BigDecimal,
    @SerializedName("monthly_limit")
    val monthlyLimit: BigDecimal
)

data class CardTransactionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("card_id")
    val cardId: String,
    @SerializedName("amount")
    val amount: BigDecimal,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("merchant_name")
    val merchantName: String? = null,
    @SerializedName("merchant_category")
    val merchantCategory: String? = null,
    @SerializedName("transaction_date")
    val transactionDate: Date,
    @SerializedName("status")
    val status: String,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("is_contactless")
    val isContactless: Boolean = false,
    @SerializedName("is_online")
    val isOnline: Boolean = false
)

data class FreezeCardRequestDto(
    @SerializedName("reason")
    val reason: String? = null
)

data class UnfreezeCardRequestDto(
    @SerializedName("pin_verification")
    val pinVerification: String? = null
)

data class ChangePinRequestDto(
    @SerializedName("current_pin")
    val currentPin: String,
    @SerializedName("new_pin")
    val newPin: String
)

data class CardDeliveryDto(
    @SerializedName("card_id")
    val cardId: String,
    @SerializedName("delivery_status")
    val deliveryStatus: String,
    @SerializedName("tracking_number")
    val trackingNumber: String? = null,
    @SerializedName("estimated_delivery")
    val estimatedDelivery: Date? = null,
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    @SerializedName("courier_name")
    val courierName: String? = null,
    @SerializedName("delivery_instructions")
    val deliveryInstructions: String? = null
)
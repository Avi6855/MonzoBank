package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for creating an international transfer
 */
data class CreateInternationalTransferDto(
    @SerializedName("from_account_id")
    val fromAccountId: String,
    
    @SerializedName("recipient_details")
    val recipientDetails: RecipientDto,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("purpose")
    val purpose: String,
    
    @SerializedName("reference")
    val reference: String,
    
    @SerializedName("exchange_rate")
    val exchangeRate: Double? = null,
    
    @SerializedName("fees")
    val fees: Double? = null
)

/**
 * DTO for international transfer recipient details
 */
data class RecipientDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("account_number")
    val accountNumber: String,
    
    @SerializedName("bank_code")
    val bankCode: String, // SWIFT/IBAN/etc
    
    @SerializedName("bank_name")
    val bankName: String,
    
    @SerializedName("country")
    val country: String,
    
    @SerializedName("address")
    val address: String? = null
)
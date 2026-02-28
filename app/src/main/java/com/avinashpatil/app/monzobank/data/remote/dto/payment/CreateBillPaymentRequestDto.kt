package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for creating a bill payment request
 */
data class CreateBillPaymentRequestDto(
    @SerializedName("payee_id")
    val payeeId: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("account_id")
    val accountId: String,
    
    @SerializedName("reference")
    val reference: String,
    
    @SerializedName("scheduled_date")
    val scheduledDate: String?,
    
    @SerializedName("bill_type")
    val billType: String? = null, // UTILITY, CREDIT_CARD, MORTGAGE, etc.
    
    @SerializedName("recurring")
    val recurring: Boolean = false
)
package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for creating a transfer request
 */
data class CreateTransferRequestDto(
    @SerializedName("from_account_id")
    val fromAccountId: String,
    
    @SerializedName("to_account_id")
    val toAccountId: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("scheduled_date")
    val scheduledDate: String?,
    
    @SerializedName("transfer_type")
    val transferType: String = "INSTANT" // INSTANT, SCHEDULED
)
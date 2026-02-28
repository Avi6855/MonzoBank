package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for payee information
 */
data class PayeeDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("account_number")
    val accountNumber: String,
    
    @SerializedName("sort_code")
    val sortCode: String,
    
    @SerializedName("type")
    val type: String, // PERSONAL, BUSINESS, UTILITY
    
    @SerializedName("bank_name")
    val bankName: String? = null,
    
    @SerializedName("reference")
    val reference: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("last_payment_date")
    val lastPaymentDate: String? = null
)
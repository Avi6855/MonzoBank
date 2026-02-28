package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for adding a new payee
 */
data class AddPayeeRequestDto(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("account_number")
    val accountNumber: String,
    
    @SerializedName("sort_code")
    val sortCode: String,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("type")
    val type: String = "PERSONAL", // PERSONAL, BUSINESS, UTILITY
    
    @SerializedName("bank_name")
    val bankName: String? = null,
    
    @SerializedName("verify_account")
    val verifyAccount: Boolean = true
)
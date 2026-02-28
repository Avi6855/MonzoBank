package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName

/**
 * DTO for updating payee information
 */
data class UpdatePayeeRequestDto(
    @SerializedName("name")
    val name: String?,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("account_number")
    val accountNumber: String? = null,
    
    @SerializedName("sort_code")
    val sortCode: String? = null,
    
    @SerializedName("type")
    val type: String? = null,
    
    @SerializedName("bank_name")
    val bankName: String? = null
)
package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for updating card spending limits
 */
data class UpdateCardLimitsDto(
    @SerializedName("daily_limit")
    val dailyLimit: Double?,
    
    @SerializedName("monthly_limit")
    val monthlyLimit: Double?,
    
    @SerializedName("transaction_limit")
    val transactionLimit: Double?,
    
    @SerializedName("atm_limit")
    val atmLimit: Double? = null,
    
    @SerializedName("online_limit")
    val onlineLimit: Double? = null
)
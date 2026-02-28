package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for changing card PIN
 */
data class ChangeCardPinDto(
    @SerializedName("current_pin")
    val currentPin: String,
    
    @SerializedName("new_pin")
    val newPin: String,
    
    @SerializedName("confirm_pin")
    val confirmPin: String? = null
)
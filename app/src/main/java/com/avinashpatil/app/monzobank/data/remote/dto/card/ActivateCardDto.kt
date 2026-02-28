package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for activating a new card
 */
data class ActivateCardDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("activation_code")
    val activationCode: String,
    
    @SerializedName("pin")
    val pin: String? = null,
    
    @SerializedName("device_id")
    val deviceId: String? = null
)
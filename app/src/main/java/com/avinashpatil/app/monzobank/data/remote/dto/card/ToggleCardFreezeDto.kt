package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for toggling card freeze status
 */
data class ToggleCardFreezeDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("freeze")
    val freeze: Boolean,
    
    @SerializedName("reason")
    val reason: String? = null
)
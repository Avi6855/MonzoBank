package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for toggling card block status
 */
data class ToggleCardBlockDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("block")
    val block: Boolean,
    
    @SerializedName("reason")
    val reason: String,
    
    @SerializedName("block_type")
    val blockType: String? = null // TEMPORARY, PERMANENT
)
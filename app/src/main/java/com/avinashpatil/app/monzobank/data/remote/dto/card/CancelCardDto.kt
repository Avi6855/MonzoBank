package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for cancelling a card
 */
data class CancelCardDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("reason")
    val reason: String,
    
    @SerializedName("immediate_cancellation")
    val immediateCancellation: Boolean = false,
    
    @SerializedName("transfer_balance")
    val transferBalance: Boolean = true
)
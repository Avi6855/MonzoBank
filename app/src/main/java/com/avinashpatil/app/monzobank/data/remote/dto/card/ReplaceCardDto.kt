package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for requesting card replacement
 */
data class ReplaceCardDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("reason")
    val reason: String,
    
    @SerializedName("delivery_address")
    val deliveryAddress: AddressDto,
    
    @SerializedName("expedited_delivery")
    val expeditedDelivery: Boolean = false,
    
    @SerializedName("same_pin")
    val samePin: Boolean = true
)

/**
 * DTO for address information
 */
data class AddressDto(
    @SerializedName("line1")
    val line1: String,
    
    @SerializedName("line2")
    val line2: String? = null,
    
    @SerializedName("city")
    val city: String,
    
    @SerializedName("postal_code")
    val postalCode: String,
    
    @SerializedName("country")
    val country: String
)
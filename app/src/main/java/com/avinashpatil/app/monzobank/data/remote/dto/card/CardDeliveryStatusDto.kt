package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for card delivery status information
 */
data class CardDeliveryStatusDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("tracking_number")
    val trackingNumber: String?,
    
    @SerializedName("estimated_delivery")
    val estimatedDelivery: String?,
    
    @SerializedName("courier")
    val courier: String? = null,
    
    @SerializedName("delivery_address")
    val deliveryAddress: AddressDto? = null,
    
    @SerializedName("last_updated")
    val lastUpdated: String? = null
)
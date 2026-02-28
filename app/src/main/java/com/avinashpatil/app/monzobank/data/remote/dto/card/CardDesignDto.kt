package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for card design options
 */
data class CardDesignDto(
    @SerializedName("design_id")
    val designId: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("image_url")
    val imageUrl: String,
    
    @SerializedName("available")
    val available: Boolean,
    
    @SerializedName("premium")
    val premium: Boolean = false,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("price")
    val price: Double? = null
)
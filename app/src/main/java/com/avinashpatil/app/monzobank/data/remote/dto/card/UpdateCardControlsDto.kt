package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for updating card control settings
 */
data class UpdateCardControlsDto(
    @SerializedName("contactless_enabled")
    val contactlessEnabled: Boolean,
    
    @SerializedName("online_payments_enabled")
    val onlinePaymentsEnabled: Boolean,
    
    @SerializedName("atm_withdrawals_enabled")
    val atmWithdrawalsEnabled: Boolean,
    
    @SerializedName("magnetic_stripe_enabled")
    val magneticStripeEnabled: Boolean,
    
    @SerializedName("international_payments_enabled")
    val internationalPaymentsEnabled: Boolean = true,
    
    @SerializedName("gambling_payments_enabled")
    val gamblingPaymentsEnabled: Boolean = true
)
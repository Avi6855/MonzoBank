package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class UpdateRecurringTransactionDto(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("frequency")
    val frequency: String?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("description")
    val description: String?
)
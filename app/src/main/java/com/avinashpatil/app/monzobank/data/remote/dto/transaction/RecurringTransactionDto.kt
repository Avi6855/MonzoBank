package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class RecurringTransactionDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("frequency")
    val frequency: String,
    @SerializedName("next_date")
    val nextDate: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("status")
    val status: String
)
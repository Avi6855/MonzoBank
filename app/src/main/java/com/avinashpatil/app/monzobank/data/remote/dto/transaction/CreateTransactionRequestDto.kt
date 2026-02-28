package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class CreateTransactionRequestDto(
    @SerializedName("account_id")
    val accountId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("type")
    val type: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("merchant_id")
    val merchantId: String?
)
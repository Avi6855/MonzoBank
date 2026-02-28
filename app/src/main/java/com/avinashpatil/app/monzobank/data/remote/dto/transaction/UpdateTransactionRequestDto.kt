package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class UpdateTransactionRequestDto(
    @SerializedName("description")
    val description: String?,
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("notes")
    val notes: String?
)
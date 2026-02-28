package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class TransactionCategoryDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("icon")
    val icon: String,
    @SerializedName("color")
    val color: String,
    @SerializedName("parent_id")
    val parentId: String?
)
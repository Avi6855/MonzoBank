package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class TransactionReceiptDto(
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("receipt_url")
    val receiptUrl: String,
    @SerializedName("merchant_name")
    val merchantName: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("items")
    val items: List<ReceiptItemDto>
)

data class ReceiptItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)
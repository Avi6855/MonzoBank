package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for card transaction list response
 */
data class TransactionListDto(
    @SerializedName("transactions")
    val transactions: List<CardTransactionDto>,
    
    @SerializedName("pagination")
    val pagination: PaginationDto,
    
    @SerializedName("total_count")
    val totalCount: Int = 0
)

/**
 * DTO for individual card transaction
 */
data class CardTransactionDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("merchant_name")
    val merchantName: String,
    
    @SerializedName("category")
    val category: String,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("status")
    val status: String
)

/**
 * DTO for pagination information
 */
data class PaginationDto(
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("per_page")
    val perPage: Int,
    
    @SerializedName("total_pages")
    val totalPages: Int,
    
    @SerializedName("has_next")
    val hasNext: Boolean,
    
    @SerializedName("has_previous")
    val hasPrevious: Boolean
)
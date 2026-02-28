package com.avinashpatil.app.monzobank.data.remote.dto.common

import com.google.gson.annotations.SerializedName

/**
 * Base response DTO for API responses
 */
data class BaseResponseDto(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: String? = null,
    @SerializedName("error_code")
    val errorCode: String? = null
)

/**
 * Pagination information for list responses
 */
data class PaginationDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("has_next")
    val hasNext: Boolean,
    @SerializedName("has_previous")
    val hasPrevious: Boolean
)



/**
 * Receipt item data
 */
data class ReceiptItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double
)

/**
 * Split bill participant data
 */
data class SplitParticipantDto(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("status")
    val status: String // PENDING, PAID, DECLINED
)
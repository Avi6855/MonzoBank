package com.avinashpatil.app.monzobank.data.remote.dto.payment

import com.google.gson.annotations.SerializedName
import com.avinashpatil.app.monzobank.data.remote.dto.card.PaginationDto

/**
 * DTO for payment history response
 */
data class PaymentHistoryDto(
    @SerializedName("payments")
    val payments: List<PaymentDto>,
    
    @SerializedName("pagination")
    val pagination: PaginationDto,
    
    @SerializedName("total_count")
    val totalCount: Int = 0,
    
    @SerializedName("date_range")
    val dateRange: DateRangeDto? = null
)

/**
 * DTO for individual payment in history
 */
data class PaymentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("type")
    val type: String, // TRANSFER, BILL_PAYMENT, INTERNATIONAL
    
    @SerializedName("amount")
    val amount: Double,
    
    @SerializedName("currency")
    val currency: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("recipient_name")
    val recipientName: String,
    
    @SerializedName("reference")
    val reference: String?,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("completed_at")
    val completedAt: String?
)

/**
 * DTO for date range filter
 */
data class DateRangeDto(
    @SerializedName("start_date")
    val startDate: String,
    
    @SerializedName("end_date")
    val endDate: String
)
package com.avinashpatil.app.monzobank.data.remote.dto.analytics

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExportDataDto(
    @Json(name = "export_id")
    val exportId: String,
    
    @Json(name = "account_id")
    val accountId: String,
    
    @Json(name = "export_type")
    val exportType: String, // "transactions", "analytics", "statements", "tax_summary"
    
    @Json(name = "format")
    val format: String, // "csv", "pdf", "excel", "json"
    
    @Json(name = "date_range")
    val dateRange: DateRangeDto,
    
    @Json(name = "filters")
    val filters: ExportFiltersDto?,
    
    @Json(name = "status")
    val status: String, // "pending", "processing", "completed", "failed"
    
    @Json(name = "file_url")
    val fileUrl: String?,
    
    @Json(name = "file_size")
    val fileSize: Long?, // in bytes
    
    @Json(name = "record_count")
    val recordCount: Int?,
    
    @Json(name = "created_at")
    val createdAt: String,
    
    @Json(name = "completed_at")
    val completedAt: String?,
    
    @Json(name = "expires_at")
    val expiresAt: String?,
    
    @Json(name = "error_message")
    val errorMessage: String?
)

@JsonClass(generateAdapter = true)
data class DateRangeDto(
    @Json(name = "start_date")
    val startDate: String,
    
    @Json(name = "end_date")
    val endDate: String
)

@JsonClass(generateAdapter = true)
data class ExportFiltersDto(
    @Json(name = "categories")
    val categories: List<String>?,
    
    @Json(name = "merchants")
    val merchants: List<String>?,
    
    @Json(name = "transaction_types")
    val transactionTypes: List<String>?,
    
    @Json(name = "min_amount")
    val minAmount: String?,
    
    @Json(name = "max_amount")
    val maxAmount: String?,
    
    @Json(name = "include_pending")
    val includePending: Boolean?,
    
    @Json(name = "include_internal_transfers")
    val includeInternalTransfers: Boolean?
)
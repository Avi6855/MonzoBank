package com.avinashpatil.app.monzobank.data.remote.dto.card

import com.google.gson.annotations.SerializedName

/**
 * DTO for reporting card issues (lost, stolen, damaged)
 */
data class ReportCardDto(
    @SerializedName("card_id")
    val cardId: String,
    
    @SerializedName("report_type")
    val reportType: String, // LOST, STOLEN, DAMAGED
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("reported_at")
    val reportedAt: String,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("police_report_number")
    val policeReportNumber: String? = null
)
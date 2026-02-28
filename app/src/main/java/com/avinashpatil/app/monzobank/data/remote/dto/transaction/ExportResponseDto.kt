package com.avinashpatil.app.monzobank.data.remote.dto.transaction

import com.google.gson.annotations.SerializedName

data class ExportResponseDto(
    @SerializedName("download_url")
    val downloadUrl: String,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("format")
    val format: String,
    @SerializedName("file_size")
    val fileSize: Long
)
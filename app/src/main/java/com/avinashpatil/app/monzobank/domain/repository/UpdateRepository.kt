package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class AppUpdate(
    val id: String,
    val version: String,
    val title: String,
    val description: String,
    val releaseNotes: List<String>,
    val isRequired: Boolean,
    val isCritical: Boolean,
    val downloadUrl: String?,
    val fileSize: Long, // in bytes
    val releaseDate: LocalDateTime,
    val minimumOsVersion: String?
)

data class UpdateStatus(
    val isUpdateAvailable: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val update: AppUpdate?
)

interface UpdateRepository {
    suspend fun checkForUpdates(): Result<UpdateStatus>
    suspend fun getUpdateInfo(version: String): Result<AppUpdate?>
    suspend fun downloadUpdate(updateId: String): Result<String>
    suspend fun installUpdate(updateId: String): Result<Unit>
    suspend fun getUpdateHistory(): Result<List<AppUpdate>>
    suspend fun markUpdateInstalled(updateId: String): Result<Unit>
    suspend fun postponeUpdate(updateId: String): Result<Unit>
}
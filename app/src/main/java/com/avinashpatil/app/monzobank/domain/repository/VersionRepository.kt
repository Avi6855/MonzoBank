package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class AppVersion(
    val version: String,
    val buildNumber: Int,
    val releaseDate: LocalDateTime,
    val features: List<String>,
    val bugFixes: List<String>,
    val isStable: Boolean,
    val minimumOsVersion: String,
    val deprecatedFeatures: List<String> = emptyList()
)

data class VersionInfo(
    val currentVersion: String,
    val latestVersion: String,
    val isUpdateRequired: Boolean,
    val isUpdateAvailable: Boolean,
    val releaseNotes: String?
)

interface VersionRepository {
    suspend fun getCurrentVersion(): Result<String>
    suspend fun getLatestVersion(): Result<String>
    suspend fun getVersionInfo(): Result<VersionInfo>
    suspend fun getVersionHistory(): Result<List<AppVersion>>
    suspend fun getVersionDetails(version: String): Result<AppVersion?>
    suspend fun checkCompatibility(version: String): Result<Boolean>
    suspend fun getDeprecatedFeatures(): Result<List<String>>
}
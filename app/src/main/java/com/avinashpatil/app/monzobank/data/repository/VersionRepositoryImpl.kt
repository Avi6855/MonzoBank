package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionRepositoryImpl @Inject constructor() : VersionRepository {
    
    private val currentVersion = "1.0.0"
    private val latestVersion = "1.0.1"
    
    private val versions = listOf(
        AppVersion(
            version = "1.0.0",
            buildNumber = 100,
            releaseDate = LocalDateTime.now().minusDays(30),
            features = listOf(
                "Initial release",
                "Account management",
                "Transaction history",
                "Basic budgeting tools"
            ),
            bugFixes = emptyList(),
            isStable = true,
            minimumOsVersion = "Android 7.0"
        ),
        AppVersion(
            version = "1.0.1",
            buildNumber = 101,
            releaseDate = LocalDateTime.now().minusDays(1),
            features = listOf(
                "Enhanced security",
                "Improved performance"
            ),
            bugFixes = listOf(
                "Fixed login issues",
                "Resolved transaction sync problems",
                "Fixed notification display"
            ),
            isStable = true,
            minimumOsVersion = "Android 7.0"
        ),
        AppVersion(
            version = "1.1.0",
            buildNumber = 110,
            releaseDate = LocalDateTime.now().plusDays(7),
            features = listOf(
                "New dashboard design",
                "Investment tracking",
                "Advanced budgeting",
                "Savings goals"
            ),
            bugFixes = listOf(
                "Performance improvements",
                "UI enhancements"
            ),
            isStable = false,
            minimumOsVersion = "Android 8.0",
            deprecatedFeatures = listOf("Legacy transaction export")
        )
    )
    
    override suspend fun getCurrentVersion(): Result<String> {
        return try {
            Result.success(currentVersion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getLatestVersion(): Result<String> {
        return try {
            Result.success(latestVersion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getVersionInfo(): Result<VersionInfo> {
        return try {
            val latestVersionDetails = versions.find { it.version == latestVersion }
            val isUpdateAvailable = currentVersion != latestVersion
            val isUpdateRequired = false // Mock logic - could be based on security updates
            
            val releaseNotes = latestVersionDetails?.let { version ->
                buildString {
                    if (version.features.isNotEmpty()) {
                        appendLine("New Features:")
                        version.features.forEach { appendLine("• $it") }
                    }
                    if (version.bugFixes.isNotEmpty()) {
                        appendLine("\nBug Fixes:")
                        version.bugFixes.forEach { appendLine("• $it") }
                    }
                }
            }
            
            val versionInfo = VersionInfo(
                currentVersion = currentVersion,
                latestVersion = latestVersion,
                isUpdateRequired = isUpdateRequired,
                isUpdateAvailable = isUpdateAvailable,
                releaseNotes = releaseNotes
            )
            
            Result.success(versionInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getVersionHistory(): Result<List<AppVersion>> {
        return try {
            val history = versions.filter { 
                it.releaseDate.isBefore(LocalDateTime.now()) 
            }.sortedByDescending { it.releaseDate }
            
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getVersionDetails(version: String): Result<AppVersion?> {
        return try {
            val versionDetails = versions.find { it.version == version }
            Result.success(versionDetails)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkCompatibility(version: String): Result<Boolean> {
        return try {
            val versionDetails = versions.find { it.version == version }
            val isCompatible = versionDetails != null && versionDetails.isStable
            Result.success(isCompatible)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDeprecatedFeatures(): Result<List<String>> {
        return try {
            val deprecatedFeatures = versions.flatMap { it.deprecatedFeatures }.distinct()
            Result.success(deprecatedFeatures)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
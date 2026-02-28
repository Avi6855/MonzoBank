package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class CrashReport(
    val id: String,
    val userId: String?,
    val exception: String,
    val stackTrace: String,
    val deviceInfo: Map<String, String>,
    val appVersion: String,
    val osVersion: String,
    val timestamp: LocalDateTime,
    val severity: CrashSeverity,
    val context: Map<String, String> = emptyMap(),
    val breadcrumbs: List<String> = emptyList(),
    val isResolved: Boolean = false,
    val resolvedAt: LocalDateTime? = null,
    val resolvedBy: String? = null
)

enum class CrashSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

interface CrashReportingRepository {
    suspend fun reportCrash(crash: CrashReport): Result<String>
    suspend fun getCrashReports(limit: Int = 50, offset: Int = 0): Result<List<CrashReport>>
    suspend fun getCrashReport(crashId: String): Result<CrashReport?>
    suspend fun getCrashReportsByUser(userId: String): Result<List<CrashReport>>
    suspend fun markCrashAsResolved(crashId: String, resolvedBy: String): Result<Unit>
    suspend fun deleteCrashReport(crashId: String): Result<Unit>
    suspend fun getCrashStatistics(): Result<Map<String, Any>>
    suspend fun searchCrashReports(query: String): Result<List<CrashReport>>
}
package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.CrashReport
import com.avinashpatil.app.monzobank.domain.repository.CrashReportingRepository
import com.avinashpatil.app.monzobank.domain.repository.CrashSeverity
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrashReportingRepositoryImpl @Inject constructor() : CrashReportingRepository {
    
    private val crashReports = ConcurrentHashMap<String, CrashReport>()
    
    override suspend fun reportCrash(crash: CrashReport): Result<String> {
        return try {
            crashReports[crash.id] = crash
            Result.success(crash.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCrashReports(limit: Int, offset: Int): Result<List<CrashReport>> {
        return try {
            val reports = crashReports.values
                .sortedByDescending { it.timestamp }
                .drop(offset)
                .take(limit)
            Result.success(reports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCrashReport(crashId: String): Result<CrashReport?> {
        return try {
            Result.success(crashReports[crashId])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCrashReportsByUser(userId: String): Result<List<CrashReport>> {
        return try {
            val userReports = crashReports.values
                .filter { it.userId == userId }
                .sortedByDescending { it.timestamp }
            Result.success(userReports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun markCrashAsResolved(crashId: String, resolvedBy: String): Result<Unit> {
        return try {
            val crash = crashReports[crashId]
            if (crash != null) {
                val updatedCrash = crash.copy(
                    isResolved = true,
                    resolvedAt = LocalDateTime.now(),
                    resolvedBy = resolvedBy
                )
                crashReports[crashId] = updatedCrash
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteCrashReport(crashId: String): Result<Unit> {
        return try {
            crashReports.remove(crashId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCrashStatistics(): Result<Map<String, Any>> {
        return try {
            val reports = crashReports.values
            val stats = mapOf(
                "totalCrashes" to reports.size,
                "resolvedCrashes" to reports.count { it.isResolved },
                "unresolvedCrashes" to reports.count { !it.isResolved },
                "criticalCrashes" to reports.count { it.severity == CrashSeverity.CRITICAL },
                "highSeverityCrashes" to reports.count { it.severity == CrashSeverity.HIGH },
                "mediumSeverityCrashes" to reports.count { it.severity == CrashSeverity.MEDIUM },
                "lowSeverityCrashes" to reports.count { it.severity == CrashSeverity.LOW },
                "crashesLast24Hours" to reports.count { 
                    it.timestamp.isAfter(LocalDateTime.now().minusDays(1)) 
                },
                "crashesLast7Days" to reports.count { 
                    it.timestamp.isAfter(LocalDateTime.now().minusDays(7)) 
                }
            )
            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun searchCrashReports(query: String): Result<List<CrashReport>> {
        return try {
            val filteredReports = crashReports.values.filter { crash ->
                crash.exception.contains(query, ignoreCase = true) ||
                crash.stackTrace.contains(query, ignoreCase = true) ||
                crash.context.values.any { it.contains(query, ignoreCase = true) }
            }.sortedByDescending { it.timestamp }
            
            Result.success(filteredReports)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
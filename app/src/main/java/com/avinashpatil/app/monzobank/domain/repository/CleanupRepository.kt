package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class CleanupTask(
    val id: String,
    val name: String,
    val description: String,
    val type: CleanupType,
    val status: CleanupStatus,
    val scheduledAt: LocalDateTime?,
    val executedAt: LocalDateTime?,
    val itemsProcessed: Int,
    val itemsDeleted: Int,
    val spaceFreed: Long, // in bytes
    val errorMessage: String?
)

data class CleanupSummary(
    val totalTasks: Int,
    val completedTasks: Int,
    val failedTasks: Int,
    val totalSpaceFreed: Long,
    val lastCleanup: LocalDateTime?
)

enum class CleanupType {
    CACHE_CLEANUP,
    LOG_CLEANUP,
    TEMP_FILES,
    OLD_TRANSACTIONS,
    EXPIRED_SESSIONS,
    ORPHANED_DATA
}

enum class CleanupStatus {
    SCHEDULED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

interface CleanupRepository {
    suspend fun getCleanupTasks(): Result<List<CleanupTask>>
    suspend fun scheduleCleanup(type: CleanupType, scheduledAt: LocalDateTime): Result<String>
    suspend fun executeCleanup(taskId: String): Result<Unit>
    suspend fun cancelCleanup(taskId: String): Result<Unit>
    suspend fun getCleanupSummary(): Result<CleanupSummary>
    suspend fun getCleanupHistory(): Result<List<CleanupTask>>
    suspend fun autoCleanup(): Result<List<CleanupTask>>
}
package com.avinashpatil.app.monzobank.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for backup and restore operations
 */
interface BackupRepository {
    
    /**
     * Create a backup of user data
     */
    suspend fun createBackup(userId: String): Result<BackupInfo>
    
    /**
     * Restore data from backup
     */
    suspend fun restoreFromBackup(backupId: String): Result<Unit>
    
    /**
     * Get list of available backups
     */
    suspend fun getBackups(userId: String): Result<List<BackupInfo>>
    
    /**
     * Delete a backup
     */
    suspend fun deleteBackup(backupId: String): Result<Unit>
    
    /**
     * Schedule automatic backup
     */
    suspend fun scheduleAutoBackup(userId: String, frequency: BackupFrequency): Result<Unit>
    
    /**
     * Cancel automatic backup
     */
    suspend fun cancelAutoBackup(userId: String): Result<Unit>
    
    /**
     * Get backup status
     */
    suspend fun getBackupStatus(backupId: String): Result<BackupStatus>
    
    /**
     * Observe backup progress
     */
    fun observeBackupProgress(): Flow<BackupProgress>
    
    /**
     * Verify backup integrity
     */
    suspend fun verifyBackup(backupId: String): Result<Boolean>
}

data class BackupInfo(
    val id: String,
    val userId: String,
    val createdAt: LocalDateTime,
    val size: Long,
    val type: BackupType,
    val status: BackupStatus,
    val description: String? = null,
    val checksum: String? = null
)

enum class BackupType {
    FULL,
    INCREMENTAL,
    DIFFERENTIAL
}

enum class BackupStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CORRUPTED
}

enum class BackupFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    NEVER
}

data class BackupProgress(
    val backupId: String,
    val totalItems: Int,
    val processedItems: Int,
    val currentOperation: String,
    val status: BackupStatus
) {
    val progressPercentage: Float
        get() = if (totalItems > 0) (processedItems.toFloat() / totalItems) * 100f else 0f
}
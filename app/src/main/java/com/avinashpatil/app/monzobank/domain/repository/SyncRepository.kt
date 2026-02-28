package com.avinashpatil.app.monzobank.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for data synchronization operations
 */
interface SyncRepository {
    
    /**
     * Start data synchronization
     */
    suspend fun startSync(): Result<Unit>
    
    /**
     * Stop data synchronization
     */
    suspend fun stopSync(): Result<Unit>
    
    /**
     * Get sync status
     */
    suspend fun getSyncStatus(): Result<SyncStatus>
    
    /**
     * Force sync all data
     */
    suspend fun forceSyncAll(): Result<Unit>
    
    /**
     * Sync specific data type
     */
    suspend fun syncDataType(dataType: String): Result<Unit>
    
    /**
     * Get last sync time
     */
    suspend fun getLastSyncTime(): Result<LocalDateTime?>
    
    /**
     * Observe sync progress
     */
    fun observeSyncProgress(): Flow<SyncProgress>
    
    /**
     * Get sync conflicts
     */
    suspend fun getSyncConflicts(): Result<List<SyncConflict>>
    
    /**
     * Resolve sync conflict
     */
    suspend fun resolveSyncConflict(conflictId: String, resolution: ConflictResolution): Result<Unit>
}

enum class SyncStatus {
    IDLE,
    SYNCING,
    COMPLETED,
    FAILED,
    PAUSED
}

data class SyncProgress(
    val totalItems: Int,
    val syncedItems: Int,
    val currentDataType: String?,
    val status: SyncStatus
) {
    val progressPercentage: Float
        get() = if (totalItems > 0) (syncedItems.toFloat() / totalItems) * 100f else 0f
}

data class SyncConflict(
    val id: String,
    val dataType: String,
    val localData: Any,
    val remoteData: Any,
    val conflictType: ConflictType,
    val timestamp: LocalDateTime
)

enum class ConflictType {
    UPDATE_CONFLICT,
    DELETE_CONFLICT,
    CREATE_CONFLICT
}

enum class ConflictResolution {
    USE_LOCAL,
    USE_REMOTE,
    MERGE,
    SKIP
}
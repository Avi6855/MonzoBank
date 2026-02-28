package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CleanupRepositoryImpl @Inject constructor() : CleanupRepository {
    
    private val tasks = mutableListOf(
        CleanupTask(
            id = "cleanup1",
            name = "Cache Cleanup",
            description = "Clean up application cache files",
            type = CleanupType.CACHE_CLEANUP,
            status = CleanupStatus.COMPLETED,
            scheduledAt = LocalDateTime.now().minusDays(1),
            executedAt = LocalDateTime.now().minusDays(1).plusMinutes(5),
            itemsProcessed = 1500,
            itemsDeleted = 1200,
            spaceFreed = 45000000L, // 45MB
            errorMessage = null
        ),
        CleanupTask(
            id = "cleanup2",
            name = "Log Cleanup",
            description = "Remove old log files",
            type = CleanupType.LOG_CLEANUP,
            status = CleanupStatus.SCHEDULED,
            scheduledAt = LocalDateTime.now().plusHours(2),
            executedAt = null,
            itemsProcessed = 0,
            itemsDeleted = 0,
            spaceFreed = 0L,
            errorMessage = null
        )
    )
    
    override suspend fun getCleanupTasks(): Result<List<CleanupTask>> {
        return try {
            val sortedTasks = tasks.sortedByDescending { it.scheduledAt }
            Result.success(sortedTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun scheduleCleanup(type: CleanupType, scheduledAt: LocalDateTime): Result<String> {
        return try {
            val task = CleanupTask(
                id = UUID.randomUUID().toString(),
                name = "${type.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}",
                description = "Scheduled cleanup task for ${type.name.lowercase()}",
                type = type,
                status = CleanupStatus.SCHEDULED,
                scheduledAt = scheduledAt,
                executedAt = null,
                itemsProcessed = 0,
                itemsDeleted = 0,
                spaceFreed = 0L,
                errorMessage = null
            )
            
            tasks.add(task)
            Result.success(task.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun executeCleanup(taskId: String): Result<Unit> {
        return try {
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index != -1) {
                val task = tasks[index]
                if (task.status == CleanupStatus.SCHEDULED) {
                    // Update status to running
                    tasks[index] = task.copy(status = CleanupStatus.RUNNING)
                    
                    // Simulate cleanup execution
                    val itemsProcessed = (100..2000).random()
                    val itemsDeleted = (itemsProcessed * 0.6).toInt()
                    val spaceFreed = itemsDeleted * (1000..50000).random().toLong()
                    
                    // Update task with results
                    tasks[index] = task.copy(
                        status = CleanupStatus.COMPLETED,
                        executedAt = LocalDateTime.now(),
                        itemsProcessed = itemsProcessed,
                        itemsDeleted = itemsDeleted,
                        spaceFreed = spaceFreed
                    )
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Task is not in scheduled status"))
                }
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            // Update task status to failed
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index != -1) {
                val task = tasks[index]
                tasks[index] = task.copy(
                    status = CleanupStatus.FAILED,
                    errorMessage = e.message
                )
            }
            Result.failure(e)
        }
    }
    
    override suspend fun cancelCleanup(taskId: String): Result<Unit> {
        return try {
            val index = tasks.indexOfFirst { it.id == taskId }
            if (index != -1) {
                val task = tasks[index]
                if (task.status == CleanupStatus.SCHEDULED) {
                    tasks[index] = task.copy(status = CleanupStatus.CANCELLED)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Task cannot be cancelled"))
                }
            } else {
                Result.failure(Exception("Task not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCleanupSummary(): Result<CleanupSummary> {
        return try {
            val totalTasks = tasks.size
            val completedTasks = tasks.count { it.status == CleanupStatus.COMPLETED }
            val failedTasks = tasks.count { it.status == CleanupStatus.FAILED }
            val totalSpaceFreed = tasks.filter { it.status == CleanupStatus.COMPLETED }
                .sumOf { it.spaceFreed }
            val lastCleanup = tasks.filter { it.status == CleanupStatus.COMPLETED }
                .maxByOrNull { it.executedAt ?: LocalDateTime.MIN }?.executedAt
            
            val summary = CleanupSummary(
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                failedTasks = failedTasks,
                totalSpaceFreed = totalSpaceFreed,
                lastCleanup = lastCleanup
            )
            
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCleanupHistory(): Result<List<CleanupTask>> {
        return try {
            val history = tasks.filter { 
                it.status == CleanupStatus.COMPLETED || it.status == CleanupStatus.FAILED 
            }.sortedByDescending { it.executedAt }
            
            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun autoCleanup(): Result<List<CleanupTask>> {
        return try {
            val autoCleanupTasks = mutableListOf<CleanupTask>()
            
            // Schedule automatic cleanup tasks
            val cleanupTypes = listOf(
                CleanupType.CACHE_CLEANUP,
                CleanupType.TEMP_FILES,
                CleanupType.EXPIRED_SESSIONS
            )
            
            cleanupTypes.forEach { type ->
                val taskId = scheduleCleanup(type, LocalDateTime.now()).getOrNull()
                if (taskId != null) {
                    executeCleanup(taskId)
                    val task = tasks.find { it.id == taskId }
                    if (task != null) {
                        autoCleanupTasks.add(task)
                    }
                }
            }
            
            Result.success(autoCleanupTasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
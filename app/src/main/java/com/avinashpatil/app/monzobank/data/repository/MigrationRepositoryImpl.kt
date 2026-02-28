package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MigrationRepositoryImpl @Inject constructor() : MigrationRepository {
    
    private val migrations = mutableListOf(
        Migration(
            id = "mig001",
            version = "1.0.0",
            description = "Initial database schema",
            script = "CREATE TABLE users...",
            status = MigrationStatus.COMPLETED,
            executedAt = LocalDateTime.now().minusDays(30),
            executionTime = 1500L,
            errorMessage = null
        ),
        Migration(
            id = "mig002",
            version = "1.0.1",
            description = "Add transaction categories",
            script = "ALTER TABLE transactions ADD COLUMN category...",
            status = MigrationStatus.COMPLETED,
            executedAt = LocalDateTime.now().minusDays(15),
            executionTime = 800L,
            errorMessage = null
        ),
        Migration(
            id = "mig003",
            version = "1.1.0",
            description = "Add investment tables",
            script = "CREATE TABLE investments...",
            status = MigrationStatus.PENDING,
            executedAt = null,
            executionTime = null,
            errorMessage = null
        )
    )
    
    override suspend fun getMigrations(): Result<List<Migration>> {
        return try {
            val sortedMigrations = migrations.sortedBy { it.version }
            Result.success(sortedMigrations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPendingMigrations(): Result<List<Migration>> {
        return try {
            val pendingMigrations = migrations.filter { it.status == MigrationStatus.PENDING }
                .sortedBy { it.version }
            Result.success(pendingMigrations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCompletedMigrations(): Result<List<Migration>> {
        return try {
            val completedMigrations = migrations.filter { it.status == MigrationStatus.COMPLETED }
                .sortedByDescending { it.executedAt }
            Result.success(completedMigrations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMigrationPlan(): Result<MigrationPlan> {
        return try {
            val pendingMigrations = migrations.filter { it.status == MigrationStatus.PENDING }
            val totalMigrations = migrations.size
            val pendingCount = pendingMigrations.size
            val estimatedTime = pendingCount * 1000L // Mock estimation: 1 second per migration
            
            val plan = MigrationPlan(
                migrations = pendingMigrations.sortedBy { it.version },
                totalMigrations = totalMigrations,
                pendingMigrations = pendingCount,
                estimatedTime = estimatedTime
            )
            
            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun executeMigration(migrationId: String): Result<Unit> {
        return try {
            val index = migrations.indexOfFirst { it.id == migrationId }
            if (index != -1) {
                val migration = migrations[index]
                if (migration.status == MigrationStatus.PENDING) {
                    // Simulate migration execution
                    val startTime = System.currentTimeMillis()
                    
                    // Update status to running
                    migrations[index] = migration.copy(status = MigrationStatus.RUNNING)
                    
                    // Simulate execution time
                    Thread.sleep(100) // Mock execution
                    
                    val executionTime = System.currentTimeMillis() - startTime
                    
                    // Update status to completed
                    migrations[index] = migration.copy(
                        status = MigrationStatus.COMPLETED,
                        executedAt = LocalDateTime.now(),
                        executionTime = executionTime
                    )
                    
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Migration is not in pending status"))
                }
            } else {
                Result.failure(Exception("Migration not found"))
            }
        } catch (e: Exception) {
            // Update migration status to failed
            val index = migrations.indexOfFirst { it.id == migrationId }
            if (index != -1) {
                val migration = migrations[index]
                migrations[index] = migration.copy(
                    status = MigrationStatus.FAILED,
                    errorMessage = e.message
                )
            }
            Result.failure(e)
        }
    }
    
    override suspend fun rollbackMigration(migrationId: String): Result<Unit> {
        return try {
            val index = migrations.indexOfFirst { it.id == migrationId }
            if (index != -1) {
                val migration = migrations[index]
                if (migration.status == MigrationStatus.COMPLETED) {
                    // Simulate rollback
                    migrations[index] = migration.copy(
                        status = MigrationStatus.ROLLED_BACK,
                        executedAt = null,
                        executionTime = null
                    )
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Migration cannot be rolled back"))
                }
            } else {
                Result.failure(Exception("Migration not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getMigrationStatus(migrationId: String): Result<MigrationStatus?> {
        return try {
            val migration = migrations.find { it.id == migrationId }
            Result.success(migration?.status)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateMigrations(): Result<Boolean> {
        return try {
            // Mock validation logic
            val hasFailedMigrations = migrations.any { it.status == MigrationStatus.FAILED }
            val isValid = !hasFailedMigrations
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
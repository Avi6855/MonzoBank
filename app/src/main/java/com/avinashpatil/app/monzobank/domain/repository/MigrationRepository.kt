package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Migration(
    val id: String,
    val version: String,
    val description: String,
    val script: String,
    val status: MigrationStatus,
    val executedAt: LocalDateTime?,
    val executionTime: Long?, // in milliseconds
    val errorMessage: String?
)

data class MigrationPlan(
    val migrations: List<Migration>,
    val totalMigrations: Int,
    val pendingMigrations: Int,
    val estimatedTime: Long // in milliseconds
)

enum class MigrationStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    ROLLED_BACK
}

interface MigrationRepository {
    suspend fun getMigrations(): Result<List<Migration>>
    suspend fun getPendingMigrations(): Result<List<Migration>>
    suspend fun getCompletedMigrations(): Result<List<Migration>>
    suspend fun getMigrationPlan(): Result<MigrationPlan>
    suspend fun executeMigration(migrationId: String): Result<Unit>
    suspend fun rollbackMigration(migrationId: String): Result<Unit>
    suspend fun getMigrationStatus(migrationId: String): Result<MigrationStatus?>
    suspend fun validateMigrations(): Result<Boolean>
}
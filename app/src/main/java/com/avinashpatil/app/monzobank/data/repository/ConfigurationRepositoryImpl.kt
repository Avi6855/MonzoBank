package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.repository.*
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigurationRepositoryImpl @Inject constructor() : ConfigurationRepository {
    
    private val configurations = mutableMapOf<String, Configuration>()
    private val configurationHistory = mutableMapOf<String, MutableList<ConfigurationHistory>>()
    
    init {
        initializeDefaultConfigurations()
    }
    
    override suspend fun getAllConfigurations(): Result<List<Configuration>> {
        return try {
            Result.success(configurations.values.toList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getConfiguration(key: String): Result<Configuration?> {
        return try {
            Result.success(configurations[key])
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getConfigurationsByCategory(category: ConfigurationCategory): Result<List<Configuration>> {
        return try {
            val categoryConfigs = configurations.values.filter { it.category == category }
            Result.success(categoryConfigs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun createConfiguration(configuration: Configuration): Result<String> {
        return try {
            if (configurations.containsKey(configuration.key)) {
                Result.failure(Exception("Configuration with key '${configuration.key}' already exists"))
            } else {
                configurations[configuration.key] = configuration
                Result.success(configuration.id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateConfiguration(key: String, value: String, updatedBy: String): Result<Unit> {
        return try {
            val existing = configurations[key]
            if (existing != null) {
                if (existing.isReadOnly) {
                    Result.failure(Exception("Configuration '$key' is read-only"))
                } else {
                    // Add to history
                    val historyEntry = ConfigurationHistory(
                        id = UUID.randomUUID().toString(),
                        configurationId = existing.id,
                        oldValue = existing.value,
                        newValue = value,
                        changedBy = updatedBy,
                        changedAt = LocalDateTime.now()
                    )
                    
                    val history = configurationHistory.getOrPut(key) { mutableListOf() }
                    history.add(historyEntry)
                    
                    // Update configuration
                    configurations[key] = existing.copy(
                        value = value,
                        updatedAt = LocalDateTime.now(),
                        updatedBy = updatedBy
                    )
                    
                    Result.success(Unit)
                }
            } else {
                Result.failure(Exception("Configuration with key '$key' not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteConfiguration(key: String): Result<Unit> {
        return try {
            val existing = configurations[key]
            if (existing != null) {
                if (existing.isReadOnly) {
                    Result.failure(Exception("Configuration '$key' is read-only and cannot be deleted"))
                } else {
                    configurations.remove(key)
                    configurationHistory.remove(key)
                    Result.success(Unit)
                }
            } else {
                Result.failure(Exception("Configuration with key '$key' not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getConfigurationHistory(key: String): Result<List<ConfigurationHistory>> {
        return try {
            val history = configurationHistory[key] ?: emptyList()
            Result.success(history.sortedByDescending { it.changedAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validateConfiguration(key: String, value: String): Result<Boolean> {
        return try {
            val config = configurations[key]
            if (config != null && config.validationRule != null) {
                // Simple validation - in real implementation, this would use proper validation rules
                val isValid = when (config.validationRule) {
                    "email" -> value.contains("@")
                    "number" -> value.toDoubleOrNull() != null
                    "boolean" -> value.lowercase() in listOf("true", "false")
                    "url" -> value.startsWith("http")
                    else -> true
                }
                Result.success(isValid)
            } else {
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resetToDefault(key: String): Result<Unit> {
        return try {
            val config = configurations[key]
            if (config != null && config.defaultValue != null) {
                updateConfiguration(key, config.defaultValue, "system")
            } else {
                Result.failure(Exception("No default value available for configuration '$key'"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun bulkUpdateConfigurations(configurations: Map<String, String>, updatedBy: String): Result<Unit> {
        return try {
            configurations.forEach { (key, value) ->
                updateConfiguration(key, value, updatedBy).getOrThrow()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun exportConfigurations(category: ConfigurationCategory?): Result<Map<String, String>> {
        return try {
            val configsToExport = if (category != null) {
                configurations.values.filter { it.category == category }
            } else {
                configurations.values
            }
            
            val exportMap = configsToExport.associate { it.key to it.value }
            Result.success(exportMap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun importConfigurations(configurations: Map<String, String>, updatedBy: String) {
        configurations.forEach { (key, value) ->
            if (this.configurations.containsKey(key)) {
                updateConfiguration(key, value, updatedBy).getOrThrow()
            }
        }
    }
    
    private fun initializeDefaultConfigurations() {
        val defaultConfigs = listOf(
            Configuration(
                id = UUID.randomUUID().toString(),
                key = "app.name",
                value = "MonzoBank",
                description = "Application name",
                category = ConfigurationCategory.GENERAL,
                isReadOnly = true,
                defaultValue = "MonzoBank"
            ),
            Configuration(
                id = UUID.randomUUID().toString(),
                key = "app.version",
                value = "1.0.0",
                description = "Application version",
                category = ConfigurationCategory.GENERAL,
                isReadOnly = true,
                defaultValue = "1.0.0"
            ),
            Configuration(
                id = UUID.randomUUID().toString(),
                key = "security.session_timeout",
                value = "30",
                description = "Session timeout in minutes",
                category = ConfigurationCategory.SECURITY,
                validationRule = "number",
                defaultValue = "30"
            ),
            Configuration(
                id = UUID.randomUUID().toString(),
                key = "payment.daily_limit",
                value = "10000.00",
                description = "Daily payment limit in GBP",
                category = ConfigurationCategory.PAYMENT,
                validationRule = "number",
                defaultValue = "10000.00"
            ),
            Configuration(
                id = UUID.randomUUID().toString(),
                key = "notification.email_enabled",
                value = "true",
                description = "Enable email notifications",
                category = ConfigurationCategory.NOTIFICATION,
                validationRule = "boolean",
                defaultValue = "true"
            )
        )
        
        defaultConfigs.forEach { config ->
            configurations[config.key] = config
        }
    }
}
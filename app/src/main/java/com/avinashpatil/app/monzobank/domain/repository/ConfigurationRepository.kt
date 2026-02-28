package com.avinashpatil.app.monzobank.domain.repository

import java.time.LocalDateTime

data class Configuration(
    val id: String,
    val key: String,
    val value: String,
    val description: String? = null,
    val category: ConfigurationCategory = ConfigurationCategory.GENERAL,
    val isEncrypted: Boolean = false,
    val isReadOnly: Boolean = false,
    val validationRule: String? = null,
    val defaultValue: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val updatedBy: String? = null
)

data class ConfigurationHistory(
    val id: String,
    val configurationId: String,
    val oldValue: String,
    val newValue: String,
    val changedBy: String,
    val changeReason: String? = null,
    val changedAt: LocalDateTime = LocalDateTime.now()
)

enum class ConfigurationCategory {
    GENERAL,
    SECURITY,
    PAYMENT,
    NOTIFICATION,
    API,
    FEATURE_FLAGS,
    LIMITS,
    COMPLIANCE,
    INTEGRATION,
    PERFORMANCE
}

interface ConfigurationRepository {
    suspend fun getAllConfigurations(): Result<List<Configuration>>
    suspend fun getConfiguration(key: String): Result<Configuration?>
    suspend fun getConfigurationsByCategory(category: ConfigurationCategory): Result<List<Configuration>>
    suspend fun createConfiguration(configuration: Configuration): Result<String>
    suspend fun updateConfiguration(key: String, value: String, updatedBy: String): Result<Unit>
    suspend fun deleteConfiguration(key: String): Result<Unit>
    suspend fun getConfigurationHistory(key: String): Result<List<ConfigurationHistory>>
    suspend fun validateConfiguration(key: String, value: String): Result<Boolean>
    suspend fun resetToDefault(key: String): Result<Unit>
    suspend fun bulkUpdateConfigurations(configurations: Map<String, String>, updatedBy: String): Result<Unit>
    suspend fun exportConfigurations(category: ConfigurationCategory?): Result<Map<String, String>>
    suspend fun importConfigurations(configurations: Map<String, String>, updatedBy: String): Unit
}
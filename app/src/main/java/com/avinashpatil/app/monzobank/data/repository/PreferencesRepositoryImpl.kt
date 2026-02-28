package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.domain.model.AppTheme
import com.avinashpatil.app.monzobank.domain.model.ChartType
import com.avinashpatil.app.monzobank.domain.model.ColorScheme
import com.avinashpatil.app.monzobank.domain.model.DashboardLayout
import com.avinashpatil.app.monzobank.domain.model.DisplayPreferences
import com.avinashpatil.app.monzobank.domain.model.FontSize
import com.avinashpatil.app.monzobank.domain.model.NotificationPreferences
import com.avinashpatil.app.monzobank.domain.model.PrivacyPreferences
import com.avinashpatil.app.monzobank.domain.model.UserPreferences
import com.avinashpatil.app.monzobank.domain.repository.PreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    // TODO: Add actual data sources (SharedPreferences, Room DAO, etc.)
) : PreferencesRepository {
    
    private val gson = Gson()
    
    // In-memory storage for demo purposes - replace with actual storage
    private val userPreferences = mutableMapOf<String, UserPreferences>()
    private val preferenceHistory = mutableMapOf<String, MutableList<Map<String, Any>>>()
    
    override suspend fun getUserPreferences(userId: String): Result<UserPreferences> {
        return try {
            val preferences = userPreferences[userId] ?: createDefaultPreferences(userId)
            Result.success(preferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserPreferences(
        userId: String,
        preferences: UserPreferences
    ): Result<Unit> {
        return try {
            val validationErrors = validatePreferences(preferences).getOrElse { emptyList() }
            if (validationErrors.isNotEmpty()) {
                return Result.failure(Exception("Validation failed: ${validationErrors.joinToString(", ")}"))
            }
            
            val updatedPreferences = preferences.copy(updatedAt = Date())
            userPreferences[userId] = updatedPreferences
            
            // Add to history
            addToHistory(userId, "user_preferences_updated", mapOf(
                "timestamp" to Date(),
                "preferences" to updatedPreferences
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getNotificationPreferences(userId: String): Result<NotificationPreferences> {
        return try {
            val userPrefs = getUserPreferences(userId).getOrNull()
            val notificationPrefs = userPrefs?.notificationPreferences 
                ?: createDefaultNotificationPreferences(userId)
            Result.success(notificationPrefs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateNotificationPreferences(
        userId: String,
        preferences: NotificationPreferences
    ): Result<Unit> {
        return try {
            val currentUserPrefs = getUserPreferences(userId).getOrNull() 
                ?: createDefaultPreferences(userId)
            
            val updatedUserPrefs = currentUserPrefs.copy(
                notificationPreferences = preferences.copy(updatedAt = Date()),
                updatedAt = Date()
            )
            
            userPreferences[userId] = updatedUserPrefs
            
            addToHistory(userId, "notification_preferences_updated", mapOf(
                "timestamp" to Date(),
                "preferences" to preferences
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPrivacyPreferences(userId: String): Result<PrivacyPreferences> {
        return try {
            val userPrefs = getUserPreferences(userId).getOrNull()
            val privacyPrefs = userPrefs?.privacyPreferences 
                ?: createDefaultPrivacyPreferences(userId)
            Result.success(privacyPrefs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updatePrivacyPreferences(
        userId: String,
        preferences: PrivacyPreferences
    ): Result<Unit> {
        return try {
            val currentUserPrefs = getUserPreferences(userId).getOrNull() 
                ?: createDefaultPreferences(userId)
            
            val updatedUserPrefs = currentUserPrefs.copy(
                privacyPreferences = preferences.copy(updatedAt = Date()),
                updatedAt = Date()
            )
            
            userPreferences[userId] = updatedUserPrefs
            
            addToHistory(userId, "privacy_preferences_updated", mapOf(
                "timestamp" to Date(),
                "preferences" to preferences
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getDisplayPreferences(userId: String): Result<DisplayPreferences> {
        return try {
            val userPrefs = getUserPreferences(userId).getOrNull()
            val displayPrefs = userPrefs?.displayPreferences 
                ?: createDefaultDisplayPreferences(userId)
            Result.success(displayPrefs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateDisplayPreferences(
        userId: String,
        preferences: DisplayPreferences
    ): Result<Unit> {
        return try {
            val currentUserPrefs = getUserPreferences(userId).getOrNull() 
                ?: createDefaultPreferences(userId)
            
            val updatedUserPrefs = currentUserPrefs.copy(
                displayPreferences = preferences.copy(updatedAt = Date()),
                updatedAt = Date()
            )
            
            userPreferences[userId] = updatedUserPrefs
            
            addToHistory(userId, "display_preferences_updated", mapOf(
                "timestamp" to Date(),
                "preferences" to preferences
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setPreference(
        userId: String,
        key: String,
        value: Any
    ): Result<Unit> {
        return try {
            val currentPrefs = getUserPreferences(userId).getOrNull() 
                ?: createDefaultPreferences(userId)
            
            val updatedCustomSettings = currentPrefs.customSettings.toMutableMap()
            updatedCustomSettings[key] = value
            
            val updatedPrefs = currentPrefs.copy(
                customSettings = updatedCustomSettings,
                updatedAt = Date()
            )
            
            userPreferences[userId] = updatedPrefs
            
            addToHistory(userId, "preference_set", mapOf(
                "timestamp" to Date(),
                "key" to key,
                "value" to value
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPreference(
        userId: String,
        key: String,
        defaultValue: Any?
    ): Result<Any?> {
        return try {
            val preferences = getUserPreferences(userId).getOrNull()
            val value = preferences?.customSettings?.get(key) ?: defaultValue
            Result.success(value)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun removePreference(
        userId: String,
        key: String
    ): Result<Unit> {
        return try {
            val currentPrefs = getUserPreferences(userId).getOrNull() 
                ?: return Result.success(Unit)
            
            val updatedCustomSettings = currentPrefs.customSettings.toMutableMap()
            updatedCustomSettings.remove(key)
            
            val updatedPrefs = currentPrefs.copy(
                customSettings = updatedCustomSettings,
                updatedAt = Date()
            )
            
            userPreferences[userId] = updatedPrefs
            
            addToHistory(userId, "preference_removed", mapOf(
                "timestamp" to Date(),
                "key" to key
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun clearAllPreferences(userId: String): Result<Unit> {
        return try {
            userPreferences.remove(userId)
            preferenceHistory.remove(userId)
            
            addToHistory(userId, "all_preferences_cleared", mapOf(
                "timestamp" to Date()
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeUserPreferences(userId: String): Flow<UserPreferences> {
        // TODO: Implement real-time observation using Room/Flow
        return flowOf(userPreferences[userId] ?: createDefaultPreferences(userId))
    }
    
    override fun observeNotificationPreferences(userId: String): Flow<NotificationPreferences> {
        // TODO: Implement real-time observation using Room/Flow
        val userPrefs = userPreferences[userId] ?: createDefaultPreferences(userId)
        return flowOf(userPrefs.notificationPreferences ?: createDefaultNotificationPreferences(userId))
    }
    
    override suspend fun exportPreferences(userId: String): Result<String> {
        return try {
            val preferences = getUserPreferences(userId).getOrNull()
            if (preferences != null) {
                val json = gson.toJson(preferences)
                Result.success(json)
            } else {
                Result.failure(Exception("No preferences found for user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun importPreferences(
        userId: String,
        preferencesData: String
    ): Result<Unit> {
        return try {
            val preferences = gson.fromJson(preferencesData, UserPreferences::class.java)
            val updatedPreferences = preferences.copy(
                userId = userId,
                updatedAt = Date()
            )
            
            updateUserPreferences(userId, updatedPreferences)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resetToDefaults(userId: String): Result<Unit> {
        return try {
            val defaultPreferences = createDefaultPreferences(userId)
            userPreferences[userId] = defaultPreferences
            
            addToHistory(userId, "preferences_reset_to_defaults", mapOf(
                "timestamp" to Date()
            ))
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPreferenceCategories(): Result<List<String>> {
        return try {
            val categories = listOf(
                "Notifications",
                "Privacy",
                "Display",
                "Security",
                "Language & Region",
                "Accessibility",
                "Data & Storage",
                "Account"
            )
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun backupPreferences(userId: String): Result<String> {
        return try {
            val preferences = getUserPreferences(userId).getOrNull()
            if (preferences != null) {
                val backup = mapOf(
                    "userId" to userId,
                    "preferences" to preferences,
                    "backupDate" to Date(),
                    "version" to "1.0"
                )
                val json = gson.toJson(backup)
                Result.success(json)
            } else {
                Result.failure(Exception("No preferences found for backup"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun restorePreferences(
        userId: String,
        backupData: String
    ): Result<Unit> {
        return try {
            val backup = gson.fromJson(backupData, Map::class.java)
            val preferencesJson = gson.toJson(backup["preferences"])
            importPreferences(userId, preferencesJson)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getPreferenceHistory(
        userId: String,
        key: String?,
        limit: Int
    ): Result<List<Map<String, Any>>> {
        return try {
            val history = preferenceHistory[userId] ?: emptyList()
            val filteredHistory = if (key != null) {
                history.filter { it["key"] == key }
            } else {
                history
            }
            
            val limitedHistory = filteredHistory.takeLast(limit)
            Result.success(limitedHistory)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun validatePreferences(preferences: UserPreferences): Result<List<String>> {
        return try {
            val errors = mutableListOf<String>()
            
            // Validate auto lock timeout
            if (preferences.autoLockTimeout < 1 || preferences.autoLockTimeout > 60) {
                errors.add("Auto lock timeout must be between 1 and 60 minutes")
            }
            
            // Validate language code
            if (preferences.language.length != 2) {
                errors.add("Language code must be 2 characters")
            }
            
            // Validate currency code
            if (preferences.currency.length != 3) {
                errors.add("Currency code must be 3 characters")
            }
            
            // Validate privacy preferences
            val privacyPrefs = preferences.privacyPreferences
            if (privacyPrefs?.dataRetentionPeriod != null && (privacyPrefs.dataRetentionPeriod < 30 || privacyPrefs.dataRetentionPeriod > 2555)) {
                errors.add("Data retention period must be between 30 and 2555 days")
            }
            
            // Validate display preferences
            val displayPrefs = preferences.displayPreferences
            if (displayPrefs?.transactionListLimit != null && (displayPrefs.transactionListLimit < 5 || displayPrefs.transactionListLimit > 100)) {
                errors.add("Transaction list limit must be between 5 and 100")
            }
            
            Result.success(errors)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun createDefaultPreferences(userId: String): UserPreferences {
        val now = Date()
        return UserPreferences(
            userId = userId,
            notificationPreferences = createDefaultNotificationPreferences(userId),
            privacyPreferences = createDefaultPrivacyPreferences(userId),
            displayPreferences = createDefaultDisplayPreferences(userId),
            createdAt = now,
            updatedAt = now
        )
    }
    
    private fun createDefaultNotificationPreferences(userId: String): NotificationPreferences {
        val now = Date()
        return NotificationPreferences(
            userId = userId,
            pushEnabled = true,
            emailEnabled = true,
            smsEnabled = false,
            marketingNotifications = false,
            balanceAlerts = true,
            createdAt = now,
            updatedAt = now
        )
    }
    
    private fun createDefaultPrivacyPreferences(userId: String): PrivacyPreferences {
        val now = Date()
        return PrivacyPreferences(
            userId = userId,
            createdAt = now,
            updatedAt = now
        )
    }
    
    private fun createDefaultDisplayPreferences(userId: String): DisplayPreferences {
        val now = Date()
        return DisplayPreferences(
            userId = userId,
            theme = AppTheme.SYSTEM,
            fontSize = FontSize.MEDIUM,
            colorScheme = ColorScheme.DEFAULT,
            dashboardLayout = DashboardLayout.DEFAULT,
            chartType = ChartType.BAR,
            createdAt = now,
            updatedAt = now
        )
    }
    
    private fun addToHistory(userId: String, action: String, data: Map<String, Any>) {
        val history = preferenceHistory.getOrPut(userId) { mutableListOf() }
        val entry = mapOf(
            "action" to action,
            "data" to data
        )
        history.add(entry)
        
        // Keep only last 50 entries
        if (history.size > 50) {
            history.removeAt(0)
        }
    }
}
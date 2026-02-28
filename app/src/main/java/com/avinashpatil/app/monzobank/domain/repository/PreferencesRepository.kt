package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.UserPreferences
import com.avinashpatil.app.monzobank.domain.model.NotificationPreferences
import com.avinashpatil.app.monzobank.domain.model.PrivacyPreferences
import com.avinashpatil.app.monzobank.domain.model.DisplayPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user preferences management
 */
interface PreferencesRepository {
    
    /**
     * Get all user preferences
     */
    suspend fun getUserPreferences(userId: String): Result<UserPreferences>
    
    /**
     * Update user preferences
     */
    suspend fun updateUserPreferences(
        userId: String,
        preferences: UserPreferences
    ): Result<Unit>
    
    /**
     * Get notification preferences
     */
    suspend fun getNotificationPreferences(userId: String): Result<NotificationPreferences>
    
    /**
     * Update notification preferences
     */
    suspend fun updateNotificationPreferences(
        userId: String,
        preferences: NotificationPreferences
    ): Result<Unit>
    
    /**
     * Get privacy preferences
     */
    suspend fun getPrivacyPreferences(userId: String): Result<PrivacyPreferences>
    
    /**
     * Update privacy preferences
     */
    suspend fun updatePrivacyPreferences(
        userId: String,
        preferences: PrivacyPreferences
    ): Result<Unit>
    
    /**
     * Get display preferences
     */
    suspend fun getDisplayPreferences(userId: String): Result<DisplayPreferences>
    
    /**
     * Update display preferences
     */
    suspend fun updateDisplayPreferences(
        userId: String,
        preferences: DisplayPreferences
    ): Result<Unit>
    
    /**
     * Set a specific preference value
     */
    suspend fun setPreference(
        userId: String,
        key: String,
        value: Any
    ): Result<Unit>
    
    /**
     * Get a specific preference value
     */
    suspend fun getPreference(
        userId: String,
        key: String,
        defaultValue: Any? = null
    ): Result<Any?>
    
    /**
     * Remove a specific preference
     */
    suspend fun removePreference(
        userId: String,
        key: String
    ): Result<Unit>
    
    /**
     * Clear all preferences for a user
     */
    suspend fun clearAllPreferences(userId: String): Result<Unit>
    
    /**
     * Observe changes to user preferences
     */
    fun observeUserPreferences(userId: String): Flow<UserPreferences>
    
    /**
     * Observe changes to notification preferences
     */
    fun observeNotificationPreferences(userId: String): Flow<NotificationPreferences>
    
    /**
     * Export user preferences
     */
    suspend fun exportPreferences(userId: String): Result<String>
    
    /**
     * Import user preferences
     */
    suspend fun importPreferences(
        userId: String,
        preferencesData: String
    ): Result<Unit>
    
    /**
     * Reset preferences to default values
     */
    suspend fun resetToDefaults(userId: String): Result<Unit>
    
    /**
     * Get preference categories
     */
    suspend fun getPreferenceCategories(): Result<List<String>>
    
    /**
     * Backup preferences
     */
    suspend fun backupPreferences(userId: String): Result<String>
    
    /**
     * Restore preferences from backup
     */
    suspend fun restorePreferences(
        userId: String,
        backupData: String
    ): Result<Unit>
    
    /**
     * Get preference history
     */
    suspend fun getPreferenceHistory(
        userId: String,
        key: String? = null,
        limit: Int = 10
    ): Result<List<Map<String, Any>>>
    
    /**
     * Validate preferences
     */
    suspend fun validatePreferences(
        preferences: UserPreferences
    ): Result<List<String>> // Returns list of validation errors
}
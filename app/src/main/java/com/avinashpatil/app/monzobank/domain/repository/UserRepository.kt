package com.avinashpatil.app.monzobank.domain.repository

import com.avinashpatil.app.monzobank.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * User repository interface
 * Defines the contract for user operations
 */
interface UserRepository {
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: String): Result<User?>
    
    /**
     * Get current user
     */
    suspend fun getCurrentUser(): Result<User?>
    
    /**
     * Update user profile
     */
    suspend fun updateUser(user: User): Result<User>
    
    /**
     * Update user profile information
     */
    suspend fun updateProfile(
        userId: String,
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?
    ): Result<User>
    
    /**
     * Get user preferences
     */
    suspend fun getUserPreferences(userId: String): Result<Map<String, Any>>
    
    /**
     * Update user preferences
     */
    suspend fun updateUserPreferences(userId: String, preferences: Map<String, Any>): Result<Unit>
    
    /**
     * Observe user changes
     */
    fun observeUser(userId: String): Flow<User?>
    
    /**
     * Delete user account
     */
    suspend fun deleteUser(userId: String): Result<Unit>
    
    /**
     * Check if user exists
     */
    suspend fun userExists(userId: String): Result<Boolean>
    
    /**
     * Get user by email
     */
    suspend fun getUserByEmail(email: String): Result<User?>
    
    /**
     * Get user by phone
     */
    suspend fun getUserByPhone(phone: String): Result<User?>
}
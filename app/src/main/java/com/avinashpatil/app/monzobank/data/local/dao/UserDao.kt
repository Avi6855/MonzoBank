package com.avinashpatil.app.monzobank.data.local.dao

import androidx.room.*
import com.avinashpatil.app.monzobank.data.local.entity.AuthTokenEntity
import com.avinashpatil.app.monzobank.data.local.entity.BiometricAuthEntity
import com.avinashpatil.app.monzobank.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for User operations
 */
@Dao
interface UserDao {
    
    // User CRUD operations
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE email = :email OR phone = :phone")
    suspend fun getUserByEmailOrPhone(email: String, phone: String): UserEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>
    
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    // Authentication token operations
    @Query("SELECT * FROM auth_tokens WHERE user_id = :userId")
    suspend fun getAuthToken(userId: String): AuthTokenEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthToken(token: AuthTokenEntity)
    
    @Query("DELETE FROM auth_tokens WHERE user_id = :userId")
    suspend fun deleteAuthToken(userId: String)
    
    @Query("DELETE FROM auth_tokens")
    suspend fun deleteAllAuthTokens()
    
    @Query("SELECT * FROM auth_tokens WHERE access_token = :accessToken")
    suspend fun getTokenByAccessToken(accessToken: String): AuthTokenEntity?
    
    // Biometric authentication operations
    @Query("SELECT * FROM biometric_auth WHERE user_id = :userId")
    suspend fun getBiometricAuth(userId: String): BiometricAuthEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBiometricAuth(biometricAuth: BiometricAuthEntity)
    
    @Query("UPDATE biometric_auth SET is_enabled = :enabled WHERE user_id = :userId")
    suspend fun updateBiometricEnabled(userId: String, enabled: Boolean)
    
    @Query("UPDATE biometric_auth SET last_used = :lastUsed WHERE user_id = :userId")
    suspend fun updateBiometricLastUsed(userId: String, lastUsed: java.time.LocalDateTime)
    
    @Query("DELETE FROM biometric_auth WHERE user_id = :userId")
    suspend fun deleteBiometricAuth(userId: String)
    
    // Utility queries
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun isEmailExists(email: String): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE phone = :phone")
    suspend fun isPhoneExists(phone: String): Int
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT * FROM users WHERE kyc_status = :status")
    suspend fun getUsersByKycStatus(status: String): List<UserEntity>
    
    // Transaction for user registration
    @Transaction
    suspend fun registerUser(user: UserEntity, authToken: AuthTokenEntity) {
        insertUser(user)
        insertAuthToken(authToken)
    }
    
    // Transaction for user login
    @Transaction
    suspend fun loginUser(userId: String, authToken: AuthTokenEntity) {
        deleteAuthToken(userId) // Remove old token
        insertAuthToken(authToken) // Insert new token
    }
    
    // Transaction for logout
    @Transaction
    suspend fun logoutUser(userId: String) {
        deleteAuthToken(userId)
        // Note: We don't disable biometric here as user might want to keep it enabled
    }
}
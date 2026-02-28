package com.avinashpatil.app.monzobank.data.repository

import com.avinashpatil.app.monzobank.data.local.dao.UserDao
import com.avinashpatil.app.monzobank.data.local.entity.UserEntity
import com.avinashpatil.app.monzobank.domain.model.User
import com.avinashpatil.app.monzobank.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserRepository
 * Handles user operations with local database
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    
    override suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserById(userId)
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): Result<User?> {
        return try {
            val userEntity = userDao.getCurrentUser()
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUser(user: User): Result<User> {
        return try {
            val userEntity = user.toEntity()
            userDao.updateUser(userEntity)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfile(
        userId: String,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        address: String?
    ): Result<User> {
        return try {
            val existingUser = userDao.getUserById(userId)
            if (existingUser != null) {
                val updatedUser = existingUser.copy(
                    firstName = firstName ?: existingUser.firstName,
                    lastName = lastName ?: existingUser.lastName,
                    phone = phoneNumber ?: existingUser.phone,
                    updatedAt = java.time.LocalDateTime.now()
                )
                userDao.updateUser(updatedUser)
                Result.success(updatedUser.toDomainModel())
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserPreferences(userId: String): Result<Map<String, Any>> {
        return try {
            // For now, return empty preferences
            // In a real implementation, this would fetch from a preferences table
            Result.success(emptyMap())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserPreferences(userId: String, preferences: Map<String, Any>): Result<Unit> {
        return try {
            // For now, do nothing
            // In a real implementation, this would update a preferences table
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun observeUser(userId: String): Flow<User?> {
        return userDao.observeUser(userId).map { it?.toDomainModel() }
    }
    
    override suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            userDao.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun userExists(userId: String): Result<Boolean> {
        return try {
            val user = userDao.getUserById(userId)
            Result.success(user != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserByEmail(email: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserByEmail(email)
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserByPhone(phone: String): Result<User?> {
        return try {
            val userEntity = userDao.getUserByPhone(phone)
            Result.success(userEntity?.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Extension functions for mapping
    private fun UserEntity.toDomainModel(): User {
        return User(
            id = id,
            email = email,
            phoneNumber = phone,
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dateOfBirth.toString(),
            address = address,
            kycStatus = kycStatus,
            accountType = com.avinashpatil.app.monzobank.data.local.entity.AccountType.CURRENT, // Default account type
            createdAt = createdAt,
            updatedAt = updatedAt,
            isEmailVerified = true, // Default for now - should be tracked separately
            isPhoneVerified = true, // Default for now - should be tracked separately
            securitySettings = com.avinashpatil.app.monzobank.domain.model.SecuritySettings(
                userId = id,
                twoFactorEnabled = false, // Default for now
                biometricEnabled = biometricEnabled,
                createdAt = java.util.Date.from(createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant()),
                updatedAt = java.util.Date.from(updatedAt.atZone(java.time.ZoneId.systemDefault()).toInstant())
            )
        )
    }
    
    private fun User.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            email = email,
            phone = phoneNumber,
            passwordHash = "", // Password hash is handled by AuthRepository
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = java.time.LocalDate.parse(dateOfBirth ?: "1990-01-01"),
            address = address ?: com.avinashpatil.app.monzobank.domain.model.Address(
                street = "",
                city = "",
                state = "",
                postalCode = "",
                country = "UK"
            ),
            kycStatus = kycStatus,
            biometricEnabled = securitySettings?.biometricEnabled ?: false,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
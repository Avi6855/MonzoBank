package com.avinashpatil.app.monzobank.data.repository

import android.content.Context
import android.provider.Settings
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.avinashpatil.app.monzobank.data.local.dao.UserDao
import com.avinashpatil.app.monzobank.data.local.entity.AuthTokenEntity
import com.avinashpatil.app.monzobank.data.local.entity.BiometricAuthEntity
import com.avinashpatil.app.monzobank.data.local.entity.UserEntity
import com.avinashpatil.app.monzobank.data.remote.api.AuthApiService
// Removed duplicate auth subdirectory imports - using root dto package only
import com.avinashpatil.app.monzobank.data.remote.dto.BiometricLoginDto
import com.avinashpatil.app.monzobank.data.remote.dto.RefreshTokenRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.PasswordResetRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.VerifyPasswordResetDto
import com.avinashpatil.app.monzobank.data.remote.dto.EmailVerificationDto
import com.avinashpatil.app.monzobank.data.remote.dto.PhoneVerificationDto
import com.avinashpatil.app.monzobank.data.remote.dto.ResendVerificationDto
import com.avinashpatil.app.monzobank.data.remote.dto.TwoFactorSetupDto
import com.avinashpatil.app.monzobank.data.remote.dto.TwoFactorVerifyDto
import com.avinashpatil.app.monzobank.data.remote.dto.UpdateProfileDto
import com.avinashpatil.app.monzobank.data.remote.dto.ChangePasswordDto
import com.avinashpatil.app.monzobank.data.remote.dto.DeleteAccountDto
import com.avinashpatil.app.monzobank.data.remote.dto.BiometricSetupDto
import com.avinashpatil.app.monzobank.data.remote.dto.AuthResponseDto
import com.avinashpatil.app.monzobank.data.remote.dto.RegisterRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.LoginRequestDto
import com.avinashpatil.app.monzobank.data.remote.dto.UserDto
import com.avinashpatil.app.monzobank.data.remote.dto.common.AddressDto
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.domain.model.*
import com.avinashpatil.app.monzobank.domain.repository.AuthRepository
import com.avinashpatil.app.monzobank.domain.repository.AuthState
import com.avinashpatil.app.monzobank.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository
 * Handles authentication operations with both local and remote data sources
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) : AuthRepository {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    
    private val encryptedPrefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        EncryptedSharedPreferences.create(
            context,
            Constants.ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    
    override suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val requestDto = request.toDto()
            val response = authApiService.register(requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponseDto = response.body()!!
                
                // Save user and token locally if registration successful
                if (authResponseDto.success && authResponseDto.user != null && authResponseDto.accessToken != null) {
                    saveUserLocally(authResponseDto.user.toUser())
                    saveTokenLocally(JwtToken(
                        token = authResponseDto.accessToken,
                        refreshToken = authResponseDto.refreshToken ?: "",
                        expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000), // 24 hours
                        tokenType = "Bearer"
                    ))
                    _authState.value = AuthState.Authenticated(authResponseDto.user.toUser())
                }
                
                Result.success(authResponseDto.toAuthResponse())
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val requestDto = request.toDto()
            val response = authApiService.login(requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponseDto = response.body()!!
                
                if (authResponseDto.success && authResponseDto.user != null && authResponseDto.accessToken != null) {
                    saveUserLocally(authResponseDto.user.toUser())
                    saveTokenLocally(JwtToken(
                        token = authResponseDto.accessToken,
                        refreshToken = authResponseDto.refreshToken ?: "",
                        expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        tokenType = "Bearer"
                    ))
                    _authState.value = AuthState.Authenticated(authResponseDto.user.toUser())
                }
                
                Result.success(authResponseDto.toAuthResponse())
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun biometricLogin(userId: String, biometricToken: String): Result<AuthResponse> {
        return try {
            val requestDto = BiometricLoginDto(
                userId = userId,
                biometricToken = biometricToken,
                deviceId = deviceId
            )
            
            val response = authApiService.biometricLogin(requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponseDto = response.body()!!
                
                if (authResponseDto.success && authResponseDto.user != null && authResponseDto.accessToken != null) {
                    saveUserLocally(authResponseDto.user.toUser())
                    saveTokenLocally(JwtToken(
                        token = authResponseDto.accessToken,
                        refreshToken = authResponseDto.refreshToken ?: "",
                        expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        tokenType = "Bearer"
                    ))
                    _authState.value = AuthState.Authenticated(authResponseDto.user.toUser())
                }
                
                Result.success(authResponseDto.toAuthResponse())
            } else {
                Result.failure(Exception("Biometric login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            val requestDto = RefreshTokenRequestDto(refreshToken)
            val response = authApiService.refreshToken(requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponseDto = response.body()!!
                
                if (authResponseDto.success && authResponseDto.accessToken != null) {
                    saveTokenLocally(JwtToken(
                        token = authResponseDto.accessToken,
                        refreshToken = authResponseDto.refreshToken ?: refreshToken,
                        expiresAt = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        tokenType = "Bearer"
                    ))
                }
                
                Result.success(authResponseDto.toAuthResponse())
            } else {
                Result.failure(Exception("Token refresh failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout(): Result<Unit> {
        return try {
            val token = getAuthToken()
            if (token != null) {
                val response = authApiService.logout("Bearer ${token.token}")
                // Continue with local logout even if API call fails
            }
            
            clearAuthData()
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        } catch (e: Exception) {
            // Still clear local data even if API call fails
            clearAuthData()
            _authState.value = AuthState.Unauthenticated
            Result.success(Unit)
        }
    }
    
    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val requestDto = PasswordResetRequestDto(email)
            val response = authApiService.requestPasswordReset(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Password reset request failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyPasswordReset(token: String, newPassword: String): Result<Unit> {
        return try {
            val requestDto = VerifyPasswordResetDto(token, newPassword)
            val response = authApiService.verifyPasswordReset(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Password reset verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Additional methods implementation...
    override suspend fun verifyEmail(email: String, code: String): Result<Unit> {
        return try {
            val requestDto = EmailVerificationDto(email, code)
            val response = authApiService.verifyEmail(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Email verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyPhone(phone: String, code: String): Result<Unit> {
        return try {
            val requestDto = PhoneVerificationDto(phone, code)
            val response = authApiService.verifyPhone(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Phone verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun resendVerificationCode(email: String?, phone: String?, type: String): Result<Unit> {
        return try {
            val requestDto = ResendVerificationDto(email, phone, type)
            val response = authApiService.resendVerificationCode(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Resend verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setupTwoFactorAuth(method: TwoFactorMethod, phone: String?): Result<TwoFactorAuthSetup> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val requestDto = TwoFactorSetupDto(method.name, phone)
            val response = authApiService.setupTwoFactorAuth("Bearer ${token.token}", requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val responseDto = response.body()!!
                val setup = TwoFactorAuthSetup(
                    method = method,
                    isEnabled = responseDto.success,
                    phoneNumber = phone,
                    email = getCurrentUser()?.email,
                    secretKey = responseDto.secretKey,
                    backupCodes = responseDto.backupCodes ?: emptyList()
                )
                Result.success(setup)
            } else {
                Result.failure(Exception("2FA setup failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun verifyTwoFactorAuth(userId: String, code: String, method: TwoFactorMethod): Result<AuthResponse> {
        return try {
            val requestDto = TwoFactorVerifyDto(userId, code, method.name)
            val response = authApiService.verifyTwoFactorAuth(requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val authResponseDto = response.body()!!
                Result.success(authResponseDto.toAuthResponse())
            } else {
                Result.failure(Exception("2FA verification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun disableTwoFactorAuth(): Result<Unit> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val response = authApiService.disableTwoFactorAuth("Bearer ${token.token}")
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Disable 2FA failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserProfile(): Result<User> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val response = authApiService.getUserProfile("Bearer ${token.token}")
            
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.user.toUser()
                saveUserLocally(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Get profile failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val requestDto = UpdateProfileDto(
                firstName = user.firstName,
                lastName = user.lastName,
                phone = user.phoneNumber,
                address = user.address?.let {
                    com.avinashpatil.app.monzobank.data.remote.dto.AddressDto(
                        street = it.street,
                        city = it.city,
                        state = it.state,
                        postalCode = it.postalCode,
                        country = it.country
                    )
                }
            )
            
            val response = authApiService.updateUserProfile("Bearer ${token.token}", requestDto)
            
            if (response.isSuccessful && response.body() != null) {
                val updatedUser = response.body()!!.user.toUser()
                saveUserLocally(updatedUser)
                Result.success(updatedUser)
            } else {
                Result.failure(Exception("Update profile failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val requestDto = ChangePasswordDto(currentPassword, newPassword)
            val response = authApiService.changePassword("Bearer ${token.token}", requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Change password failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAccount(password: String, reason: String?): Result<Unit> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val requestDto = DeleteAccountDto(password, reason)
            val response = authApiService.deleteAccount("Bearer ${token.token}", requestDto)
            
            if (response.isSuccessful) {
                clearAuthData()
                _authState.value = AuthState.Unauthenticated
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete account failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkEmailExists(email: String): Result<Boolean> {
        return try {
            val response = authApiService.checkEmailExists(email)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.exists)
            } else {
                Result.failure(Exception("Check email failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun checkPhoneExists(phone: String): Result<Boolean> {
        return try {
            val response = authApiService.checkPhoneExists(phone)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.exists)
            } else {
                Result.failure(Exception("Check phone failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun setupBiometricAuth(biometricKey: String, deviceId: String): Result<Unit> {
        return try {
            val token = getAuthToken()
            if (token == null) {
                return Result.failure(Exception("No authentication token found"))
            }
            
            val requestDto = BiometricSetupDto(biometricKey, deviceId)
            val response = authApiService.setupBiometricAuth("Bearer ${token.token}", requestDto)
            
            if (response.isSuccessful) {
                // Save biometric setup locally
                val user = getCurrentUser()
                if (user != null) {
                    val biometricEntity = BiometricAuthEntity(
                        id = UUID.randomUUID().toString(),
                        userId = user.id,
                        biometricKey = biometricKey,
                        isEnabled = true,
                        createdAt = LocalDateTime.now(),
                        lastUsed = null
                    )
                    userDao.insertBiometricAuth(biometricEntity)
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Biometric setup failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        return try {
            val userId = encryptedPrefs.getString(Constants.PREF_USER_ID, null)
            if (userId != null) {
                val userEntity = userDao.getUserById(userId)
                userEntity?.toDomainModel()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getAuthToken(): JwtToken? {
        return try {
            val accessToken = encryptedPrefs.getString(Constants.PREF_ACCESS_TOKEN, null)
            val refreshToken = encryptedPrefs.getString(Constants.PREF_REFRESH_TOKEN, null)
            val expiresAt = encryptedPrefs.getLong(Constants.PREF_TOKEN_EXPIRES, 0L)
            
            if (accessToken != null && refreshToken != null) {
                JwtToken(
                    token = accessToken,
                    refreshToken = refreshToken,
                    expiresAt = expiresAt,
                    tokenType = "Bearer"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveUser(user: User) {
        saveUserLocally(user)
    }
    
    override suspend fun saveAuthToken(token: JwtToken) {
        saveTokenLocally(token)
    }
    
    override suspend fun clearAuthData() {
        try {
            val userId = encryptedPrefs.getString(Constants.PREF_USER_ID, null)
            if (userId != null) {
                userDao.logoutUser(userId)
            }
            
            encryptedPrefs.edit()
                .remove(Constants.PREF_USER_ID)
                .remove(Constants.PREF_ACCESS_TOKEN)
                .remove(Constants.PREF_REFRESH_TOKEN)
                .remove(Constants.PREF_TOKEN_EXPIRES)
                .apply()
        } catch (e: Exception) {
            // Log error but don't throw
        }
    }
    
    override suspend fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        return token != null && isTokenValid()
    }
    
    override suspend fun isBiometricEnabled(): Boolean {
        return try {
            val user = getCurrentUser()
            if (user != null) {
                val biometricAuth = userDao.getBiometricAuth(user.id)
                biometricAuth?.isEnabled == true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun setBiometricEnabled(enabled: Boolean) {
        try {
            val user = getCurrentUser()
            if (user != null) {
                userDao.updateBiometricEnabled(user.id, enabled)
            }
        } catch (e: Exception) {
            // Log error
        }
    }
    

    
    override fun getAuthState(): Flow<AuthState> {
        return _authState.asStateFlow()
    }
    
    override suspend fun isTokenValid(): Boolean {
        return try {
            val token = getAuthToken()
            if (token != null) {
                val currentTime = System.currentTimeMillis()
                currentTime < token.expiresAt
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    // Private helper methods
    private suspend fun saveUserLocally(user: User) {
        try {
            val userEntity = UserEntity(
                id = user.id,
                email = user.email,
                phone = user.phoneNumber,
                passwordHash = "", // Don't store password hash locally
                firstName = user.firstName,
                lastName = user.lastName,
                dateOfBirth = user.dateOfBirth?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                address = user.address ?: Address(
                    street = "",
                    city = "",
                    state = "",
                    postalCode = "",
                    country = ""
                ),
                kycStatus = user.kycStatus,
                biometricEnabled = user.preferences?.biometricEnabled ?: false,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            )
            
            userDao.insertUser(userEntity)
            
            encryptedPrefs.edit()
                .putString(Constants.PREF_USER_ID, user.id)
                .apply()
        } catch (e: Exception) {
            // Log error
        }
    }
    
    private fun saveTokenLocally(token: JwtToken) {
        try {
            encryptedPrefs.edit()
                .putString(Constants.PREF_ACCESS_TOKEN, token.token)
                .putString(Constants.PREF_REFRESH_TOKEN, token.refreshToken)
                .putLong(Constants.PREF_TOKEN_EXPIRES, token.expiresAt)
                .apply()
        } catch (e: Exception) {
            // Log error
        }
    }
}

// Extension functions for mapping between DTOs and domain models
private fun AuthResponseDto.toAuthResponse(): AuthResponse {
    return AuthResponse(
        success = success,
        token = accessToken,
        refreshToken = refreshToken,
        user = user?.toUser(),
        message = message,
        requiresTwoFactor = false,
        twoFactorMethods = null
    )
}

private fun RegisterRequest.toDto(): RegisterRequestDto {
    return RegisterRequestDto(
        email = email,
        phone = phone,
        password = password,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth.toString(),
        address = address.toDto()
    )
}

private fun LoginRequest.toDto(): LoginRequestDto {
    return LoginRequestDto(
        email = email,
        password = password,
        biometricToken = null,
        deviceId = null
    )
}

private fun Address.toDto(): com.avinashpatil.app.monzobank.data.remote.dto.AddressDto {
    return com.avinashpatil.app.monzobank.data.remote.dto.AddressDto(
        street = street,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country
    )
}

private fun UserDto.toUser(): User {
    return User(
        id = id,
        email = email,
        phoneNumber = phone,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth,
        address = address?.toDomainModel(),
        kycStatus = KYCStatus.valueOf(kycStatus),
        accountType = AccountType.CURRENT,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
        isEmailVerified = isEmailVerified,
        isPhoneVerified = isPhoneVerified,
        preferences = UserPreferences(
            userId = id,
            biometricEnabled = biometricEnabled,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )
    )
}

private fun com.avinashpatil.app.monzobank.data.remote.dto.AddressDto.toDomainModel(): Address {
    return Address(
        street = street,
        city = city,
        state = state,
        postalCode = postalCode,
        country = country
    )
}

private fun UserEntity.toDomainModel(): User {
    return User(
        id = id,
        email = email,
        phoneNumber = phone,
        firstName = firstName,
        lastName = lastName,
        dateOfBirth = dateOfBirth?.toString(),
        address = address,
        kycStatus = kycStatus,
        accountType = AccountType.CURRENT,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isEmailVerified = true, // Default value since not stored in UserEntity
        isPhoneVerified = true, // Default value since not stored in UserEntity
        preferences = UserPreferences(
            userId = id,
            biometricEnabled = biometricEnabled,
            createdAt = java.util.Date(),
            updatedAt = java.util.Date()
        )
    )
}
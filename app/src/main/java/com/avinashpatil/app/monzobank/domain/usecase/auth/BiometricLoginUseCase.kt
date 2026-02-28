package com.avinashpatil.app.monzobank.domain.usecase.auth

import com.avinashpatil.app.monzobank.domain.model.AuthResponse
import com.avinashpatil.app.monzobank.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for biometric authentication login
 */
class BiometricLoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(userId: String, biometricToken: String): Result<AuthResponse> {
        if (userId.isBlank()) {
            return Result.failure(Exception("User ID is required"))
        }
        
        if (biometricToken.isBlank()) {
            return Result.failure(Exception("Biometric token is required"))
        }
        
        // Check if biometric is enabled for this user
        val isBiometricEnabled = authRepository.isBiometricEnabled()
        if (!isBiometricEnabled) {
            return Result.failure(Exception("Biometric authentication is not enabled"))
        }
        
        return authRepository.biometricLogin(userId, biometricToken)
    }
}

/**
 * Use case for logout
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}

/**
 * Use case for checking authentication state
 */
class GetAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    operator fun invoke() = authRepository.getAuthState()
}

/**
 * Use case for checking if user is logged in
 */
class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(): Boolean {
        return authRepository.isLoggedIn()
    }
}

/**
 * Use case for getting current user
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke() = authRepository.getCurrentUser()
}

/**
 * Use case for password reset request
 */
class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email is required"))
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        return authRepository.requestPasswordReset(email.trim().lowercase())
    }
}

/**
 * Use case for verifying password reset
 */
class VerifyPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(token: String, newPassword: String): Result<Unit> {
        if (token.isBlank()) {
            return Result.failure(Exception("Reset token is required"))
        }
        
        if (newPassword.isBlank()) {
            return Result.failure(Exception("New password is required"))
        }
        
        if (newPassword.length < 8) {
            return Result.failure(Exception("Password must be at least 8 characters"))
        }
        
        return authRepository.verifyPasswordReset(token, newPassword)
    }
}

/**
 * Use case for email verification
 */
class VerifyEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(email: String, code: String): Result<Unit> {
        if (email.isBlank()) {
            return Result.failure(Exception("Email is required"))
        }
        
        if (code.isBlank()) {
            return Result.failure(Exception("Verification code is required"))
        }
        
        if (code.length != 6) {
            return Result.failure(Exception("Verification code must be 6 digits"))
        }
        
        return authRepository.verifyEmail(email.trim().lowercase(), code)
    }
}

/**
 * Use case for phone verification
 */
class VerifyPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(phone: String, code: String): Result<Unit> {
        if (phone.isBlank()) {
            return Result.failure(Exception("Phone number is required"))
        }
        
        if (code.isBlank()) {
            return Result.failure(Exception("Verification code is required"))
        }
        
        if (code.length != 6) {
            return Result.failure(Exception("Verification code must be 6 digits"))
        }
        
        return authRepository.verifyPhone(phone.trim(), code)
    }
}

/**
 * Use case for resending verification code
 */
class ResendVerificationCodeUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(email: String?, phone: String?, type: String): Result<Unit> {
        when (type.uppercase()) {
            "EMAIL" -> {
                if (email.isNullOrBlank()) {
                    return Result.failure(Exception("Email is required for email verification"))
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    return Result.failure(Exception("Invalid email format"))
                }
            }
            "PHONE" -> {
                if (phone.isNullOrBlank()) {
                    return Result.failure(Exception("Phone number is required for phone verification"))
                }
            }
            else -> {
                return Result.failure(Exception("Invalid verification type. Must be EMAIL or PHONE"))
            }
        }
        
        return authRepository.resendVerificationCode(
            email?.trim()?.lowercase(),
            phone?.trim(),
            type.uppercase()
        )
    }
}
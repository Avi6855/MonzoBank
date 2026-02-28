package com.avinashpatil.monzobank.dto

import com.avinashpatil.monzobank.entity.KycStatus
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

data class CreateUserRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,
    
    @field:Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @field:NotBlank(message = "Phone number is required")
    val phone: String,
    
    @field:Size(min = 8, message = "Password must be at least 8 characters")
    @field:NotBlank(message = "Password is required")
    val password: String,
    
    @field:NotBlank(message = "First name is required")
    @field:Size(max = 100, message = "First name must not exceed 100 characters")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    @field:Size(max = 100, message = "Last name must not exceed 100 characters")
    val lastName: String,
    
    @field:NotNull(message = "Date of birth is required")
    @field:Past(message = "Date of birth must be in the past")
    val dateOfBirth: LocalDate,
    
    val address: String? = null
)

data class UpdateUserRequest(
    @field:Size(max = 100, message = "First name must not exceed 100 characters")
    val firstName: String? = null,
    
    @field:Size(max = 100, message = "Last name must not exceed 100 characters")
    val lastName: String? = null,
    
    @field:Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phone: String? = null,
    
    val address: String? = null,
    
    val profileImageUrl: String? = null
)

data class UserResponse(
    val id: UUID,
    val email: String,
    val phone: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: LocalDate,
    val address: String?,
    val kycStatus: KycStatus,
    val biometricEnabled: Boolean,
    val isActive: Boolean,
    val emailVerified: Boolean,
    val phoneVerified: Boolean,
    val profileImageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class LoginRequest(
    @field:NotBlank(message = "Email or phone is required")
    val emailOrPhone: String,
    
    @field:NotBlank(message = "Password is required")
    val password: String,
    
    val rememberMe: Boolean = false
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val user: UserResponse
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class ChangePasswordRequest(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:Size(min = 8, message = "New password must be at least 8 characters")
    @field:NotBlank(message = "New password is required")
    val newPassword: String
)

data class ForgotPasswordRequest(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String
)

data class ResetPasswordRequest(
    @field:NotBlank(message = "Reset token is required")
    val resetToken: String,
    
    @field:Size(min = 8, message = "New password must be at least 8 characters")
    @field:NotBlank(message = "New password is required")
    val newPassword: String
)

data class VerifyEmailRequest(
    @field:NotBlank(message = "Verification token is required")
    val verificationToken: String
)

data class VerifyPhoneRequest(
    @field:NotBlank(message = "Verification code is required")
    @field:Size(min = 6, max = 6, message = "Verification code must be 6 digits")
    val verificationCode: String
)
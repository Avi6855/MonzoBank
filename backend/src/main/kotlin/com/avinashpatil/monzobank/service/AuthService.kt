package com.avinashpatil.monzobank.service

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.entity.User
import com.avinashpatil.monzobank.exception.*
import com.avinashpatil.monzobank.repository.UserRepository
import com.avinashpatil.monzobank.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailService: EmailService,
    private val smsService: SmsService
) {
    
    fun register(request: CreateUserRequest): LoginResponse {
        // Create user through UserService
        val userResponse = userService.createUser(request)
        
        // Send verification emails/SMS
        sendVerificationEmail(userResponse.email)
        sendVerificationSms(userResponse.phone)
        
        // Generate tokens
        val accessToken = jwtTokenProvider.generateAccessToken(userResponse.id.toString())
        val refreshToken = jwtTokenProvider.generateRefreshToken(userResponse.id.toString())
        
        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtTokenProvider.getAccessTokenExpiration(),
            user = userResponse
        )
    }
    
    fun login(request: LoginRequest): LoginResponse {
        // Find user by email or phone
        val user = findUserByEmailOrPhone(request.emailOrPhone)
            ?: throw InvalidCredentialsException("Invalid credentials")
        
        // Verify password
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw InvalidCredentialsException("Invalid credentials")
        }
        
        // Check if user is active
        if (!user.isActive) {
            throw AccessDeniedException("Account is deactivated")
        }
        
        // Generate tokens
        val accessToken = jwtTokenProvider.generateAccessToken(user.id.toString())
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.id.toString())
        
        val userResponse = mapToUserResponse(user)
        
        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtTokenProvider.getAccessTokenExpiration(),
            user = userResponse
        )
    }
    
    fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw InvalidTokenException("Invalid refresh token")
        }
        
        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow { UserNotFoundException("User not found") }
        
        if (!user.isActive) {
            throw AccessDeniedException("Account is deactivated")
        }
        
        // Generate new tokens
        val newAccessToken = jwtTokenProvider.generateAccessToken(user.id.toString())
        val newRefreshToken = jwtTokenProvider.generateRefreshToken(user.id.toString())
        
        val userResponse = mapToUserResponse(user)
        
        return LoginResponse(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            expiresIn = jwtTokenProvider.getAccessTokenExpiration(),
            user = userResponse
        )
    }
    
    fun changePassword(userId: UUID, request: ChangePasswordRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException("User not found") }
        
        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword, user.passwordHash)) {
            throw InvalidCredentialsException("Current password is incorrect")
        }
        
        // Update password
        val updatedUser = user.copy(
            passwordHash = passwordEncoder.encode(request.newPassword)
        )
        
        userRepository.save(updatedUser)
    }
    
    fun forgotPassword(request: ForgotPasswordRequest) {
        val user = userRepository.findByEmail(request.email)
            ?: throw UserNotFoundException("User not found with email: ${request.email}")
        
        // Generate reset token
        val resetToken = jwtTokenProvider.generatePasswordResetToken(user.id.toString())
        
        // Send reset email
        emailService.sendPasswordResetEmail(user.email, resetToken)
    }
    
    fun resetPassword(request: ResetPasswordRequest) {
        if (!jwtTokenProvider.validateToken(request.resetToken)) {
            throw InvalidTokenException("Invalid or expired reset token")
        }
        
        val userId = jwtTokenProvider.getUserIdFromToken(request.resetToken)
        val user = userRepository.findById(UUID.fromString(userId))
            .orElseThrow { UserNotFoundException("User not found") }
        
        // Update password
        val updatedUser = user.copy(
            passwordHash = passwordEncoder.encode(request.newPassword)
        )
        
        userRepository.save(updatedUser)
    }
    
    fun verifyEmail(request: VerifyEmailRequest) {
        if (!jwtTokenProvider.validateToken(request.verificationToken)) {
            throw InvalidTokenException("Invalid or expired verification token")
        }
        
        val userId = jwtTokenProvider.getUserIdFromToken(request.verificationToken)
        userService.verifyEmail(UUID.fromString(userId))
    }
    
    fun verifyPhone(userId: UUID, request: VerifyPhoneRequest) {
        // In a real implementation, you would verify the code against a stored value
        // For now, we'll assume the code is valid if it's 6 digits
        if (request.verificationCode.length != 6 || !request.verificationCode.all { it.isDigit() }) {
            throw ValidationException("Invalid verification code")
        }
        
        userService.verifyPhone(userId)
    }
    
    fun logout(userId: UUID) {
        // In a real implementation, you might want to blacklist the token
        // For now, we'll just log the logout event
        println("User $userId logged out")
    }
    
    private fun findUserByEmailOrPhone(emailOrPhone: String): User? {
        return if (emailOrPhone.contains("@")) {
            userRepository.findByEmail(emailOrPhone)
        } else {
            userRepository.findByPhone(emailOrPhone)
        }
    }
    
    private fun sendVerificationEmail(email: String) {
        // Generate verification token
        val user = userRepository.findByEmail(email)!!
        val verificationToken = jwtTokenProvider.generateEmailVerificationToken(user.id.toString())
        
        // Send verification email
        emailService.sendVerificationEmail(email, verificationToken)
    }
    
    private fun sendVerificationSms(phone: String) {
        // Generate verification code
        val verificationCode = (100000..999999).random().toString()
        
        // Send SMS
        smsService.sendVerificationSms(phone, verificationCode)
    }
    
    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id,
            email = user.email,
            phone = user.phone,
            firstName = user.firstName,
            lastName = user.lastName,
            dateOfBirth = user.dateOfBirth,
            address = user.address,
            kycStatus = user.kycStatus,
            biometricEnabled = user.biometricEnabled,
            isActive = user.isActive,
            emailVerified = user.emailVerified,
            phoneVerified = user.phoneVerified,
            profileImageUrl = user.profileImageUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}
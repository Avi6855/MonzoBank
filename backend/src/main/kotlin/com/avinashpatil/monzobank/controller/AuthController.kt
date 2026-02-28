package com.avinashpatil.monzobank.controller

import com.avinashpatil.monzobank.dto.*
import com.avinashpatil.monzobank.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("User registered successfully", response))
    }
    
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.login(request)
        return ResponseEntity.ok(ApiResponse.success("Login successful", response))
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response))
    }
    
    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal userId: String): ResponseEntity<ApiResponse<Unit>> {
        authService.logout(UUID.fromString(userId))
        return ResponseEntity.ok(ApiResponse.success("Logout successful"))
    }
    
    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.changePassword(UUID.fromString(userId), request)
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"))
    }
    
    @PostMapping("/forgot-password")
    fun forgotPassword(@Valid @RequestBody request: ForgotPasswordRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.forgotPassword(request)
        return ResponseEntity.ok(ApiResponse.success("Password reset email sent"))
    }
    
    @PostMapping("/reset-password")
    fun resetPassword(@Valid @RequestBody request: ResetPasswordRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.resetPassword(request)
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully"))
    }
    
    @PostMapping("/verify-email")
    fun verifyEmail(@Valid @RequestBody request: VerifyEmailRequest): ResponseEntity<ApiResponse<Unit>> {
        authService.verifyEmail(request)
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"))
    }
    
    @GetMapping("/verify-email")
    fun verifyEmailByLink(@RequestParam token: String): ResponseEntity<String> {
        authService.verifyEmail(VerifyEmailRequest(token))
        return ResponseEntity.ok("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Email Verified</title>
                <style>
                    body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
                    .success { color: #4CAF50; }
                </style>
            </head>
            <body>
                <h1 class="success">Email Verified Successfully!</h1>
                <p>Your email has been verified. You can now close this window and return to the app.</p>
            </body>
            </html>
        """.trimIndent())
    }
    
    @PostMapping("/verify-phone")
    fun verifyPhone(
        @AuthenticationPrincipal userId: String,
        @Valid @RequestBody request: VerifyPhoneRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        authService.verifyPhone(UUID.fromString(userId), request)
        return ResponseEntity.ok(ApiResponse.success("Phone verified successfully"))
    }
    
    @PostMapping("/resend-verification-email")
    fun resendVerificationEmail(@AuthenticationPrincipal userId: String): ResponseEntity<ApiResponse<Unit>> {
        // Implementation would resend verification email
        return ResponseEntity.ok(ApiResponse.success("Verification email sent"))
    }
    
    @PostMapping("/resend-verification-sms")
    fun resendVerificationSms(@AuthenticationPrincipal userId: String): ResponseEntity<ApiResponse<Unit>> {
        // Implementation would resend verification SMS
        return ResponseEntity.ok(ApiResponse.success("Verification SMS sent"))
    }
}

// Generic API Response wrapper
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun <T> success(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(true, message, data)
        }
        
        fun <T> error(message: String, data: T? = null): ApiResponse<T> {
            return ApiResponse(false, message, data)
        }
    }
}
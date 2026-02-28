package com.avinashpatil.app.monzobank.domain.usecase.auth

import com.avinashpatil.app.monzobank.domain.model.AuthResponse
import com.avinashpatil.app.monzobank.domain.model.LoginRequest
import com.avinashpatil.app.monzobank.domain.repository.AuthRepository
import com.avinashpatil.app.monzobank.utils.Constants
import javax.inject.Inject

/**
 * Use case for user login
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        // Validate input
        if (email.isBlank()) {
            return Result.failure(Exception("Email is required"))
        }
        
        if (password.isBlank()) {
            return Result.failure(Exception("Password is required"))
        }
        
        if (!isValidEmail(email)) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            return Result.failure(Exception("Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters"))
        }
        
        val loginRequest = LoginRequest(
            email = email.trim().lowercase(),
            password = password
        )
        
        return authRepository.login(loginRequest)
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
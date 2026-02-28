package com.avinashpatil.app.monzobank.domain.usecase.auth

import com.avinashpatil.app.monzobank.domain.model.Address
import com.avinashpatil.app.monzobank.domain.model.AuthResponse
import com.avinashpatil.app.monzobank.domain.model.RegisterRequest
import com.avinashpatil.app.monzobank.domain.repository.AuthRepository
import com.avinashpatil.app.monzobank.utils.Constants
import java.time.LocalDate
import java.time.Period
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * Use case for user registration
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    
    suspend operator fun invoke(
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        dateOfBirth: LocalDate,
        address: Address
    ): Result<AuthResponse> {
        
        // Validate input
        val validationResult = validateInput(
            email, phone, password, confirmPassword,
            firstName, lastName, dateOfBirth, address
        )
        
        if (validationResult.isFailure) {
            return validationResult
        }
        
        // Check if email already exists
        val emailExistsResult = authRepository.checkEmailExists(email.trim().lowercase())
        if (emailExistsResult.isSuccess && emailExistsResult.getOrNull() == true) {
            return Result.failure(Exception("Email already exists"))
        }
        
        // Check if phone already exists
        val phoneExistsResult = authRepository.checkPhoneExists(phone.trim())
        if (phoneExistsResult.isSuccess && phoneExistsResult.getOrNull() == true) {
            return Result.failure(Exception("Phone number already exists"))
        }
        
        val registerRequest = RegisterRequest(
            email = email.trim().lowercase(),
            phone = phone.trim(),
            password = password,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dateOfBirth = dateOfBirth,
            address = address
        )
        
        return authRepository.register(registerRequest)
    }
    
    private fun validateInput(
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        dateOfBirth: LocalDate,
        address: Address
    ): Result<AuthResponse> {
        
        // Email validation
        if (email.isBlank()) {
            return Result.failure(Exception("Email is required"))
        }
        
        if (!isValidEmail(email)) {
            return Result.failure(Exception("Invalid email format"))
        }
        
        // Phone validation
        if (phone.isBlank()) {
            return Result.failure(Exception("Phone number is required"))
        }
        
        if (!isValidPhone(phone)) {
            return Result.failure(Exception("Invalid phone number format"))
        }
        
        // Password validation
        if (password.isBlank()) {
            return Result.failure(Exception("Password is required"))
        }
        
        if (password.length < Constants.MIN_PASSWORD_LENGTH) {
            return Result.failure(Exception("Password must be at least ${Constants.MIN_PASSWORD_LENGTH} characters"))
        }
        
        if (password.length > Constants.MAX_PASSWORD_LENGTH) {
            return Result.failure(Exception("Password must be less than ${Constants.MAX_PASSWORD_LENGTH} characters"))
        }
        
        if (!isValidPassword(password)) {
            return Result.failure(Exception("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"))
        }
        
        if (password != confirmPassword) {
            return Result.failure(Exception("Passwords do not match"))
        }
        
        // Name validation
        if (firstName.isBlank()) {
            return Result.failure(Exception("First name is required"))
        }
        
        if (firstName.length < Constants.MIN_NAME_LENGTH || firstName.length > Constants.MAX_NAME_LENGTH) {
            return Result.failure(Exception("First name must be between ${Constants.MIN_NAME_LENGTH} and ${Constants.MAX_NAME_LENGTH} characters"))
        }
        
        if (lastName.isBlank()) {
            return Result.failure(Exception("Last name is required"))
        }
        
        if (lastName.length < Constants.MIN_NAME_LENGTH || lastName.length > Constants.MAX_NAME_LENGTH) {
            return Result.failure(Exception("Last name must be between ${Constants.MIN_NAME_LENGTH} and ${Constants.MAX_NAME_LENGTH} characters"))
        }
        
        // Age validation (must be at least 18 years old)
        val age = Period.between(dateOfBirth, LocalDate.now()).years
        if (age < 18) {
            return Result.failure(Exception("You must be at least 18 years old to register"))
        }
        
        if (age > 120) {
            return Result.failure(Exception("Invalid date of birth"))
        }
        
        // Address validation
        if (address.street.isBlank()) {
            return Result.failure(Exception("Street address is required"))
        }
        
        if (address.city.isBlank()) {
            return Result.failure(Exception("City is required"))
        }
        
        if (address.postalCode.isBlank()) {
            return Result.failure(Exception("Postcode is required"))
        }
        
        if (address.country.isBlank()) {
            return Result.failure(Exception("Country is required"))
        }
        
        return Result.success(AuthResponse(
            success = true,
            token = null,
            refreshToken = null,
            user = null,
            message = "Validation successful"
        ))
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = Pattern.compile(Constants.PHONE_PATTERN)
        return phonePattern.matcher(phone).matches()
    }
    
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Pattern.compile(Constants.PASSWORD_PATTERN)
        return passwordPattern.matcher(password).matches()
    }
}
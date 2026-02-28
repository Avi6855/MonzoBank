package com.avinashpatil.app.monzobank.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

/**
 * Form validation state management
 */
class FormValidationState {
    private val _errors = mutableMapOf<String, String?>()
    val errors: Map<String, String?> get() = _errors
    
    fun setError(field: String, error: String?) {
        _errors[field] = error
    }
    
    fun clearError(field: String) {
        _errors.remove(field)
    }
    
    fun hasErrors(): Boolean = _errors.values.any { it != null }
    
    fun getError(field: String): String? = _errors[field]
    
    fun isFieldValid(field: String): Boolean = _errors[field] == null
}

/**
 * Composable for validated text input
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    validator: (String) -> String? = { null },
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    
    LaunchedEffect(value) {
        if (hasBeenFocused) {
            errorMessage = validator(value)
        }
    }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                hasBeenFocused = true
                errorMessage = validator(newValue)
            },
            label = { Text(label) },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            maxLines = maxLines,
            enabled = enabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (errorMessage != null) Color.Red else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (errorMessage != null) Color.Red else MaterialTheme.colorScheme.outline
            )
        )
        
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

/**
 * Email validation
 */
fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email is required"
        !email.isValidEmail() -> "Please enter a valid email address"
        else -> null
    }
}

/**
 * Password validation
 */
fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.isValidPassword() -> "Password must contain at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character"
        else -> null
    }
}

/**
 * Confirm password validation
 */
fun validateConfirmPassword(password: String, confirmPassword: String): String? {
    return when {
        confirmPassword.isBlank() -> "Please confirm your password"
        password != confirmPassword -> "Passwords do not match"
        else -> null
    }
}

/**
 * Phone number validation
 */
fun validatePhoneNumber(phone: String): String? {
    return when {
        phone.isBlank() -> "Phone number is required"
        !phone.isValidPhoneNumber() -> "Please enter a valid UK phone number"
        else -> null
    }
}

/**
 * Amount validation
 */
fun validateAmount(amount: String): String? {
    return when {
        amount.isBlank() -> "Amount is required"
        !amount.isValidAmount() -> "Please enter a valid amount"
        else -> {
            try {
                val decimal = BigDecimal(amount)
                when {
                    decimal <= BigDecimal.ZERO -> "Amount must be greater than zero"
                    decimal > BigDecimal("10000") -> "Amount cannot exceed £10,000"
                    else -> null
                }
            } catch (e: Exception) {
                "Please enter a valid amount"
            }
        }
    }
}

/**
 * Transfer amount validation with account context
 */
fun validateTransferAmount(
    amount: String,
    accountBalance: BigDecimal,
    overdraftLimit: BigDecimal? = null,
    dailyLimit: BigDecimal? = null
): String? {
    val basicValidation = validateAmount(amount)
    if (basicValidation != null) return basicValidation
    
    try {
        val decimal = BigDecimal(amount)
        val validation = SecurityUtils.isValidTransferAmount(
            amount = decimal,
            accountBalance = accountBalance,
            overdraftLimit = overdraftLimit,
            dailyLimit = dailyLimit,
            monthlyLimit = null
        )
        
        return if (validation.isValid) null else validation.message
    } catch (e: Exception) {
        return "Please enter a valid amount"
    }
}

/**
 * Account number validation
 */
fun validateAccountNumber(accountNumber: String): String? {
    return when {
        accountNumber.isBlank() -> "Account number is required"
        !accountNumber.isValidAccountNumber() -> "Account number must be 8 digits"
        else -> null
    }
}

/**
 * Sort code validation
 */
fun validateSortCode(sortCode: String): String? {
    return when {
        sortCode.isBlank() -> "Sort code is required"
        !sortCode.isValidSortCode() -> "Sort code must be in format XX-XX-XX"
        else -> null
    }
}

/**
 * Card number validation
 */
fun validateCardNumber(cardNumber: String): String? {
    return when {
        cardNumber.isBlank() -> "Card number is required"
        !cardNumber.isValidCardNumber() -> "Please enter a valid card number"
        else -> null
    }
}

/**
 * PIN validation
 */
fun validatePin(pin: String): String? {
    return when {
        pin.isBlank() -> "PIN is required"
        !SecurityUtils.isValidPin(pin) -> "PIN must be 4-6 digits"
        else -> null
    }
}

/**
 * Name validation
 */
fun validateName(name: String): String? {
    return when {
        name.isBlank() -> "Name is required"
        name.length < 2 -> "Name must be at least 2 characters"
        name.length > 50 -> "Name cannot exceed 50 characters"
        !name.matches(Regex("^[a-zA-Z\\s'-]+$")) -> "Name can only contain letters, spaces, hyphens, and apostrophes"
        else -> null
    }
}

/**
 * Reference validation
 */
fun validateReference(reference: String): String? {
    return when {
        reference.length > 18 -> "Reference cannot exceed 18 characters"
        reference.contains(Regex("[<>\"&']")) -> "Reference contains invalid characters"
        else -> null
    }
}

/**
 * Date of birth validation
 */
fun validateDateOfBirth(dateString: String): String? {
    if (dateString.isBlank()) return "Date of birth is required"
    
    try {
        // Assuming format DD/MM/YYYY
        val parts = dateString.split("/")
        if (parts.size != 3) return "Please use DD/MM/YYYY format"
        
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        
        if (day < 1 || day > 31) return "Invalid day"
        if (month < 1 || month > 12) return "Invalid month"
        if (year < 1900 || year > 2010) return "Invalid year"
        
        // Check if user is at least 18 years old
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        if (currentYear - year < 18) return "You must be at least 18 years old"
        
        return null
    } catch (e: Exception) {
        return "Please enter a valid date in DD/MM/YYYY format"
    }
}

/**
 * Address validation
 */
fun validateAddress(address: String): String? {
    return when {
        address.isBlank() -> "Address is required"
        address.length < 10 -> "Please enter a complete address"
        address.length > 100 -> "Address cannot exceed 100 characters"
        else -> null
    }
}

/**
 * Postcode validation (UK format)
 */
fun validatePostcode(postcode: String): String? {
    val ukPostcodePattern = Regex("^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$")
    
    return when {
        postcode.isBlank() -> "Postcode is required"
        !postcode.uppercase().matches(ukPostcodePattern) -> "Please enter a valid UK postcode"
        else -> null
    }
}

/**
 * Generic required field validation
 */
fun validateRequired(value: String, fieldName: String): String? {
    return if (value.isBlank()) "$fieldName is required" else null
}

/**
 * Validation for minimum length
 */
fun validateMinLength(value: String, minLength: Int, fieldName: String): String? {
    return if (value.length < minLength) "$fieldName must be at least $minLength characters" else null
}

/**
 * Validation for maximum length
 */
fun validateMaxLength(value: String, maxLength: Int, fieldName: String): String? {
    return if (value.length > maxLength) "$fieldName cannot exceed $maxLength characters" else null
}

/**
 * Combine multiple validators
 */
fun combineValidators(vararg validators: (String) -> String?): (String) -> String? {
    return { value ->
        validators.firstNotNullOfOrNull { it(value) }
    }
}
package com.avinashpatil.app.monzobank.utils

import android.util.Patterns
import java.math.BigDecimal
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

object SecurityUtils {
    
    // Password validation
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    )
    
    // Phone number validation (UK format)
    private val UK_PHONE_PATTERN = Pattern.compile(
        "^(\\+44\\s?7\\d{3}|\\(?07\\d{3}\\)?)\\s?\\d{3}\\s?\\d{3}$"
    )
    
    // Account number validation (8 digits)
    private val ACCOUNT_NUMBER_PATTERN = Pattern.compile("^\\d{8}$")
    
    // Sort code validation (XX-XX-XX format)
    private val SORT_CODE_PATTERN = Pattern.compile("^\\d{2}-\\d{2}-\\d{2}$")
    
    // Card number validation (basic Luhn algorithm)
    private val CARD_NUMBER_PATTERN = Pattern.compile("^\\d{13,19}$")
    
    /**
     * Validates email address format
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Validates password strength
     * Requirements: At least 8 characters, 1 digit, 1 lowercase, 1 uppercase, 1 special character
     */
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && PASSWORD_PATTERN.matcher(password).matches()
    }
    
    /**
     * Validates UK phone number format
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.isNotBlank() && UK_PHONE_PATTERN.matcher(phone).matches()
    }
    
    /**
     * Validates account number format
     */
    fun isValidAccountNumber(accountNumber: String): Boolean {
        return accountNumber.isNotBlank() && ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()
    }
    
    /**
     * Validates sort code format
     */
    fun isValidSortCode(sortCode: String): Boolean {
        return sortCode.isNotBlank() && SORT_CODE_PATTERN.matcher(sortCode).matches()
    }
    
    /**
     * Validates card number using Luhn algorithm
     */
    fun isValidCardNumber(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace("\\s", "")
        if (!CARD_NUMBER_PATTERN.matcher(cleanNumber).matches()) {
            return false
        }
        return isValidLuhn(cleanNumber)
    }
    
    /**
     * Validates monetary amount
     */
    fun isValidAmount(amount: String): Boolean {
        return try {
            val decimal = BigDecimal(amount)
            decimal > BigDecimal.ZERO && decimal.scale() <= 2
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Validates transfer amount against account balance and limits
     */
    fun isValidTransferAmount(
        amount: BigDecimal,
        accountBalance: BigDecimal,
        overdraftLimit: BigDecimal?,
        dailyLimit: BigDecimal?,
        monthlyLimit: BigDecimal?
    ): ValidationResult {
        if (amount <= BigDecimal.ZERO) {
            return ValidationResult(false, "Amount must be greater than zero")
        }
        
        val availableBalance = accountBalance + (overdraftLimit ?: BigDecimal.ZERO)
        if (amount > availableBalance) {
            return ValidationResult(false, "Insufficient funds")
        }
        
        dailyLimit?.let { limit ->
            if (amount > limit) {
                return ValidationResult(false, "Amount exceeds daily limit of £${limit.setScale(2)}")
            }
        }
        
        monthlyLimit?.let { limit ->
            if (amount > limit) {
                return ValidationResult(false, "Amount exceeds monthly limit of £${limit.setScale(2)}")
            }
        }
        
        return ValidationResult(true, "Valid amount")
    }
    
    /**
     * Sanitizes user input to prevent injection attacks
     */
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
    }
    
    /**
     * Generates a secure random PIN
     */
    fun generateSecurePin(length: Int = 4): String {
        val random = SecureRandom()
        val pin = StringBuilder()
        repeat(length) {
            pin.append(random.nextInt(10))
        }
        return pin.toString()
    }
    
    /**
     * Hashes a password using SHA-256 with salt
     */
    fun hashPassword(password: String, salt: String = generateSalt()): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest((password + salt).toByteArray())
        return Base64.encodeToString(hash, Base64.DEFAULT) + ":" + salt
    }
    
    /**
     * Verifies a password against its hash
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val parts = hashedPassword.split(":")
        if (parts.size != 2) return false
        
        val hash = parts[0]
        val salt = parts[1]
        
        return hashPassword(password, salt).startsWith(hash)
    }
    
    /**
     * Generates a random salt for password hashing
     */
    private fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT)
    }
    
    /**
     * Implements Luhn algorithm for card number validation
     */
    private fun isValidLuhn(cardNumber: String): Boolean {
        var sum = 0
        var alternate = false
        
        for (i in cardNumber.length - 1 downTo 0) {
            var n = cardNumber[i].toString().toInt()
            
            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }
            
            sum += n
            alternate = !alternate
        }
        
        return sum % 10 == 0
    }
    
    /**
     * Masks sensitive data for display
     */
    fun maskCardNumber(cardNumber: String): String {
        if (cardNumber.length < 4) return cardNumber
        return "**** **** **** ${cardNumber.takeLast(4)}"
    }
    
    fun maskAccountNumber(accountNumber: String): String {
        if (accountNumber.length < 4) return accountNumber
        return "****${accountNumber.takeLast(4)}"
    }
    
    fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 2) return email
        
        val username = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        
        val maskedPart = "*".repeat(username.length - 2)
        return "${username.take(2)}$maskedPart$domain"
    }
    
    /**
     * Validates session timeout
     */
    fun isSessionValid(lastActivity: Long, timeoutMinutes: Int = 30): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeoutMillis = timeoutMinutes * 60 * 1000L
        return (currentTime - lastActivity) < timeoutMillis
    }
    
    /**
     * Generates a secure transaction reference
     */
    fun generateTransactionReference(): String {
        val random = SecureRandom()
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..8)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    /**
     * Validates transaction reference format
     */
    fun isValidTransactionReference(reference: String): Boolean {
        return reference.matches(Regex("^[A-Z0-9]{8}$"))
    }
    
    /**
     * Rate limiting check
     */
    fun isRateLimited(
        attempts: Int,
        timeWindow: Long,
        maxAttempts: Int = 5,
        windowMinutes: Int = 15
    ): Boolean {
        val windowMillis = windowMinutes * 60 * 1000L
        val currentTime = System.currentTimeMillis()
        
        return attempts >= maxAttempts && (currentTime - timeWindow) < windowMillis
    }
    
    /**
     * Validates PIN format
     */
    fun isValidPin(pin: String): Boolean {
        return pin.matches(Regex("^\\d{4,6}$"))
    }
    
    /**
     * Checks for suspicious transaction patterns
     */
    fun isSuspiciousTransaction(
        amount: BigDecimal,
        averageTransactionAmount: BigDecimal,
        isInternational: Boolean = false,
        isUnusualTime: Boolean = false
    ): Boolean {
        // Flag transactions that are significantly higher than average
        val suspiciousAmountThreshold = averageTransactionAmount.multiply(BigDecimal("5"))
        
        return amount > suspiciousAmountThreshold || 
               (isInternational && amount > BigDecimal("1000")) ||
               (isUnusualTime && amount > BigDecimal("500"))
    }
}

/**
 * Data class for validation results
 */
data class ValidationResult(
    val isValid: Boolean,
    val message: String
)

/**
 * Input validation extensions
 */
fun String.isValidEmail() = SecurityUtils.isValidEmail(this)
fun String.isValidPassword() = SecurityUtils.isValidPassword(this)
fun String.isValidPhoneNumber() = SecurityUtils.isValidPhoneNumber(this)
fun String.isValidAccountNumber() = SecurityUtils.isValidAccountNumber(this)
fun String.isValidSortCode() = SecurityUtils.isValidSortCode(this)
fun String.isValidCardNumber() = SecurityUtils.isValidCardNumber(this)
fun String.isValidAmount() = SecurityUtils.isValidAmount(this)
fun String.sanitize() = SecurityUtils.sanitizeInput(this)
fun String.maskCard() = SecurityUtils.maskCardNumber(this)
fun String.maskAccount() = SecurityUtils.maskAccountNumber(this)
fun String.maskEmail() = SecurityUtils.maskEmail(this)
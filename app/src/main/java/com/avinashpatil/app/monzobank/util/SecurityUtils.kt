package com.avinashpatil.app.monzobank.util

import android.content.Context
import android.os.Build
import timber.log.Timber
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityUtils {
    
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_SIZE = 256
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 16
    
    /**
     * Encrypt data using AES-GCM encryption
     */
    fun encryptData(data: String, secretKey: SecretKey): EncryptionResult? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val encryptedData = cipher.doFinal(data.toByteArray())
            
            EncryptionResult(encryptedData, iv)
        } catch (e: Exception) {
            Timber.e(e, "Error encrypting data")
            null
        }
    }
    
    /**
     * Decrypt data using AES-GCM decryption
     */
    fun decryptData(encryptedData: ByteArray, iv: ByteArray, secretKey: SecretKey): String? {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_SIZE * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val decryptedData = cipher.doFinal(encryptedData)
            String(decryptedData)
        } catch (e: Exception) {
            Timber.e(e, "Error decrypting data")
            null
        }
    }
    
    /**
     * Generate a secure random key
     */
    fun generateSecretKey(): SecretKey? {
        return try {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(KEY_SIZE)
            keyGenerator.generateKey()
        } catch (e: Exception) {
            Timber.e(e, "Error generating secret key")
            null
        }
    }
    
    /**
     * Generate secure random bytes
     */
    fun generateSecureRandom(size: Int): ByteArray {
        val random = SecureRandom()
        val bytes = ByteArray(size)
        random.nextBytes(bytes)
        return bytes
    }
    
    /**
     * Hash data using SHA-256
     */
    fun hashSHA256(data: String): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(data.toByteArray())
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error hashing data")
            null
        }
    }
    
    /**
     * Validate device security
     */
    fun validateDeviceSecurity(context: Context): SecurityValidationResult {
        val issues = mutableListOf<String>()
        
        // Check if device is rooted
        if (isDeviceRooted()) {
            issues.add("Device appears to be rooted")
        }
        
        // Check if debugger is attached
        if (isDebuggerAttached()) {
            issues.add("Debugger is attached")
        }
        
        // Check if running on emulator
        if (isEmulator()) {
            issues.add("Running on emulator")
        }
        
        // Check developer options
        if (isDeveloperOptionsEnabled(context)) {
            issues.add("Developer options are enabled")
        }
        
        return SecurityValidationResult(
            isSecure = issues.isEmpty(),
            issues = issues
        )
    }
    
    /**
     * Check if device is rooted
     */
    private fun isDeviceRooted(): Boolean {
        return try {
            // Check for common root files
            val rootFiles = arrayOf(
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"
            )
            
            rootFiles.any { java.io.File(it).exists() }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if debugger is attached
     */
    private fun isDebuggerAttached(): Boolean {
        return android.os.Debug.isDebuggerConnected() || android.os.Debug.waitingForDebugger()
    }
    
    /**
     * Check if running on emulator
     */
    private fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                "google_sdk" == Build.PRODUCT)
    }
    
    /**
     * Check if developer options are enabled
     */
    private fun isDeveloperOptionsEnabled(context: Context): Boolean {
        return try {
            android.provider.Settings.Secure.getInt(
                context.contentResolver,
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Generate a secure token
     */
    fun generateSecureToken(length: Int = 32): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = SecureRandom()
        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    /**
     * Check if device is secure
     */
    fun isDeviceSecure(context: Context): Boolean {
        val result = validateDeviceSecurity(context)
        return result.isSecure
    }
    
    /**
     * Check if device is compromised
     */
    fun isDeviceCompromised(): Boolean {
        return isDeviceRooted()
    }
    
    /**
     * Check if debugging is detected
     */
    fun isDebuggingDetected(): Boolean {
        return isDebuggerAttached()
    }
    
    /**
     * Check if app is tampered
     */
    fun isAppTampered(): Boolean {
        // Basic tampering detection - can be enhanced
        return isEmulator() || isDebuggerAttached()
    }
    
    /**
     * Check if deep link is safe
     */
    fun isDeepLinkSafe(uri: String?): Boolean {
        if (uri.isNullOrBlank()) return false
        
        return try {
            // Basic validation - check for suspicious patterns
            val lowerUri = uri.lowercase()
            val suspiciousPatterns = listOf(
                "javascript:",
                "data:",
                "file:",
                "<script",
                "eval(",
                "alert("
            )
            
            !suspiciousPatterns.any { lowerUri.contains(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error validating deep link")
            false
        }
    }
    
    /**
     * Check if payment request is valid
     */
    fun isPaymentRequestValid(paymentData: String?): Boolean {
        if (paymentData.isNullOrBlank()) return false
        
        return try {
            // Basic validation for payment request
            val lowerData = paymentData.lowercase()
            val suspiciousPatterns = listOf(
                "<script",
                "javascript:",
                "eval(",
                "alert(",
                "document.",
                "window."
            )
            
            !suspiciousPatterns.any { lowerData.contains(it) }
        } catch (e: Exception) {
            Timber.e(e, "Error validating payment request")
            false
        }
    }
    
    data class EncryptionResult(
        val encryptedData: ByteArray,
        val iv: ByteArray
    )
    
    data class SecurityValidationResult(
        val isSecure: Boolean,
        val issues: List<String>
    )
}
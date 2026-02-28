package com.avinashpatil.app.monzobank.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

object BiometricUtils {
    
    /**
     * Check if biometric authentication is available on the device
     */
    fun isBiometricAvailable(context: Context): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                BiometricAvailability.AVAILABLE
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                BiometricAvailability.NO_HARDWARE
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                BiometricAvailability.HARDWARE_UNAVAILABLE
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                BiometricAvailability.NONE_ENROLLED
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                BiometricAvailability.SECURITY_UPDATE_REQUIRED
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                BiometricAvailability.UNSUPPORTED
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                BiometricAvailability.UNKNOWN
            }
            else -> BiometricAvailability.UNKNOWN
        }
    }
    
    /**
     * Check if device has biometric hardware
     */
    fun hasBiometricHardware(context: Context): Boolean {
        val availability = isBiometricAvailable(context)
        return availability != BiometricAvailability.NO_HARDWARE
    }
    
    /**
     * Check if biometric is enrolled
     */
    fun isBiometricEnrolled(context: Context): Boolean {
        val availability = isBiometricAvailable(context)
        return availability == BiometricAvailability.AVAILABLE
    }
    
    /**
     * Check biometric availability - returns boolean for simple checks
     */
    fun checkBiometricAvailability(context: Context): Boolean {
        val availability = isBiometricAvailable(context)
        return availability == BiometricAvailability.AVAILABLE
    }
    
    /**
     * Create biometric prompt
     */
    fun createBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onError: (Int, CharSequence) -> Unit,
        onFailed: () -> Unit
    ): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)
        
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Timber.d("Biometric authentication succeeded")
                onSuccess(result)
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Timber.e("Biometric authentication error: $errorCode - $errString")
                onError(errorCode, errString)
            }
            
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Timber.w("Biometric authentication failed")
                onFailed()
            }
        }
        
        return BiometricPrompt(activity, executor, callback)
    }
    
    /**
     * Create biometric prompt info
     */
    fun createPromptInfo(
        title: String = "Biometric Authentication",
        subtitle: String = "Use your biometric credential to authenticate",
        description: String = "Place your finger on the sensor or look at the camera",
        negativeButtonText: String = "Use PIN",
        allowDeviceCredential: Boolean = false
    ): BiometricPrompt.PromptInfo {
        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
        
        if (allowDeviceCredential) {
            builder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            builder.setNegativeButtonText(negativeButtonText)
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        }
        
        return builder.build()
    }
    
    /**
     * Get supported biometric types
     */
    fun getSupportedBiometricTypes(context: Context): List<BiometricType> {
        val supportedTypes = mutableListOf<BiometricType>()
        val biometricManager = BiometricManager.from(context)
        
        // Check for fingerprint
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            supportedTypes.add(BiometricType.FINGERPRINT)
        }
        
        // Note: BiometricManager doesn't distinguish between face and fingerprint
        // This would require additional platform-specific checks
        
        return supportedTypes
    }
    
    /**
     * Get biometric capability description
     */
    fun getBiometricCapabilityDescription(context: Context): String {
        return when (isBiometricAvailable(context)) {
            BiometricAvailability.AVAILABLE -> "Biometric authentication is available and ready to use"
            BiometricAvailability.NO_HARDWARE -> "This device doesn't have biometric hardware"
            BiometricAvailability.HARDWARE_UNAVAILABLE -> "Biometric hardware is currently unavailable"
            BiometricAvailability.NONE_ENROLLED -> "No biometric credentials are enrolled. Please set up biometric authentication in device settings"
            BiometricAvailability.SECURITY_UPDATE_REQUIRED -> "A security update is required for biometric authentication"
            BiometricAvailability.UNSUPPORTED -> "Biometric authentication is not supported on this device"
            BiometricAvailability.UNKNOWN -> "Biometric authentication status is unknown"
        }
    }
    
    /**
     * Convert error code to user-friendly message
     */
    fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_CANCELED -> "Authentication was cancelled"
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> "No biometric hardware present"
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> "Biometric hardware unavailable"
            BiometricPrompt.ERROR_LOCKOUT -> "Too many failed attempts. Try again later"
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "Biometric authentication is permanently locked"
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> "Authentication cancelled by user"
            BiometricPrompt.ERROR_NO_BIOMETRICS -> "No biometric credentials enrolled"
            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> "No device credential set"
            BiometricPrompt.ERROR_NO_SPACE -> "Not enough storage space"
            BiometricPrompt.ERROR_TIMEOUT -> "Authentication timed out"
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> "Unable to process biometric"
            BiometricPrompt.ERROR_USER_CANCELED -> "Authentication cancelled by user"
            BiometricPrompt.ERROR_VENDOR -> "Vendor-specific error occurred"
            else -> "Unknown biometric error occurred"
        }
    }
    
    enum class BiometricAvailability {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NONE_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        UNSUPPORTED,
        UNKNOWN
    }
    
    enum class BiometricType {
        FINGERPRINT,
        FACE,
        IRIS,
        VOICE
    }
}
package com.avinashpatil.app.monzobank.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.avinashpatil.app.monzobank.domain.model.BiometricAuthResult
import com.avinashpatil.app.monzobank.domain.model.BiometricType
// import dagger.hilt.android.qualifiers.ApplicationContext
// import javax.inject.Inject
// import javax.inject.Singleton

/**
 * Helper class for biometric authentication operations
 */
// @Singleton
class BiometricHelper /* @Inject constructor(
    @ApplicationContext private val context: Context
) */ {
    
    private lateinit var context: Context
    
    constructor(context: Context) {
        this.context = context
    }
    
    /**
     * Check if biometric authentication is available on the device
     */
    fun isBiometricAvailable(): BiometricAvailability {
        val biometricManager = BiometricManager.from(context)
        
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAvailability.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricAvailability.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricAvailability.HARDWARE_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricAvailability.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricAvailability.SECURITY_UPDATE_REQUIRED
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> BiometricAvailability.UNSUPPORTED
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> BiometricAvailability.UNKNOWN
            else -> BiometricAvailability.UNKNOWN
        }
    }
    
    /**
     * Create biometric prompt for authentication
     */
    fun createBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: (BiometricAuthResult) -> Unit,
        onError: (BiometricAuthResult) -> Unit
    ): BiometricPrompt {
        
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(
                        BiometricAuthResult(
                            isSuccessful = false,
                            biometricType = BiometricType.FINGERPRINT,
                            errorMessage = "Authentication error: $errString"
                        )
                    )
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    
                    // Generate a secure token for the successful authentication
                    val token = generateBiometricToken()
                    
                    onSuccess(
                        BiometricAuthResult(
                            isSuccessful = true,
                            biometricType = BiometricType.FINGERPRINT
                        )
                    )
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError(
                        BiometricAuthResult(
                            isSuccessful = false,
                            biometricType = BiometricType.FINGERPRINT,
                            errorMessage = "Authentication failed. Please try again."
                        )
                    )
                }
            }
        )
        
        return biometricPrompt
    }
    
    /**
     * Create biometric prompt info for login
     */
    fun createLoginPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Login")
            .setSubtitle("Use your fingerprint or face to log in to Monzo Bank")
            .setDescription("Place your finger on the sensor or look at the camera to authenticate")
            .setNegativeButtonText("Use Password")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }
    
    /**
     * Create biometric prompt info for setup
     */
    fun createSetupPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Set up Biometric Authentication")
            .setSubtitle("Enable biometric login for faster access")
            .setDescription("Your biometric data will be stored securely on your device")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }
    
    /**
     * Create biometric prompt info for transaction confirmation
     */
    fun createTransactionPromptInfo(amount: String): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("Confirm Transaction")
            .setSubtitle("Authenticate to confirm payment of $amount")
            .setDescription("Use your biometric to authorize this transaction")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }
    
    /**
     * Generate a secure biometric token
     */
    private fun generateBiometricToken(): String {
        // In a real implementation, this would generate a cryptographically secure token
        // For now, we'll use a simple timestamp-based token
        val timestamp = System.currentTimeMillis()
        val randomComponent = (1000..9999).random()
        return "bio_${timestamp}_${randomComponent}"
    }
    
    /**
     * Get user-friendly message for biometric availability status
     */
    fun getBiometricAvailabilityMessage(availability: BiometricAvailability): String {
        return when (availability) {
            BiometricAvailability.AVAILABLE -> "Biometric authentication is available"
            BiometricAvailability.NO_HARDWARE -> "No biometric hardware available on this device"
            BiometricAvailability.HARDWARE_UNAVAILABLE -> "Biometric hardware is currently unavailable"
            BiometricAvailability.NONE_ENROLLED -> "No biometric credentials are enrolled. Please set up fingerprint or face unlock in your device settings"
            BiometricAvailability.SECURITY_UPDATE_REQUIRED -> "A security update is required to use biometric authentication"
            BiometricAvailability.UNSUPPORTED -> "Biometric authentication is not supported on this device"
            BiometricAvailability.UNKNOWN -> "Biometric authentication status is unknown"
        }
    }
    
    /**
     * Check if biometric authentication should be offered to the user
     */
    fun shouldOfferBiometric(): Boolean {
        return isBiometricAvailable() == BiometricAvailability.AVAILABLE
    }
}

/**
 * Enum representing biometric availability states
 */
enum class BiometricAvailability {
    AVAILABLE,
    NO_HARDWARE,
    HARDWARE_UNAVAILABLE,
    NONE_ENROLLED,
    SECURITY_UPDATE_REQUIRED,
    UNSUPPORTED,
    UNKNOWN
}

/**
 * Data class for biometric authentication configuration
 */
data class BiometricConfig(
    val title: String,
    val subtitle: String,
    val description: String,
    val negativeButtonText: String,
    val allowedAuthenticators: Int = BiometricManager.Authenticators.BIOMETRIC_STRONG
)
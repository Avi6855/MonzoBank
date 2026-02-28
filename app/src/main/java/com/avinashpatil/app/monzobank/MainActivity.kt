package com.avinashpatil.app.monzobank

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.avinashpatil.app.monzobank.BuildConfig
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.avinashpatil.app.monzobank.presentation.navigation.MonzoBankNavigation
import com.avinashpatil.app.monzobank.presentation.theme.MonzoBankTheme
import com.avinashpatil.app.monzobank.presentation.viewmodel.AuthViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.SecurityViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.ThemeViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.BiometricViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.SessionViewModel
import com.avinashpatil.app.monzobank.presentation.screens.splash.SplashScreen
import com.avinashpatil.app.monzobank.presentation.screens.auth.BiometricPromptScreen
import com.avinashpatil.app.monzobank.presentation.screens.main.MainScreen
import com.avinashpatil.app.monzobank.presentation.screens.security.SecurityLockScreen
import com.avinashpatil.app.monzobank.presentation.state.AuthState
import com.avinashpatil.app.monzobank.domain.model.SecurityState
import com.avinashpatil.app.monzobank.domain.model.BiometricState
import com.avinashpatil.app.monzobank.domain.model.SessionState
import com.avinashpatil.app.monzobank.util.SecurityUtils
import com.avinashpatil.app.monzobank.util.BiometricUtils
// import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

// @AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by viewModels()
    private val securityViewModel: SecurityViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private val biometricViewModel: BiometricViewModel by viewModels()
    private val sessionViewModel: SessionViewModel by viewModels()
    
    private var isAppInForeground = true
    private var backgroundTime: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configure window for security
        configureWindowSecurity()
        
        // Initialize security checks
        initializeSecurityChecks()
        
        // Set up splash screen
        setupSplashScreen(splashScreen)
        
        // Set content
        setContent {
            MonzoBankApp()
        }
        
        // Handle deep links
        handleIntent(intent)
        
        Timber.d("MainActivity onCreate")
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }
    
    override fun onResume() {
        super.onResume()
        isAppInForeground = true
        
        // Check if app was in background for too long
        if (backgroundTime > 0) {
            val backgroundDuration = System.currentTimeMillis() - backgroundTime
            val sessionTimeout = MonzoBankApplication.SESSION_TIMEOUT_MINUTES * 60 * 1000
            
            if (backgroundDuration > sessionTimeout) {
                // Session expired, require re-authentication
                sessionViewModel.expireSession()
                authViewModel.logout()
            } else if (backgroundDuration > 30000) { // 30 seconds
                // Require biometric authentication
                biometricViewModel.requireAuthentication()
            }
        }
        
        backgroundTime = 0
        
        // Resume security monitoring
        securityViewModel.resumeMonitoring()
        
        Timber.d("MainActivity resumed")
    }
    
    override fun onPause() {
        super.onPause()
        isAppInForeground = false
        backgroundTime = System.currentTimeMillis()
        
        // Pause security monitoring
        securityViewModel.pauseMonitoring()
        
        Timber.d("MainActivity paused")
    }
    
    override fun onStop() {
        super.onStop()
        
        // Clear sensitive data from memory if needed
        if (!isAppInForeground) {
            securityViewModel.clearSensitiveData()
        }
        
        Timber.d("MainActivity stopped")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up resources
        securityViewModel.cleanup()
        sessionViewModel.cleanup()
        
        Timber.d("MainActivity destroyed")
    }
    
    @Composable
    private fun MonzoBankApp() {
        val themeState by themeViewModel.themeState.collectAsStateWithLifecycle()
        val authState by authViewModel.authState.collectAsStateWithLifecycle()
        val securityState by securityViewModel.securityState.collectAsStateWithLifecycle()
        val biometricState by biometricViewModel.biometricState.collectAsStateWithLifecycle()
        val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()
        
        var showSplash by remember { mutableStateOf(true) }
        
        // Handle splash screen timing
        LaunchedEffect(authState) {
            delay(2000) // Minimum splash duration
            showSplash = false
        }
        
        MonzoBankTheme(
            darkTheme = themeState.isDarkMode,
            dynamicColor = themeState.useDynamicColors
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                when {
                    showSplash -> {
                        SplashScreen(
                            onSplashComplete = { showSplash = false }
                        )
                    }
                    
                    securityState.isDeviceCompromised -> {
                        SecurityLockScreen(
                            securityState = securityState,
                            onSecurityResolved = {
                                securityViewModel.resolveSecurityIssue()
                            },
                            onExitApp = {
                                finishAffinity()
                            }
                        )
                    }
                    
                    biometricState.isRequired && biometricState.isAvailable -> {
                        BiometricPromptScreen(
                            biometricState = biometricState,
                            onBiometricSuccess = {
                                biometricViewModel.onAuthenticationSuccess()
                            },
                            onBiometricError = { error ->
                                biometricViewModel.onAuthenticationError(error)
                            },
                            onBiometricFailed = {
                                biometricViewModel.onAuthenticationFailed()
                            },
                            onFallbackToPin = {
                                biometricViewModel.fallbackToPin()
                            }
                        )
                    }
                    
                    sessionState.isExpired -> {
                        SecurityLockScreen(
                            securityState = SecurityState(
                                isLocked = true,
                                lockReason = "Session expired. Please authenticate again."
                            ),
                            onSecurityResolved = {
                            // Get current user before ending session
                            val currentUser = sessionViewModel.sessionState.value.userId
                            if (currentUser != null) {
                                sessionViewModel.endSession()
                            } else {
                                // If no active session, just clear the expired state
                                sessionViewModel.clearExpiredSession()
                            }
                            authViewModel.logout()
                            authViewModel.clearSuccessStates()
                        },
                            onExitApp = {
                                finishAffinity()
                            }
                        )
                    }
                    
                    else -> {
                        val navController = rememberNavController()
                        
                        MonzoBankNavigation(
                            navController = navController,
                            authState = authState,
                            startDestination = when {
                                authState.isAuthenticated -> "dashboard"
                                authState.isOnboardingComplete -> "login"
                                else -> "onboarding"
                            },
                            authViewModel = authViewModel
                        )
                    }
                }
            }
        }
        
        // Handle security events
        LaunchedEffect(securityState) {
            when {
                securityState.isFraudDetected -> {
                    // Handle fraud detection
                    securityViewModel.handleFraudDetection()
                }
                
                securityState.isUnauthorizedAccess -> {
                    // Handle unauthorized access
                    securityViewModel.handleUnauthorizedAccess()
                    authViewModel.logout()
                }
                
                securityState.isSuspiciousActivity -> {
                    // Handle suspicious activity
                    securityViewModel.handleSuspiciousActivity()
                }
                
                else -> {
                    // No security events to handle
                }
            }
        }
        
        // Handle session events
        LaunchedEffect(sessionState) {
            when {
                sessionState.isExpiring -> {
                    // Warn user about session expiration
                    sessionViewModel.showExpirationWarning()
                }
                
                sessionState.isExpired -> {
                    // Force logout
                    authViewModel.logout()
                }
                
                else -> {
                    // No session events to handle
                }
            }
        }
        
        // Handle authentication state changes
        LaunchedEffect(authState.isAuthenticated) {
            if (authState.isAuthenticated) {
                // Get current user ID from auth UI state
                val currentUserId = authViewModel.uiState.value.currentUserId ?: "user_${System.currentTimeMillis()}"
                // Start session when user is authenticated
                sessionViewModel.startSession(
                    userId = currentUserId,
                    sessionType = com.avinashpatil.app.monzobank.domain.model.SessionState.SessionType.AUTHENTICATED
                )
            } else if (!authState.isAuthenticated) {
                // End session when user is logged out
                sessionViewModel.endSession()
            }
        }
    }
    
    private fun configureWindowSecurity() {
        // Prevent screenshots and screen recording in production
        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        // Configure window for edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Prevent app from appearing in recent apps with sensitive content
        if (!BuildConfig.DEBUG) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }
    
    private fun initializeSecurityChecks() {
        lifecycleScope.launch {
            try {
                // Check device security
                val isDeviceSecure = SecurityUtils.isDeviceSecure(this@MainActivity)
                if (!isDeviceSecure) {
                    securityViewModel.reportSecurityIssue("Device is not secure")
                }
                
                // Check for root/jailbreak
                val isDeviceCompromised = SecurityUtils.isDeviceCompromised()
                if (isDeviceCompromised) {
                    securityViewModel.reportDeviceCompromised()
                }
                
                // Check for debugging
                val isDebuggingDetected = SecurityUtils.isDebuggingDetected()
                if (isDebuggingDetected && !BuildConfig.DEBUG) {
                    securityViewModel.reportDebuggingDetected()
                }
                
                // Check app integrity
                val isAppTampered = SecurityUtils.isAppTampered()
                if (isAppTampered) {
                    securityViewModel.reportAppTampered()
                }
                
                // Initialize biometric availability
                val biometricAvailability = BiometricUtils.checkBiometricAvailability(this@MainActivity)
                biometricViewModel.updateAvailability(biometricAvailability)
                
                Timber.d("Security checks completed")
            } catch (e: Exception) {
                Timber.e(e, "Error during security initialization")
                securityViewModel.reportSecurityError(e.message ?: "Unknown security error")
            }
        }
    }
    
    private fun setupSplashScreen(splashScreen: androidx.core.splashscreen.SplashScreen) {
        splashScreen.setKeepOnScreenCondition {
            // Keep splash screen while initializing
            authViewModel.authState.value.isLoading
        }
        
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Custom exit animation if needed
            splashScreenView.remove()
        }
    }
    
    private fun handleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                Intent.ACTION_VIEW -> {
                    // Handle deep links
                    val data = it.data
                    data?.let { uri ->
                        handleDeepLink(uri.toString())
                    }
                }
                
                "com.avinashpatil.app.monzobank.PAYMENT_REQUEST" -> {
                    // Handle payment request from external app
                    val paymentData = it.getStringExtra("payment_data")
                    paymentData?.let { data ->
                        handlePaymentRequest(data)
                    }
                }
                
                "com.avinashpatil.app.monzobank.SECURITY_ALERT" -> {
                    // Handle security alert
                    val alertType = it.getStringExtra("alert_type")
                    alertType?.let { type ->
                        handleSecurityAlert(type)
                    }
                }
                
                else -> {
                    // Handle unknown intent actions
                    Timber.d("Unknown intent action: ${it.action}")
                }
            }
        }
    }
    
    private fun handleDeepLink(deepLink: String) {
        lifecycleScope.launch {
            try {
                // Parse and handle deep link
                Timber.d("Handling deep link: $deepLink")
                
                // Validate deep link security
                if (SecurityUtils.isDeepLinkSafe(deepLink)) {
                    // Process the deep link
                    // This would be handled by the navigation system
                } else {
                    Timber.w("Unsafe deep link detected: $deepLink")
                    securityViewModel.reportSuspiciousActivity("Unsafe deep link: $deepLink")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling deep link: $deepLink")
            }
        }
    }
    
    private fun handlePaymentRequest(paymentData: String) {
        lifecycleScope.launch {
            try {
                // Handle external payment request
                Timber.d("Handling payment request: $paymentData")
                
                // Validate payment request
                if (SecurityUtils.isPaymentRequestValid(paymentData)) {
                    // Process payment request
                    // This would be handled by the payment system
                } else {
                    Timber.w("Invalid payment request: $paymentData")
                    securityViewModel.reportSuspiciousActivity("Invalid payment request")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling payment request: $paymentData")
            }
        }
    }
    
    private fun handleSecurityAlert(alertType: String) {
        lifecycleScope.launch {
            try {
                // Handle security alert
                Timber.d("Handling security alert: $alertType")
                
                when (alertType) {
                    "fraud_detected" -> {
                        securityViewModel.handleFraudAlert()
                    }
                    
                    "suspicious_login" -> {
                        securityViewModel.handleSuspiciousLogin()
                    }
                    
                    "device_compromised" -> {
                        securityViewModel.handleDeviceCompromised()
                    }
                    
                    else -> {
                        Timber.w("Unknown security alert type: $alertType")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error handling security alert: $alertType")
            }
        }
    }
}
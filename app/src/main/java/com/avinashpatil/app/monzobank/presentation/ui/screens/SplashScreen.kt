package com.avinashpatil.app.monzobank.presentation.ui.screens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.avinashpatil.app.monzobank.presentation.theme.MonzoBankTheme
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import com.avinashpatil.app.monzobank.utils.Constants
import kotlinx.coroutines.delay

/**
 * Splash screen with animated Monzo logo, gradient background, and biometric authentication
 * Displays animations, then prompts for biometric authentication before navigating
 */
@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }
    
    var showBiometricPrompt by remember { mutableStateOf(false) }
    var animationCompleted by remember { mutableStateOf(false) }
    var biometricAvailable by remember { mutableStateOf(false) }
    
    // Check biometric availability
    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        biometricAvailable = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    // Animation effect
    LaunchedEffect(key1 = true) {
        // Scale animation
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                delayMillis = 100
            )
        )
        
        // Alpha animation
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = 400
            )
        )
        
        // Wait for splash duration
        delay(Constants.SPLASH_DELAY)
        
        animationCompleted = true
        
        // Show biometric prompt if available, otherwise navigate
        if (biometricAvailable) {
            showBiometricPrompt = true
            // Start pulsing animation for biometric icon
            pulseScale.animateTo(
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            // Navigate to login if no biometric available
            onNavigateToLogin()
        }
    }
    
    // Biometric authentication
    if (showBiometricPrompt && context is FragmentActivity) {
        val biometricPrompt = remember {
            BiometricPrompt(context as FragmentActivity, ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        // Navigate to login on error
                        onNavigateToLogin()
                    }
                    
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        // Navigate to dashboard on success
                        onNavigateToDashboard()
                    }
                    
                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Stay on splash screen, user can try again
                    }
                })
        }
        
        val promptInfo = remember {
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("Monzo Bank Authentication")
                .setSubtitle("Use your fingerprint or face to access your account")
                .setNegativeButtonText("Use PIN instead")
                .build()
        }
        
        LaunchedEffect(showBiometricPrompt) {
            if (showBiometricPrompt) {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MonzoCoralPrimary,
                        MonzoCoralSecondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Monzo Logo (placeholder - using text for now)
            Text(
                text = "M",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(24.dp)
            )
            
            // App Name
            Text(
                text = "Monzo Bank",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            
            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(8.dp)
            )
            
            // Tagline
            Text(
                text = "Banking made simple",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            // Show biometric prompt UI when animation is completed and biometric is available
            if (animationCompleted && biometricAvailable && showBiometricPrompt) {
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(48.dp)
                )
                
                // Biometric Icon with pulsing animation
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Authentication",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(64.dp)
                        .scale(pulseScale.value)
                        .alpha(0.9f)
                )
                
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(16.dp)
                )
                
                Text(
                    text = "Touch to authenticate",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
                
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.size(24.dp)
                )
                
                // Alternative login button
                Button(
                    onClick = { onNavigateToLogin() },
                    modifier = Modifier.alpha(0.8f)
                ) {
                    Text(
                        text = "Use PIN instead",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    MonzoBankTheme {
        SplashScreen(
            onNavigateToOnboarding = {},
            onNavigateToDashboard = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenWithBiometricPreview() {
    MonzoBankTheme {
        SplashScreen(
            onNavigateToOnboarding = {},
            onNavigateToDashboard = {},
            onNavigateToLogin = {}
        )
    }
}
package com.avinashpatil.app.monzobank.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.monzobank.domain.model.BiometricState

@Composable
fun BiometricPromptScreen(
    biometricState: BiometricState,
    onBiometricSuccess: () -> Unit,
    onBiometricError: (String) -> Unit,
    onBiometricFailed: () -> Unit,
    onFallbackToPin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Biometric Icon
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometric Authentication",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = "Biometric Authentication",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = when (biometricState.authenticationStatus) {
                        BiometricState.AuthenticationStatus.PENDING -> 
                            "Use your fingerprint or face to authenticate"
                        BiometricState.AuthenticationStatus.FAILED -> 
                            "Authentication failed. Please try again."
                        BiometricState.AuthenticationStatus.ERROR -> 
                            biometricState.errorMessage ?: "An error occurred during authentication"
                        else -> "Please authenticate to continue"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Status indicator
                when (biometricState.authenticationStatus) {
                    BiometricState.AuthenticationStatus.PENDING -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    BiometricState.AuthenticationStatus.FAILED -> {
                        Text(
                            text = "Failed attempts: ${biometricState.failedAttempts}/${biometricState.maxFailedAttempts}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    BiometricState.AuthenticationStatus.ERROR -> {
                        Text(
                            text = "Error",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    else -> {
                        // Empty space
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel/Use PIN button
                    OutlinedButton(
                        onClick = onFallbackToPin,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Use PIN")
                    }
                    
                    // Retry button (only show if failed)
                    if (biometricState.authenticationStatus == BiometricState.AuthenticationStatus.FAILED ||
                        biometricState.authenticationStatus == BiometricState.AuthenticationStatus.ERROR) {
                        Button(
                            onClick = {
                                // Trigger biometric prompt again
                                // This would typically be handled by the parent composable
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                
                // Show lockout message if applicable
                if (biometricState.isLockedOut) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Too many failed attempts. Biometric authentication is temporarily locked.",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
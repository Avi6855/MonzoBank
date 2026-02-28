package com.avinashpatil.app.monzobank.presentation.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.monzobank.domain.model.SecurityState

@Composable
fun SecurityLockScreen(
    securityState: SecurityState,
    onSecurityResolved: () -> Unit,
    onExitApp: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
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
                // Security Icon
                Icon(
                    imageVector = if (securityState.isDeviceCompromised) Icons.Default.Warning else Icons.Default.Security,
                    contentDescription = "Security Alert",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title
                Text(
                    text = when {
                        securityState.isDeviceCompromised -> "Device Security Compromised"
                        securityState.isLocked -> "Security Lock"
                        else -> "Security Alert"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Description
                Text(
                    text = securityState.lockReason ?: when {
                        securityState.isDeviceCompromised -> 
                            "Your device appears to be compromised. For your security, the app has been locked."
                        securityState.isLocked -> 
                            "The app has been locked for security reasons."
                        else -> 
                            "A security issue has been detected."
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Security details if available
                if (securityState.securityAlerts.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Security Issues Detected:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            securityState.securityAlerts.take(3).forEach { alert ->
                                Text(
                                    text = "• ${alert.message}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Exit App button
                    OutlinedButton(
                        onClick = onExitApp,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Exit App")
                    }
                    
                    // Retry/Continue button (only if not device compromised)
                    if (!securityState.isDeviceCompromised) {
                        Button(
                            onClick = onSecurityResolved,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Continue")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Security tips
                Text(
                    text = when {
                        securityState.isDeviceCompromised -> 
                            "Please ensure your device is secure and not rooted/jailbroken."
                        else -> 
                            "Your security is our priority. Please contact support if this issue persists."
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
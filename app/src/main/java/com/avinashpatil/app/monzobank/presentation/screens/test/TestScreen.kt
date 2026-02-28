package com.avinashpatil.app.monzobank.presentation.screens.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.presentation.viewmodel.SessionViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.AuthViewModel

@Composable
fun TestScreen(
    sessionViewModel: SessionViewModel,
    authViewModel: AuthViewModel
) {
    val sessionState by sessionViewModel.sessionState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Test Screen",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Session State",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Active: ${sessionState.isActive}")
                Text("Expired: ${sessionState.isExpired}")
                Text("User ID: ${sessionState.userId}")
                Text("Remaining Time: ${sessionState.remainingTime / 1000}s")
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Auth State",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Authenticated: ${authState.isAuthenticated}")
                Text("Loading: ${authState.isLoading}")
                Text("Onboarding Complete: ${authState.isOnboardingComplete}")
            }
        }
        
        Button(
            onClick = {
                sessionViewModel.simulateSessionExpiry()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Simulate Session Expiry")
        }
        
        Button(
            onClick = {
                authViewModel.logout()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
        
        Button(
            onClick = {
                sessionViewModel.startSession(
                    userId = "test_user",
                    sessionType = com.avinashpatil.app.monzobank.domain.model.SessionState.SessionType.AUTHENTICATED
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start New Session")
        }
    }
}
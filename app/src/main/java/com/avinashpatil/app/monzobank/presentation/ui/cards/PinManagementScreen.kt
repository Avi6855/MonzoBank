package com.avinashpatil.app.monzobank.presentation.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinManagementScreen(
    cardId: String,
    onBackClick: () -> Unit,
    onPinChanged: () -> Unit
) {
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var showCurrentPin by remember { mutableStateOf(false) }
    var showNewPin by remember { mutableStateOf(false) }
    var showConfirmPin by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Change PIN") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Security Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your PIN is used to authorize transactions and ATM withdrawals.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            OutlinedTextField(
                value = currentPin,
                onValueChange = { 
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        currentPin = it
                        errorMessage = null
                    }
                },
                label = { Text("Current PIN") },
                visualTransformation = if (showCurrentPin) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(
                        onClick = { showCurrentPin = !showCurrentPin }
                    ) {
                        Icon(
                            imageVector = if (showCurrentPin) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showCurrentPin) "Hide PIN" else "Show PIN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )
            
            OutlinedTextField(
                value = newPin,
                onValueChange = { 
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        newPin = it
                        errorMessage = null
                    }
                },
                label = { Text("New PIN") },
                visualTransformation = if (showNewPin) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(
                        onClick = { showNewPin = !showNewPin }
                    ) {
                        Icon(
                            imageVector = if (showNewPin) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showNewPin) "Hide PIN" else "Show PIN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )
            
            OutlinedTextField(
                value = confirmPin,
                onValueChange = { 
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        confirmPin = it
                        errorMessage = null
                    }
                },
                label = { Text("Confirm New PIN") },
                visualTransformation = if (showConfirmPin) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(
                        onClick = { showConfirmPin = !showConfirmPin }
                    ) {
                        Icon(
                            imageVector = if (showConfirmPin) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (showConfirmPin) "Hide PIN" else "Show PIN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage != null
            )
            
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    when {
                        currentPin.length != 4 -> {
                            errorMessage = "Please enter your current 4-digit PIN"
                        }
                        newPin.length != 4 -> {
                            errorMessage = "New PIN must be 4 digits"
                        }
                        confirmPin != newPin -> {
                            errorMessage = "PINs do not match"
                        }
                        newPin == currentPin -> {
                            errorMessage = "New PIN must be different from current PIN"
                        }
                        else -> {
                            isLoading = true
                            onPinChanged()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && currentPin.isNotEmpty() && newPin.isNotEmpty() && confirmPin.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Change PIN")
                }
            }
        }
    }
}
package com.avinashpatil.app.monzobank.presentation.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardControlsScreen(
    cardId: String,
    onBackClick: () -> Unit,
    onPinManagementClick: () -> Unit
) {
    var cardLocked by remember { mutableStateOf(false) }
    var contactlessEnabled by remember { mutableStateOf(true) }
    var onlinePaymentsEnabled by remember { mutableStateOf(true) }
    var atmWithdrawalsEnabled by remember { mutableStateOf(true) }
    var magneticStripeEnabled by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Card Controls") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                CardStatusCard(
                    cardLocked = cardLocked,
                    onLockToggle = { cardLocked = !cardLocked }
                )
            }
            
            item {
                Text(
                    text = "Payment Controls",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(getPaymentControls(
                contactlessEnabled,
                onlinePaymentsEnabled,
                atmWithdrawalsEnabled,
                magneticStripeEnabled
            )) { control ->
                PaymentControlCard(
                    control = control,
                    onToggle = { enabled ->
                        when (control.type) {
                            "contactless" -> contactlessEnabled = enabled
                            "online" -> onlinePaymentsEnabled = enabled
                            "atm" -> atmWithdrawalsEnabled = enabled
                            "magnetic" -> magneticStripeEnabled = enabled
                        }
                    }
                )
            }
            
            item {
                Text(
                    text = "Security Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                SecuritySettingsCard(
                    onPinManagementClick = onPinManagementClick
                )
            }
            
            item {
                SpendingLimitsCard()
            }
        }
    }
}

@Composable
fun CardStatusCard(
    cardLocked: Boolean,
    onLockToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (cardLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (cardLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (cardLocked) "Card Locked" else "Card Active",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (cardLocked) "All transactions are blocked" else "Card is ready to use",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = !cardLocked,
                onCheckedChange = { onLockToggle() }
            )
        }
    }
}

@Composable
fun PaymentControlCard(
    control: PaymentControl,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = control.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = control.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = control.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = control.enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
fun SecuritySettingsCard(
    onPinManagementClick: () -> Unit
) {
    Card(
        onClick = onPinManagementClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "PIN Management",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Change or reset your card PIN",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SpendingLimitsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Spending Limits",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Daily Limit",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "£500",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ATM Limit",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "£300",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class PaymentControl(
    val type: String,
    val title: String,
    val description: String,
    val emoji: String,
    val enabled: Boolean
)

fun getPaymentControls(
    contactlessEnabled: Boolean,
    onlinePaymentsEnabled: Boolean,
    atmWithdrawalsEnabled: Boolean,
    magneticStripeEnabled: Boolean
): List<PaymentControl> {
    return listOf(
        PaymentControl(
            type = "contactless",
            title = "Contactless Payments",
            description = "Tap to pay transactions",
            emoji = "📱",
            enabled = contactlessEnabled
        ),
        PaymentControl(
            type = "online",
            title = "Online Payments",
            description = "Internet and app purchases",
            emoji = "🌐",
            enabled = onlinePaymentsEnabled
        ),
        PaymentControl(
            type = "atm",
            title = "ATM Withdrawals",
            description = "Cash withdrawals from ATMs",
            emoji = "🏧",
            enabled = atmWithdrawalsEnabled
        ),
        PaymentControl(
            type = "magnetic",
            title = "Magnetic Stripe",
            description = "Swipe transactions (less secure)",
            emoji = "💳",
            enabled = magneticStripeEnabled
        )
    )
}
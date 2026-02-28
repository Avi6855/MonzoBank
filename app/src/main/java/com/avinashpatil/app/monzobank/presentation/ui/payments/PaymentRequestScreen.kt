package com.avinashpatil.app.monzobank.presentation.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentRequestScreen(
    onBackClick: () -> Unit,
    onRequestSent: () -> Unit,
    onContactSelect: (Contact) -> Unit
) {
    var selectedContact by remember { mutableStateOf<Contact?>(null) }
    var amount by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var dueDate by remember { mutableStateOf("") }
    
    val contacts = remember { getContacts() }
    val filteredContacts = contacts.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Request Money") },
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
            if (selectedContact == null) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search contacts") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    Text(
                        text = "Select Contact",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(filteredContacts) { contact ->
                    ContactCard(
                        contact = contact,
                        onClick = {
                            selectedContact = contact
                            onContactSelect(contact)
                        }
                    )
                }
            } else {
                item {
                    SelectedContactCard(
                        contact = selectedContact!!,
                        onRemove = { selectedContact = null }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                                amount = newValue
                            }
                        },
                        label = { Text("Amount (£)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = reason,
                        onValueChange = { reason = it },
                        label = { Text("Reason for request") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        placeholder = { Text("e.g., Dinner split, Concert tickets...") }
                    )
                }
                
                item {
                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due date (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., By Friday, ASAP...") }
                    )
                }
                
                item {
                    RequestSummaryCard(
                        contact = selectedContact!!,
                        amount = amount,
                        reason = reason,
                        dueDate = dueDate
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Share request link
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Link")
                        }
                        
                        Button(
                            onClick = {
                                isLoading = true
                                // Simulate request process
                                onRequestSent()
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && amount.isNotEmpty() && reason.isNotEmpty() && amount.toDoubleOrNull() != null && amount.toDouble() > 0
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Text("Send Request")
                            }
                        }
                    }
                }
                
                item {
                    RequestTipsCard()
                }
            }
        }
    }
}

@Composable
fun RequestSummaryCard(
    contact: Contact,
    amount: String,
    reason: String,
    dueDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Request Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "From:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (amount.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Amount:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "£$amount",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            if (reason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reason:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            if (dueDate.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Due:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = dueDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RequestTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Request Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val tips = listOf(
                "Be specific about what the money is for",
                "Set a reasonable due date if needed",
                "You can send reminders for overdue requests",
                "Share the request link via text or social media"
            )
            
            tips.forEach { tip ->
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
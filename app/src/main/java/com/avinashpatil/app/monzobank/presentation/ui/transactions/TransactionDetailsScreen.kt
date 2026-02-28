package com.avinashpatil.app.monzobank.presentation.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(
    transactionId: String,
    onBackClick: () -> Unit,
    onCategorizeClick: () -> Unit = {},
    onSplitClick: () -> Unit = {},
    onReceiptClick: () -> Unit = {},
    onDisputeClick: () -> Unit = {}
) {
    // Mock transaction data
    val transaction = remember {
        Transaction(
            id = transactionId,
            accountId = "acc_1",
            amount = -45.50,
            description = "Tesco Express",
            category = "Groceries",
            date = Date(),
            merchant = "Tesco",
            type = "debit",
            location = "123 High Street, London",
            reference = "TXN${transactionId.takeLast(8).uppercase()}",
            balance = 2801.82
        )
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy 'at' HH:mm", Locale.UK)
    val isDebit = transaction.amount < 0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Transaction Details") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* Share transaction */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Transaction Amount Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDebit) 
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Transaction Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDebit) 
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                                    else 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDebit) Icons.Default.Remove else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = if (isDebit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = currencyFormatter.format(Math.abs(transaction.amount)),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDebit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = if (isDebit) "Payment" else "Received",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Transaction Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Transaction Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        DetailRow("Merchant", transaction.merchant ?: "Unknown")
                        DetailRow("Description", transaction.description)
                        DetailRow("Category", transaction.category)
                        DetailRow("Date & Time", dateFormatter.format(transaction.date))
                        DetailRow("Reference", transaction.reference ?: "N/A")
                        transaction.location?.let {
                            DetailRow("Location", it)
                        }
                        DetailRow("Balance After", currencyFormatter.format(transaction.balance))
                    }
                }
            }

            // Quick Actions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.Category,
                                label = "Categorize",
                                onClick = onCategorizeClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.CallSplit,
                                label = "Split Bill",
                                onClick = onSplitClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.Receipt,
                                label = "Receipt",
                                onClick = onReceiptClick
                            )
                        }
                    }
                }
            }

            // Additional Options
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "More Options",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        ListItem(
                            headlineContent = { Text("Add Note") },
                            leadingContent = {
                                Icon(Icons.Default.Note, contentDescription = null)
                            },
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        )

                        ListItem(
                            headlineContent = { Text("Set Reminder") },
                            leadingContent = {
                                Icon(Icons.Default.Alarm, contentDescription = null)
                            },
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        )

                        ListItem(
                            headlineContent = { Text("Export Details") },
                            leadingContent = {
                                Icon(Icons.Default.Download, contentDescription = null)
                            },
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        )

                        if (isDebit) {
                            Divider()
                            ListItem(
                                headlineContent = { 
                                    Text(
                                        "Report Issue",
                                        color = MaterialTheme.colorScheme.error
                                    ) 
                                },
                                leadingContent = {
                                    Icon(
                                        Icons.Default.Report,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }

            // Security Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Secure Transaction",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "This transaction was processed securely with end-to-end encryption.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}
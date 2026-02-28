package com.avinashpatil.app.monzobank.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.avinashpatil.app.monzobank.data.model.Account
import com.avinashpatil.app.monzobank.data.model.Transaction
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountId: String,
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit = {},
    onTransferClick: () -> Unit = {},
    onStatementClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    // Mock account data
    val account = remember {
        Account(
            id = accountId,
            name = "Current Account",
            type = "current",
            balance = 2847.32,
            currency = "GBP",
            accountNumber = "12345678",
            sortCode = "04-00-04",
            isActive = true
        )
    }

    // Mock transactions
    val transactions = remember {
        listOf(
            Transaction(
                id = "1",
                accountId = accountId,
                amount = -45.50,
                description = "Tesco Express",
                category = "Groceries",
                date = Date(),
                merchant = "Tesco",
                type = "debit"
            ),
            Transaction(
                id = "2",
                accountId = accountId,
                amount = 1200.00,
                description = "Salary Payment",
                category = "Income",
                date = Date(System.currentTimeMillis() - 86400000),
                merchant = "Employer Ltd",
                type = "credit"
            ),
            Transaction(
                id = "3",
                accountId = accountId,
                amount = -12.99,
                description = "Netflix Subscription",
                category = "Entertainment",
                date = Date(System.currentTimeMillis() - 172800000),
                merchant = "Netflix",
                type = "debit"
            )
        )
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text(account.name) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currencyFormatter.format(account.balance),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Sort Code: ${account.sortCode}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Account: ${account.accountNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
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
                                icon = Icons.Default.Send,
                                label = "Transfer",
                                onClick = onTransferClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.Receipt,
                                label = "Statement",
                                onClick = onStatementClick
                            )
                            QuickActionButton(
                                icon = Icons.Default.Share,
                                label = "Share Details",
                                onClick = { /* Handle share */ }
                            )
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }

            // View All Transactions
            item {
                OutlinedButton(
                    onClick = { /* Navigate to all transactions */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Transactions")
                }
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (transaction.amount > 0) 
                            Color.Green.copy(alpha = 0.1f) 
                        else 
                            Color.Red.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.amount > 0) Icons.Default.Add else Icons.Default.Remove,
                    contentDescription = null,
                    tint = if (transaction.amount > 0) Color.Green else Color.Red
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Amount
            Text(
                text = currencyFormatter.format(Math.abs(transaction.amount)),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.amount > 0) Color.Green else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
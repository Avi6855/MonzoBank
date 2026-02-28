package com.avinashpatil.app.monzobank.presentation.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardOverviewScreen(
    onBackClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit
) {
    var balanceVisible by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Dashboard Overview") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = { balanceVisible = !balanceVisible }
                ) {
                    Icon(
                        if (balanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (balanceVisible) "Hide Balance" else "Show Balance"
                    )
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TotalBalanceCard(balanceVisible = balanceVisible)
            }
            
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                QuickActionsRow()
            }
            
            item {
                Text(
                    text = "Accounts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(getAccounts()) { account ->
                AccountOverviewCard(
                    account = account,
                    balanceVisible = balanceVisible,
                    onClick = { onAccountClick(account.id) }
                )
            }
            
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(getRecentTransactions()) { transaction ->
                TransactionOverviewCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
}

@Composable
fun TotalBalanceCard(balanceVisible: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (balanceVisible) "£5,432.10" else "••••••",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuickActionsRow() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(getQuickActions()) { action ->
            QuickActionCard(action = action)
        }
    }
}

@Composable
fun QuickActionCard(action: QuickAction) {
    Card(
        modifier = Modifier.size(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = action.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AccountOverviewCard(
    account: AccountOverview,
    balanceVisible: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = account.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = if (balanceVisible) "£${account.balance}" else "••••••",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun TransactionOverviewCard(
    transaction: TransactionOverview,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = transaction.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "£${transaction.amount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncoming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

data class QuickAction(
    val title: String,
    val emoji: String
)

data class AccountOverview(
    val id: String,
    val name: String,
    val type: String,
    val balance: String
)

data class TransactionOverview(
    val id: String,
    val description: String,
    val amount: String,
    val date: String,
    val emoji: String,
    val isIncoming: Boolean
)

fun getQuickActions(): List<QuickAction> {
    return listOf(
        QuickAction("Transfer", "💸"),
        QuickAction("Pay Bills", "📄"),
        QuickAction("QR Pay", "📱"),
        QuickAction("Split Bill", "🧾")
    )
}

fun getAccounts(): List<AccountOverview> {
    return listOf(
        AccountOverview("1", "Current Account", "Personal", "2,345.67"),
        AccountOverview("2", "Savings Account", "Savings", "3,086.43")
    )
}

fun getRecentTransactions(): List<TransactionOverview> {
    return listOf(
        TransactionOverview("1", "Tesco Superstore", "45.67", "Today", "🛒", false),
        TransactionOverview("2", "Salary Payment", "2,500.00", "Yesterday", "💰", true),
        TransactionOverview("3", "Netflix Subscription", "12.99", "2 days ago", "📺", false)
    )
}
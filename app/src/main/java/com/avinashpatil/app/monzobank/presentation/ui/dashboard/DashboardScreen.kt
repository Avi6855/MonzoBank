package com.avinashpatil.app.monzobank.presentation.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import com.avinashpatil.app.monzobank.presentation.viewmodel.AccountViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTransactions: (String) -> Unit,
    onNavigateToCards: () -> Unit,
    onNavigateToTransfers: () -> Unit,
    accountViewModel: AccountViewModel? = null,
    transactionViewModel: TransactionViewModel? = null
) {
    // For now, we'll create mock data since ViewModels are disabled
    // val accountUiState by accountViewModel?.uiState?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(AccountUiState()) }
    // val selectedAccount by accountViewModel?.selectedAccount?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(null) }
    // val transactionUiState by transactionViewModel?.uiState?.collectAsStateWithLifecycle() ?: remember { mutableStateOf(TransactionUiState()) }
    
    // Mock data for demonstration
    // val accountUiState = remember { AccountUiState(accounts = emptyList(), isLoading = false) }
    val selectedAccount: Account? = null
    // val transactionUiState = remember { TransactionUiState(recentTransactions = emptyList(), isLoadingRecent = false) }
    
    // Mock user ID - in real app this would come from authentication
    val userId = "user123"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Good morning!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = { /* Navigate to profile */ }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MonzoCoralPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Balance Card
            item {
                AccountBalanceCard(
                    accounts = emptyList(), // Mock: no accounts
                    selectedAccount = selectedAccount,
                    onAccountSelected = { /* accountViewModel?.selectAccount(it) */ },
                    isLoading = false // Mock: not loading
                )
            }
            
            // Quick Actions
            item {
                DashboardQuickActionsSection(
                    onTransferClick = onNavigateToTransfers,
                    onCardsClick = onNavigateToCards,
                    onPayClick = { /* Navigate to payments */ },
                    onTopUpClick = { /* Navigate to top up */ }
                )
            }
            
            // Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = emptyList(), // Mock: no recent transactions
                    isLoading = false, // Mock: not loading
                    onViewAllClick = {
                        selectedAccount?.let { account ->
                            onNavigateToTransactions(account.id)
                        }
                    }
                )
            }
            
            // Spending Insights (placeholder)
            item {
                SpendingInsightsCard()
            }
        }
    }
}

@Composable
fun AccountBalanceCard(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MonzoCoralPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (accounts.isNotEmpty()) {
                // Account selector
                if (accounts.size > 1) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(accounts) { account ->
                            AccountChip(
                                account = account,
                                isSelected = account.id == selectedAccount?.id,
                                onClick = { onAccountSelected(account) }
                            )
                        }
                    }
                }
                
                selectedAccount?.let { account ->
                    Text(
                        text = account.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = account.formattedBalance,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    Text(
                        text = "Available balance: ${account.formattedBalance}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "No accounts found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AccountChip(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color.White.copy(alpha = 0.2f)
    } else {
        Color.White.copy(alpha = 0.1f)
    }
    
    Surface(
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(20.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = account.type.name.lowercase().replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White
        )
    }
}

@Composable
fun DashboardQuickActionsSection(
    onTransferClick: () -> Unit,
    onCardsClick: () -> Unit,
    onPayClick: () -> Unit,
    onTopUpClick: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionItem(
                icon = Icons.Default.Send, // Using Send instead of SwapHoriz
                label = "Transfer",
                onClick = onTransferClick
            )
            QuickActionItem(
                icon = Icons.Default.AccountBox, // Using AccountBox instead of CreditCard
                label = "Cards",
                onClick = onCardsClick
            )
            QuickActionItem(
                icon = Icons.Default.AccountCircle, // Using AccountCircle instead of Payment
                label = "Pay",
                onClick = onPayClick
            )
            QuickActionItem(
                icon = Icons.Default.Add,
                label = "Top Up",
                onClick = onTopUpClick
            )
        }
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            color = MonzoCoralSecondary.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MonzoCoralPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    isLoading: Boolean,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "View All",
                    color = MonzoCoralPrimary
                )
            }
        }
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MonzoCoralPrimary)
            }
        } else if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    transactions.forEach { transaction ->
                        DashboardTransactionItem(
                            transaction = transaction,
                            onClick = { /* Navigate to transaction details */ }
                        )
                        if (transaction != transactions.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.getDefault())
    val amountColor = if (transaction.transactionType == TransactionType.CREDIT) {
        Color(0xFF4CAF50)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Transaction icon
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = transaction.category.name.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Transaction details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.displayDescription,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = dateFormatter.format(transaction.transactionDate),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Amount
        Text(
            text = if (transaction.transactionType == TransactionType.CREDIT) {
                "+${transaction.formattedAmount}"
            } else {
                "-${transaction.formattedAmount}"
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = amountColor
        )
    }
}

@Composable
fun SpendingInsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Spending Insights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Track your spending patterns and get personalized insights to help you manage your money better.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            TextButton(
                onClick = { /* Navigate to insights */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MonzoCoralPrimary
                )
            ) {
                Text("View Insights")
            }
        }
    }
}
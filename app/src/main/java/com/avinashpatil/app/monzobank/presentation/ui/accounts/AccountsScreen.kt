package com.avinashpatil.app.monzobank.presentation.ui.accounts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import com.avinashpatil.app.monzobank.presentation.viewmodel.AccountViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTransactions: (String) -> Unit,
    onNavigateToStatements: (String) -> Unit,
    accountViewModel: AccountViewModel? = null
) {
    // Mock data for demonstration
    val accountUiState = remember { AccountUiState(accounts = emptyList(), isLoading = false) }
    val selectedAccount: Account? = null
    
    var showCreateAccountDialog by remember { mutableStateOf(false) }
    
    // Mock user ID - in real app this would come from authentication
    val userId = "user123"
    
    // LaunchedEffect(Unit) {
    //     accountViewModel?.loadAccounts(userId)
    // }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Accounts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showCreateAccountDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Account",
                        tint = MonzoCoralPrimary
                    )
                }
            }
        )
        
        if (accountUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MonzoCoralPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Balance Summary
                item {
                    TotalBalanceSummary(
                        totalBalance = java.math.BigDecimal.ZERO, // accountViewModel?.getTotalBalance() ?: BigDecimal.ZERO,
                        accountCount = accountUiState.accounts.size
                    )
                }
                
                // Account Type Sections
                AccountType.values().forEach { accountType ->
                    val accountsOfType = emptyList<Account>() // accountViewModel?.getAccountsByType(accountType) ?: emptyList()
                    if (accountsOfType.isNotEmpty()) {
                        item {
                            AccountTypeSection(
                                accountType = accountType,
                                accounts = accountsOfType,
                                onAccountClick = { account ->
                                    // accountViewModel?.selectAccount(account)
                                    onNavigateToTransactions(account.id)
                                },
                                onStatementsClick = onNavigateToStatements
                            )
                        }
                    }
                }
                
                // Empty state if no accounts
                if (accountUiState.accounts.isEmpty()) {
                    item {
                        EmptyAccountsState(
                            onCreateAccountClick = { showCreateAccountDialog = true }
                        )
                    }
                }
            }
        }
    }
    
    // Create Account Dialog
    if (showCreateAccountDialog) {
        CreateAccountDialog(
            onDismiss = { showCreateAccountDialog = false },
            onCreateAccount = { accountType, accountName ->
                // accountViewModel?.createAccount(
                //     userId = userId,
                //     accountType = accountType,
                //     accountName = accountName
                // )
                showCreateAccountDialog = false
            },
            isLoading = accountUiState.isCreatingAccount
        )
    }
    
    // Error handling
    accountUiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            // accountViewModel?.clearError()
        }
    }
}

@Composable
fun TotalBalanceSummary(
    totalBalance: java.math.BigDecimal,
    accountCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MonzoCoralPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Text(
                text = "£${String.format("%.2f", totalBalance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = "Across $accountCount ${if (accountCount == 1) "account" else "accounts"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AccountTypeSection(
    accountType: AccountType,
    accounts: List<Account>,
    onAccountClick: (Account) -> Unit,
    onStatementsClick: (String) -> Unit
) {
    Column {
        Text(
            text = getAccountTypeDisplayName(accountType),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        accounts.forEach { account ->
            AccountCard(
                account = account,
                onClick = { onAccountClick(account) },
                onStatementsClick = { onStatementsClick(account.id) }
            )
            
            if (account != accounts.last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AccountCard(
    account: Account,
    onClick: () -> Unit,
    onStatementsClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = account.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${account.sortCode} • ${account.accountNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    Text(
                        text = "Opened ${dateFormatter.format(account.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = account.formattedBalance,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MonzoCoralPrimary
                    )
                    
                    if (account.availableBalance != account.balance) {
                        Text(
                            text = "Available: ${account.formattedAvailableBalance}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
            
            // Account actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MonzoCoralPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Transactions")
                }
                
                OutlinedButton(
                    onClick = onStatementsClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MonzoCoralPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Statements")
                }
            }
        }
    }
}

@Composable
fun EmptyAccountsState(
    onCreateAccountClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No accounts yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Create your first account to start managing your money with Monzo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            
            Button(
                onClick = onCreateAccountClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Account")
            }
        }
    }
}

@Composable
fun CreateAccountDialog(
    onDismiss: () -> Unit,
    onCreateAccount: (AccountType, String?) -> Unit,
    isLoading: Boolean
) {
    var selectedAccountType by remember { mutableStateOf(AccountType.CURRENT) }
    var accountName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create New Account",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Choose account type:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                AccountType.values().forEach { accountType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAccountType = accountType }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedAccountType == accountType,
                            onClick = { selectedAccountType = accountType },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MonzoCoralPrimary
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = getAccountTypeDisplayName(accountType),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = getAccountTypeDescription(accountType),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MonzoCoralPrimary,
                        focusedLabelColor = MonzoCoralPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateAccount(
                        selectedAccountType,
                        accountName.takeIf { it.isNotBlank() }
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

fun getAccountTypeDisplayName(accountType: AccountType): String {
    return when (accountType) {
        AccountType.CURRENT -> "Current Account"
        AccountType.SAVINGS -> "Savings Account"
        AccountType.BUSINESS -> "Business Account"
        AccountType.JOINT -> "Joint Account"
        AccountType.ISA -> "ISA Account"
        AccountType.PREMIUM -> "Premium Account"
    }
}

fun getAccountTypeDescription(accountType: AccountType): String {
    return when (accountType) {
        AccountType.CURRENT -> "For everyday spending and payments"
        AccountType.SAVINGS -> "Earn interest on your savings"
        AccountType.BUSINESS -> "Manage your business finances"
        AccountType.JOINT -> "Share an account with someone"
        AccountType.ISA -> "Tax-free savings account"
        AccountType.PREMIUM -> "Premium banking with exclusive benefits"
    }
}

fun getAccountTypeIcon(accountType: AccountType): ImageVector {
    return when (accountType) {
        AccountType.CURRENT -> Icons.Default.Home
        AccountType.SAVINGS -> Icons.Default.Star
        AccountType.BUSINESS -> Icons.Default.AccountBox
        AccountType.JOINT -> Icons.Default.Person
        AccountType.ISA -> Icons.Default.Savings
        AccountType.PREMIUM -> Icons.Default.Diamond
    }
}
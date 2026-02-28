package com.avinashpatil.app.monzobank.presentation.ui.family

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
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
// Using local TransactionStatus enum instead of data.local.entity.TransactionStatus

data class FamilyAccount(
    val id: String,
    val accountName: String,
    val accountNumber: String,
    val accountType: FamilyAccountType,
    val balance: Double,
    val currency: String,
    val ownerId: String,
    val ownerName: String,
    val permissions: List<AccountPermission>,
    val spendingLimit: Double,
    val monthlyAllowance: Double,
    val isActive: Boolean,
    val createdDate: String
)

data class AccountPermission(
    val memberId: String,
    val memberName: String,
    val permissionType: PermissionType,
    val spendingLimit: Double
)

data class AccountTransaction(
    val id: String,
    val accountId: String,
    val description: String,
    val amount: Double,
    val date: String,
    val category: String,
    val memberId: String,
    val memberName: String,
    val type: TransactionType,
    val status: TransactionStatus
)

data class SpendingControl(
    val memberId: String,
    val memberName: String,
    val dailyLimit: Double,
    val weeklyLimit: Double,
    val monthlyLimit: Double,
    val blockedCategories: List<String>,
    val allowedMerchants: List<String>,
    val requireApproval: Boolean
)

enum class PermissionType {
    VIEW_ONLY, SPEND, MANAGE, ADMIN
}

enum class TransactionStatus {
    PENDING, APPROVED, DECLINED, COMPLETED, PROCESSING, FAILED, CANCELLED, REVERSED, DISPUTED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyAccountsScreen(
    onBackClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onCreateAccountClick: () -> Unit,
    onManagePermissionsClick: (String) -> Unit
) {
    val familyAccounts = remember { getDummyFamilyAccounts() }
    val recentTransactions = remember { getDummyAccountTransactions() }
    val spendingControls = remember { getDummySpendingControls() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Accounts", "Transactions", "Controls")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Accounts") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Account settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { /* Reports */ }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reports")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onCreateAccountClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Account")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> AccountsContent(
                    accounts = familyAccounts,
                    onAccountClick = onAccountClick,
                    onManagePermissionsClick = onManagePermissionsClick
                )
                1 -> TransactionsContent(
                    transactions = recentTransactions,
                    onTransactionClick = onTransactionClick
                )
                2 -> SpendingControlsContent(
                    controls = spendingControls,
                    onEditControlClick = { /* Edit spending control */ }
                )
            }
        }
    }
}

@Composable
fun AccountsContent(
    accounts: List<FamilyAccount>,
    onAccountClick: (String) -> Unit,
    onManagePermissionsClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Accounts Summary
        item {
            AccountsSummaryCard(accounts = accounts)
        }

        // Account Types Filter
        item {
            AccountTypesFilter()
        }

        // Accounts List
        item {
            Text(
                text = "Family Accounts (${accounts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (accounts.isEmpty()) {
            item {
                EmptyAccountsState()
            }
        } else {
            items(accounts) { account ->
                FamilyAccountCard(
                    account = account,
                    onClick = { onAccountClick(account.id) },
                    onManagePermissionsClick = { onManagePermissionsClick(account.id) }
                )
            }
        }
    }
}

@Composable
fun TransactionsContent(
    transactions: List<AccountTransaction>,
    onTransactionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* Filter transactions */ }) {
                    Text("Filter")
                }
            }
        }

        if (transactions.isEmpty()) {
            item {
                EmptyTransactionsState()
            }
        } else {
            items(transactions) { transaction ->
                AccountTransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
}

@Composable
fun SpendingControlsContent(
    controls: List<SpendingControl>,
    onEditControlClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Spending Controls",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Manage spending limits and restrictions for family members",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(controls) { control ->
            SpendingControlCard(
                control = control,
                onEditClick = { onEditControlClick(control.memberId) }
            )
        }
    }
}

@Composable
fun AccountsSummaryCard(accounts: List<FamilyAccount>) {
    val totalBalance = accounts.sumOf { it.balance }
    val activeAccounts = accounts.count { it.isActive }
    val totalAllowance = accounts.sumOf { it.monthlyAllowance }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Accounts Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Total Balance",
                    value = "£${String.format("%,.2f", totalBalance)}",
                    icon = Icons.Default.AccountBalance
                )
                SummaryItem(
                    title = "Active Accounts",
                    value = activeAccounts.toString(),
                    icon = Icons.Default.AccountBox
                )
                SummaryItem(
                    title = "Monthly Allowance",
                    value = "£${String.format("%,.0f", totalAllowance)}",
                    icon = Icons.Default.Schedule
                )
            }
        }
    }
}

@Composable
fun AccountTypesFilter() {
    val accountTypes = listOf("All", "Parent", "Teen", "Child", "Joint")
    var selectedType by remember { mutableStateOf("All") }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(accountTypes) { type ->
            FilterChip(
                onClick = { selectedType = type },
                label = { Text(type) },
                selected = type == selectedType
            )
        }
    }
}

@Composable
fun FamilyAccountCard(
    account: FamilyAccount,
    onClick: () -> Unit,
    onManagePermissionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Account Type Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(getAccountTypeColor(account.accountType).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAccountTypeIcon(account.accountType),
                            contentDescription = null,
                            tint = getAccountTypeColor(account.accountType),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = account.accountName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "****${account.accountNumber.takeLast(4)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${account.currency} ${String.format("%,.2f", account.balance)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    FamilyAccountTypeChip(type = account.accountType)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Account Owner and Permissions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Owner: ${account.ownerName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (account.permissions.isNotEmpty()) {
                        Text(
                            text = "${account.permissions.size} member(s) have access",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Row {
                    if (account.spendingLimit > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Limit: £${String.format("%.0f", account.spendingLimit)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = onManagePermissionsClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ManageAccounts,
                            contentDescription = "Manage Permissions",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTransactionCard(
    transaction: AccountTransaction,
    onClick: () -> Unit
) {
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
            Icon(
                imageVector = if (transaction.type == TransactionType.CREDIT) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (transaction.type == TransactionType.CREDIT) Color.Green else Color.Red,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${transaction.memberName} • ${transaction.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.CREDIT) "+" else "-"}£${String.format("%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.type == TransactionType.CREDIT) Color.Green else Color.Red
                )
                TransactionStatusChip(status = transaction.status)
            }
        }
    }
}

@Composable
fun SpendingControlCard(
    control: SpendingControl,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = control.memberName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row {
                    if (control.requireApproval) {
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Approval Required",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Controls",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Spending Limits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LimitItem(
                    title = "Daily",
                    amount = control.dailyLimit
                )
                LimitItem(
                    title = "Weekly",
                    amount = control.weeklyLimit
                )
                LimitItem(
                    title = "Monthly",
                    amount = control.monthlyLimit
                )
            }
            
            if (control.blockedCategories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Blocked Categories: ${control.blockedCategories.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LimitItem(
    title: String,
    amount: Double
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "£${String.format("%.0f", amount)}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionStatusChip(status: TransactionStatus) {
    val (color, text) = when (status) {
        TransactionStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        TransactionStatus.APPROVED -> Color.Green to "Approved"
        TransactionStatus.PROCESSING -> Color.Blue to "Processing"
        TransactionStatus.FAILED -> Color.Red to "Failed"
        TransactionStatus.CANCELLED -> Color.Gray to "Cancelled"
        TransactionStatus.REVERSED -> Color.Red to "Reversed"
        TransactionStatus.DISPUTED -> Color(0xFFFF9800) to "Disputed"
        TransactionStatus.DECLINED -> Color.Red to "Declined"
        TransactionStatus.COMPLETED -> Color.Blue to "Completed"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun EmptyAccountsState() {
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Family Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first family account to start managing finances together.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyTransactionsState() {
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
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Family account transactions will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getAccountTypeIcon(type: FamilyAccountType): ImageVector {
    return when (type) {
        FamilyAccountType.PARENT -> Icons.Default.Person
        FamilyAccountType.TEEN -> Icons.Default.School
        FamilyAccountType.CHILD -> Icons.Default.ChildCare
        FamilyAccountType.JOINT -> Icons.Default.Group
    }
}

fun getDummyFamilyAccounts(): List<FamilyAccount> {
    return listOf(
        FamilyAccount(
            id = "FA001",
            accountName = "Smith Family Main Account",
            accountNumber = "12345678",
            accountType = FamilyAccountType.JOINT,
            balance = 4500.00,
            currency = "£",
            ownerId = "FM001",
            ownerName = "John Smith",
            permissions = listOf(
                AccountPermission("FM002", "Sarah Smith", PermissionType.ADMIN, 2000.0),
                AccountPermission("FM003", "Emma Smith", PermissionType.SPEND, 200.0)
            ),
            spendingLimit = 5000.0,
            monthlyAllowance = 0.0,
            isActive = true,
            createdDate = "Jan 15, 2023"
        ),
        FamilyAccount(
            id = "FA002",
            accountName = "Emma's Teen Account",
            accountNumber = "87654321",
            accountType = FamilyAccountType.TEEN,
            balance = 350.00,
            currency = "£",
            ownerId = "FM003",
            ownerName = "Emma Smith",
            permissions = listOf(
                AccountPermission("FM001", "John Smith", PermissionType.ADMIN, 0.0),
                AccountPermission("FM002", "Sarah Smith", PermissionType.MANAGE, 0.0)
            ),
            spendingLimit = 200.0,
            monthlyAllowance = 100.0,
            isActive = true,
            createdDate = "Mar 10, 2023"
        ),
        FamilyAccount(
            id = "FA003",
            accountName = "Jake's Savings",
            accountNumber = "11223344",
            accountType = FamilyAccountType.CHILD,
            balance = 125.00,
            currency = "£",
            ownerId = "FM004",
            ownerName = "Jake Smith",
            permissions = listOf(
                AccountPermission("FM001", "John Smith", PermissionType.ADMIN, 0.0),
                AccountPermission("FM002", "Sarah Smith", PermissionType.ADMIN, 0.0)
            ),
            spendingLimit = 50.0,
            monthlyAllowance = 50.0,
            isActive = true,
            createdDate = "May 20, 2023"
        )
    )
}

fun getDummyAccountTransactions(): List<AccountTransaction> {
    return listOf(
        AccountTransaction(
            id = "AT001",
            accountId = "FA002",
            description = "Monthly allowance",
            amount = 100.0,
            date = "Today",
            category = "Allowance",
            memberId = "FM003",
            memberName = "Emma Smith",
            type = TransactionType.CREDIT,
            status = TransactionStatus.COMPLETED
        ),
        AccountTransaction(
            id = "AT002",
            accountId = "FA002",
            description = "Coffee shop purchase",
            amount = 4.50,
            date = "Yesterday",
            category = "Food & Drink",
            memberId = "FM003",
            memberName = "Emma Smith",
            type = TransactionType.DEBIT,
            status = TransactionStatus.COMPLETED
        ),
        AccountTransaction(
            id = "AT003",
            accountId = "FA003",
            description = "Chore reward",
            amount = 10.0,
            date = "2 days ago",
            category = "Rewards",
            memberId = "FM004",
            memberName = "Jake Smith",
            type = TransactionType.CREDIT,
            status = TransactionStatus.COMPLETED
        ),
        AccountTransaction(
            id = "AT004",
            accountId = "FA001",
            description = "Grocery shopping",
            amount = 85.50,
            date = "3 days ago",
            category = "Groceries",
            memberId = "FM002",
            memberName = "Sarah Smith",
            type = TransactionType.DEBIT,
            status = TransactionStatus.COMPLETED
        ),
        AccountTransaction(
            id = "AT005",
            accountId = "FA002",
            description = "Online purchase - pending approval",
            amount = 45.00,
            date = "1 hour ago",
            category = "Shopping",
            memberId = "FM003",
            memberName = "Emma Smith",
            type = TransactionType.DEBIT,
            status = TransactionStatus.PENDING
        )
    )
}

fun getDummySpendingControls(): List<SpendingControl> {
    return listOf(
        SpendingControl(
            memberId = "FM003",
            memberName = "Emma Smith",
            dailyLimit = 25.0,
            weeklyLimit = 100.0,
            monthlyLimit = 200.0,
            blockedCategories = listOf("Gambling", "Alcohol", "Adult Content"),
            allowedMerchants = listOf("School Cafeteria", "Local Library", "Bus Service"),
            requireApproval = true
        ),
        SpendingControl(
            memberId = "FM004",
            memberName = "Jake Smith",
            dailyLimit = 10.0,
            weeklyLimit = 30.0,
            monthlyLimit = 50.0,
            blockedCategories = listOf("Gambling", "Alcohol", "Adult Content", "Online Gaming"),
            allowedMerchants = listOf("School Store", "Ice Cream Truck", "Toy Store"),
            requireApproval = true
        )
    )
}
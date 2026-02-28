package com.avinashpatil.app.monzobank.presentation.screens.main

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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.avinashpatil.app.monzobank.data.dummy.DummyDataProvider
import com.avinashpatil.app.monzobank.data.dummy.QuickAction
import com.avinashpatil.app.monzobank.domain.model.*
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToScreen: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(
                    navController = navController,
                    onNavigateToScreen = onNavigateToScreen
                )
            }
            composable("accounts") {
                AccountsScreen(navController = navController)
            }
            composable("cards") {
                CardsScreen(navController = navController)
            }
            composable("payments") {
                PaymentsScreen(navController = navController)
            }
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    onLogout = onLogout,
                    onNavigateToScreen = onNavigateToScreen
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    onNavigateToScreen: (String) -> Unit = {}
) {
    // Get dummy data
    val user = remember { DummyDataProvider.getUser() }
    val accounts = remember { DummyDataProvider.getAccounts() }
    val accountSummary = remember { DummyDataProvider.getAccountSummary() }
    val recentTransactions = remember { DummyDataProvider.getRecentActivity() }
    val quickActions = remember { DummyDataProvider.getQuickActions() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            WelcomeHeader(user = user)
        }
        
        // Account Balance Card
        item {
            AccountBalanceCard(
                accountSummary = accountSummary,
                primaryAccount = accounts.firstOrNull { it.isDefault }
            )
        }
        
        // Quick Actions
        item {
            QuickActionsSection(quickActions = quickActions, onNavigateToScreen = onNavigateToScreen)
        }
        
        // Recent Transactions
        item {
            RecentTransactionsSection(
                transactions = recentTransactions,
                onViewAllClick = { onNavigateToScreen("transactions") }
            )
        }
        
        // Account Overview
        item {
            AccountOverviewSection(
                accounts = accounts,
                onAccountClick = { navController.navigate("accounts") }
            )
        }
    }
}

@Composable
fun AccountsScreen(navController: NavController) {
    val accounts = remember { DummyDataProvider.getAccounts() }
    val accountSummary = remember { DummyDataProvider.getAccountSummary() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Accounts",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Total Balance Summary
        item {
            AccountsTotalBalanceCard(accountSummary = accountSummary)
        }
        
        // Accounts List
        item {
            Text(
                text = "Your Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        items(accounts) { account ->
            AccountDetailCard(
                account = account,
                onClick = { navController.navigate("accounts/${account.id}") }
            )
        }
        
        // Add Account Button
        item {
            AddAccountCard(
                onClick = { navController.navigate("accounts/create") }
            )
        }
    }
}

@Composable
fun CardsScreen(navController: NavController) {
    val cards = remember { DummyDataProvider.getCards() }
    val recentTransactions = remember { DummyDataProvider.getTransactions().filter { it.cardId != null }.take(3) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Cards",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Cards List
        items(cards) { card ->
            CardDetailItem(
                card = card,
                onClick = { navController.navigate("cards/${card.id}") }
            )
        }
        
        // Recent Card Transactions
        if (recentTransactions.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Card Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(recentTransactions) { transaction ->
                CardTransactionItem(transaction = transaction)
            }
        }
        
        // Add Card Button
        item {
            AddCardButton(
                onClick = { /* Show add card dialog - implement card creation */ }
            )
        }
    }
}

@Composable
fun PaymentsScreen(navController: NavController) {
    val payments = remember { DummyDataProvider.getPayments() }
    val quickActions = remember { DummyDataProvider.getQuickActions() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Payments",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Quick Payment Actions
        item {
            PaymentQuickActions(
                actions = quickActions,
                onActionClick = { action ->
                    navController.navigate(action.route)
                }
            )
        }
        
        // Recent Payments
        item {
            Text(
                text = "Recent Payments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        items(payments) { payment ->
            PaymentItem(
                payment = payment,
                onClick = { /* Navigate to payment details - implement payment details screen */ }
            )
        }
        
        // Scheduled Payments Section
        item {
            ScheduledPaymentsCard()
        }
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    onLogout: () -> Unit,
    onNavigateToScreen: (String) -> Unit = {}
) {
    val user = remember { DummyDataProvider.getUser() }
    val accountSummary = remember { DummyDataProvider.getAccountSummary() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        item {
            ProfileHeader(user = user)
        }
        
        // Account Summary
        item {
            ProfileAccountSummary(accountSummary = accountSummary)
        }
        
        // Settings Sections
        item {
            ProfileSettingsSection(
                title = "Account Settings",
                items = listOf(
                    ProfileMenuItem("Personal Details", Icons.Default.Person, "Update your information"),
                    ProfileMenuItem("Security Settings", Icons.Default.Security, "Manage passwords & 2FA"),
                    ProfileMenuItem("Notifications", Icons.Default.Notifications, "Control your alerts")
                ),
                onItemClick = { item ->
                    when (item.title) {
                        "Personal Details" -> onNavigateToScreen("settings")
                        "Security Settings" -> onNavigateToScreen("security")
                        "Notifications" -> onNavigateToScreen("settings")
                    }
                }
            )
        }
        
        item {
            ProfileSettingsSection(
                title = "Banking",
                items = listOf(
                    ProfileMenuItem("Statements", Icons.Default.Receipt, "Download statements"),
                    ProfileMenuItem("Direct Debits", Icons.Default.Schedule, "Manage recurring payments"),
                    ProfileMenuItem("Standing Orders", Icons.Default.Repeat, "View scheduled transfers")
                ),
                onItemClick = { item ->
                    when (item.title) {
                        "Statements" -> onNavigateToScreen("settings/export")
                        "Direct Debits" -> onNavigateToScreen("payments")
                        "Standing Orders" -> onNavigateToScreen("payments")
                    }
                }
            )
        }
        
        item {
            ProfileSettingsSection(
                title = "Support",
                items = listOf(
                    ProfileMenuItem("Help Center", Icons.Default.Help, "Get help and support"),
                    ProfileMenuItem("Contact Us", Icons.Default.Phone, "Speak to our team"),
                    ProfileMenuItem("Feedback", Icons.Default.Feedback, "Share your thoughts")
                ),
                onItemClick = { item ->
                    when (item.title) {
                        "Help Center" -> onNavigateToScreen("support")
                        "Contact Us" -> onNavigateToScreen("support")
                        "Feedback" -> onNavigateToScreen("support")
                    }
                }
            )
        }
        
        // Logout Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }
        }
        
        // App Version
        item {
            Text(
                text = "Monzo Bank v1.0.0 (Development Build)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = "home"
    ),
    BottomNavItem(
        title = "Accounts",
        icon = Icons.Default.AccountBalance,
        route = "accounts"
    ),
    BottomNavItem(
        title = "Cards",
        icon = Icons.Default.CreditCard,
        route = "cards"
    ),
    BottomNavItem(
        title = "Payments",
        icon = Icons.Default.Payment,
        route = "payments"
    ),
    BottomNavItem(
        title = "Profile",
        icon = Icons.Default.Person,
        route = "profile"
    )
)

// Home Screen Components
@Composable
fun WelcomeHeader(user: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good ${getTimeOfDayGreeting()}, ${user.firstName}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Welcome back to Monzo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MonzoCoralPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.initials,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AccountBalanceCard(
    accountSummary: AccountSummary,
    primaryAccount: Account?
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
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Text(
                text = "£${String.format("%.2f", accountSummary.totalBalance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            primaryAccount?.let { account ->
                Text(
                    text = "${account.name} • ${account.formattedAccountNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceInfoItem(
                    label = "Available",
                    value = "£${String.format("%.2f", accountSummary.totalAvailableBalance)}"
                )
                BalanceInfoItem(
                    label = "Accounts",
                    value = accountSummary.accountCount.toString()
                )
            }
        }
    }
}

@Composable
fun BalanceInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun QuickActionsSection(
    quickActions: List<QuickAction>,
    onNavigateToScreen: (String) -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(quickActions) { action ->
                QuickActionItem(
                    action = action,
                    onClick = { onNavigateToScreen(action.route) }
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
    action: QuickAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = action.icon,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
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
                Text("View All")
            }
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                transactions.forEach { transaction ->
                    TransactionItem(transaction = transaction)
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

@Composable
fun TransactionItem(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.categoryIcon,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = transaction.displayDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateFormat.format(transaction.transactionDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        
        Text(
            text = if (transaction.isCredit) "+${transaction.formattedAmount}" else "-${transaction.formattedAmount}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (transaction.isCredit) Color(0xFF00C851) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AccountOverviewSection(
    accounts: List<Account>,
    onAccountClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onAccountClick) {
                Text("Manage")
            }
        }
        
        accounts.take(3).forEach { account ->
            AccountOverviewItem(account = account)
            if (account != accounts.take(3).last()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AccountOverviewItem(account: Account) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = account.formattedAccountNumber,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Text(
                text = account.formattedBalance,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun getTimeOfDayGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "morning"
        in 12..17 -> "afternoon"
        else -> "evening"
    }
}

// Accounts Screen Components
@Composable
fun AccountsTotalBalanceCard(accountSummary: AccountSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Text(
                text = "£${String.format("%.2f", accountSummary.totalBalance)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AccountSummaryItem(
                    label = "Available",
                    value = "£${String.format("%.2f", accountSummary.totalAvailableBalance)}"
                )
                AccountSummaryItem(
                    label = "Accounts",
                    value = accountSummary.accountCount.toString()
                )
                AccountSummaryItem(
                    label = "Savings Rate",
                    value = "${accountSummary.savingsRate}%"
                )
            }
        }
    }
}

@Composable
fun AccountSummaryItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun AccountDetailCard(
    account: Account,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = account.formattedAccountNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    
                    // Account Type Badge
                    Surface(
                        modifier = Modifier.padding(top = 8.dp),
                        color = getAccountTypeColor(account.type),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = getAccountTypeDisplayName(account.type),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = account.formattedBalance,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Available: ${account.formattedAvailableBalance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            if (account.overdraftLimit > java.math.BigDecimal.ZERO) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Overdraft Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "£${String.format("%.2f", account.overdraftAvailable)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun AddAccountCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Account",
                tint = MonzoCoralPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Open New Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MonzoCoralPrimary
                )
                Text(
                    text = "Savings, Joint, or Business account",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

fun getAccountTypeColor(accountType: com.avinashpatil.app.monzobank.data.local.entity.AccountType): Color {
    return when (accountType) {
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.CURRENT -> MonzoCoralPrimary
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.SAVINGS -> Color(0xFF4CAF50)
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.JOINT -> Color(0xFF2196F3)
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.BUSINESS -> Color(0xFF9C27B0)
        else -> Color(0xFF607D8B)
    }
}

fun getAccountTypeDisplayName(accountType: com.avinashpatil.app.monzobank.data.local.entity.AccountType): String {
    return when (accountType) {
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.CURRENT -> "Current"
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.SAVINGS -> "Savings"
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.JOINT -> "Joint"
        com.avinashpatil.app.monzobank.data.local.entity.AccountType.BUSINESS -> "Business"
        else -> "Account"
    }
}

// Cards Screen Components
@Composable
fun CardDetailItem(
    card: Card,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = getCardBackgroundColor(card.cardType)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = card.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = card.maskedCardNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Text(
                    text = card.cardIcon,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "EXPIRES",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = card.expiryText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = card.statusText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "DAILY LIMIT",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = card.formattedDailyLimit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (card.isFrozen) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Frozen",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Card is frozen",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CardTransactionItem(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.categoryIcon,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = transaction.displayDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = dateFormat.format(transaction.transactionDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    if (transaction.isContactlessTransaction) {
                        Text(
                            text = "Contactless",
                            style = MaterialTheme.typography.labelSmall,
                            color = MonzoCoralPrimary
                        )
                    }
                }
            }
            
            Text(
                text = "-${transaction.formattedAmount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun AddCardButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Card",
                tint = MonzoCoralPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Order New Card",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MonzoCoralPrimary
                )
                Text(
                    text = "Debit, Credit, or Virtual card",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

fun getCardBackgroundColor(cardType: com.avinashpatil.app.monzobank.domain.model.CardType): Color {
    return when (cardType) {
        com.avinashpatil.app.monzobank.domain.model.CardType.DEBIT -> MonzoCoralPrimary
        com.avinashpatil.app.monzobank.domain.model.CardType.CREDIT -> Color(0xFF1976D2)
        com.avinashpatil.app.monzobank.domain.model.CardType.VIRTUAL -> Color(0xFF7B1FA2)
        com.avinashpatil.app.monzobank.domain.model.CardType.PREPAID -> Color(0xFF388E3C)
        else -> Color(0xFF424242)
    }
}

// Payments Screen Components
@Composable
fun PaymentQuickActions(
    actions: List<QuickAction>,
    onActionClick: (QuickAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(actions) { action ->
                    PaymentQuickActionItem(
                        action = action,
                        onClick = { onActionClick(action) }
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentQuickActionItem(
    action: QuickAction,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MonzoCoralPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = action.icon,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun PaymentItem(
    payment: Payment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getPaymentStatusColor(payment.status).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getPaymentIcon(payment.type),
                        contentDescription = null,
                        tint = getPaymentStatusColor(payment.status),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = payment.recipientName ?: "Payment",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = payment.description ?: payment.reference ?: "No description",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    PaymentStatusChip(status = payment.status)
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "-£${String.format("%.2f", payment.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                payment.scheduledDate?.let { date ->
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Text(
                        text = dateFormat.format(java.util.Date.from(date.atZone(java.time.ZoneId.systemDefault()).toInstant())),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentStatusChip(status: PaymentStatus) {
    Surface(
        color = getPaymentStatusColor(status).copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = getPaymentStatusText(status),
            style = MaterialTheme.typography.labelSmall,
            color = getPaymentStatusColor(status),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ScheduledPaymentsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
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
                    text = "Scheduled Payments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(
                    onClick = { /* Navigate to scheduled payments */ }
                ) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MonzoCoralPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "2 payments scheduled for this week",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Next: Rent payment - £250.00 (Tomorrow)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

fun getPaymentIcon(paymentType: PaymentType): ImageVector {
    return when (paymentType) {
        PaymentType.BANK_TRANSFER -> Icons.Default.AccountBalance
        PaymentType.INSTANT_TRANSFER -> Icons.Default.FlashOn
        else -> Icons.Default.Payment
    }
}

fun getPaymentStatusColor(status: PaymentStatus): Color {
    return when (status) {
        PaymentStatus.COMPLETED -> Color(0xFF4CAF50)
        PaymentStatus.PENDING -> Color(0xFFFF9800)
        PaymentStatus.FAILED -> Color(0xFFF44336)
        else -> Color(0xFF9E9E9E)
    }
}

fun getPaymentStatusText(status: PaymentStatus): String {
    return when (status) {
        PaymentStatus.COMPLETED -> "Completed"
        PaymentStatus.PENDING -> "Pending"
        PaymentStatus.FAILED -> "Failed"
        else -> "Unknown"
    }
}

// Profile Screen Components
data class ProfileMenuItem(
    val title: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun ProfileHeader(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MonzoCoralPrimary
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (user.isVerified) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verified Account",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAccountSummary(accountSummary: AccountSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Account Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProfileSummaryItem(
                    label = "Total Balance",
                    value = "£${String.format("%.2f", accountSummary.totalBalance)}"
                )
                ProfileSummaryItem(
                    label = "Accounts",
                    value = accountSummary.accountCount.toString()
                )
                ProfileSummaryItem(
                    label = "Monthly Savings",
                    value = "£${String.format("%.0f", accountSummary.monthlyIncome - accountSummary.monthlyExpenses)}"
                )
            }
        }
    }
}

@Composable
fun ProfileSummaryItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MonzoCoralPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun ProfileSettingsSection(
    title: String,
    items: List<ProfileMenuItem>,
    onItemClick: (ProfileMenuItem) -> Unit = {}
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ProfileSettingsItem(
                        item = item,
                        onClick = { onItemClick(item) }
                    )
                    if (index < items.size - 1) {
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

@Composable
fun ProfileSettingsItem(
    item: ProfileMenuItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = MonzoCoralPrimary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}
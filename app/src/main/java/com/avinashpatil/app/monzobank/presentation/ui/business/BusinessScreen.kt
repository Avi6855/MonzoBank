package com.avinashpatil.app.monzobank.presentation.ui.business

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

data class BusinessAccount(
    val id: String,
    val name: String,
    val accountNumber: String,
    val balance: Double,
    val currency: String,
    val type: BusinessAccountType
)

data class BusinessTransaction(
    val id: String,
    val description: String,
    val amount: Double,
    val date: String,
    val type: TransactionType,
    val category: String,
    val reference: String
)

data class BusinessService(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
)

data class QuickStat(
    val title: String,
    val value: String,
    val change: String,
    val isPositive: Boolean,
    val icon: ImageVector
)

enum class BusinessAccountType {
    CURRENT, SAVINGS, CREDIT, LOAN
}

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessScreen(
    onBackClick: () -> Unit,
    onAccountClick: (String) -> Unit,
    onServiceClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onViewAllTransactionsClick: () -> Unit
) {
    val accounts = remember { getDummyBusinessAccounts() }
    val transactions = remember { getDummyBusinessTransactions() }
    val services = remember { getDummyBusinessServices() }
    val quickStats = remember { getDummyQuickStats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Banking") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                BusinessWelcomeSection()
            }

            // Quick Stats
            item {
                QuickStatsSection(stats = quickStats)
            }

            // Business Accounts
            item {
                BusinessAccountsSection(
                    accounts = accounts,
                    onAccountClick = onAccountClick
                )
            }

            // Business Services
            item {
                BusinessServicesSection(
                    services = services,
                    onServiceClick = onServiceClick
                )
            }

            // Recent Transactions
            item {
                RecentTransactionsSection(
                    transactions = transactions,
                    onTransactionClick = onTransactionClick,
                    onViewAllClick = onViewAllTransactionsClick
                )
            }

            // Business Insights
            item {
                BusinessInsightsSection()
            }
        }
    }
}

@Composable
fun BusinessWelcomeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Welcome to Business Banking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Manage your business finances with ease",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Access powerful tools and services designed specifically for your business needs. From account management to payroll processing, we've got you covered.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun QuickStatsSection(stats: List<QuickStat>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Business Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(stats) { stat ->
                QuickStatCard(stat = stat)
            }
        }
    }
}

@Composable
fun QuickStatCard(stat: QuickStat) {
    Card(
        modifier = Modifier.width(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                
                if (stat.change.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (stat.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = if (stat.isPositive) Color.Green else Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stat.change,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (stat.isPositive) Color.Green else Color.Red
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = stat.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BusinessAccountsSection(
    accounts: List<BusinessAccount>,
    onAccountClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Business Accounts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* View all accounts */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        accounts.forEach { account ->
            BusinessAccountCard(
                account = account,
                onClick = { onAccountClick(account.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BusinessAccountCard(
    account: BusinessAccount,
    onClick: () -> Unit
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "****${account.accountNumber.takeLast(4)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                BusinessAccountTypeChip(type = account.type)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "${account.currency} ${String.format("%,.2f", account.balance)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BusinessServicesSection(
    services: List<BusinessService>,
    onServiceClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Business Services",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(services) { service ->
                BusinessServiceCard(
                    service = service,
                    onClick = { onServiceClick(service.id) }
                )
            }
        }
    }
}

@Composable
fun BusinessServiceCard(
    service: BusinessService,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = service.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(service.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = null,
                    tint = service.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = service.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<BusinessTransaction>,
    onTransactionClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
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
        
        Spacer(modifier = Modifier.height(12.dp))
        
        transactions.take(5).forEach { transaction ->
            BusinessTransactionCard(
                transaction = transaction,
                onClick = { onTransactionClick(transaction.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BusinessTransactionCard(
    transaction: BusinessTransaction,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = getTransactionIcon(transaction.type),
                    contentDescription = null,
                    tint = getTransactionColor(transaction.type),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${transaction.category} • ${transaction.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}£${String.format("%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = getTransactionColor(transaction.type)
                )
                Text(
                    text = transaction.reference,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BusinessInsightsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Business Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Your business cash flow has improved by 15% this month. Consider investing in growth opportunities or building your emergency fund.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { /* View detailed insights */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Detailed Insights")
            }
        }
    }
}

@Composable
fun BusinessAccountTypeChip(type: BusinessAccountType) {
    val (color, text) = when (type) {
        BusinessAccountType.CURRENT -> Color.Blue to "Current"
        BusinessAccountType.SAVINGS -> Color.Green to "Savings"
        BusinessAccountType.CREDIT -> Color(0xFFFF9800) to "Credit"
        BusinessAccountType.LOAN -> Color.Red to "Loan"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun getTransactionIcon(type: TransactionType): ImageVector {
    return when (type) {
        TransactionType.INCOME -> Icons.Default.TrendingUp
        TransactionType.EXPENSE -> Icons.Default.TrendingDown
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
    }
}

fun getTransactionColor(type: TransactionType): Color {
    return when (type) {
        TransactionType.INCOME -> Color.Green
        TransactionType.EXPENSE -> Color.Red
        TransactionType.TRANSFER -> Color.Blue
    }
}

fun getDummyBusinessAccounts(): List<BusinessAccount> {
    return listOf(
        BusinessAccount(
            id = "BA001",
            name = "Business Current Account",
            accountNumber = "12345678",
            balance = 45750.25,
            currency = "£",
            type = BusinessAccountType.CURRENT
        ),
        BusinessAccount(
            id = "BA002",
            name = "Business Savings Account",
            accountNumber = "87654321",
            balance = 125000.00,
            currency = "£",
            type = BusinessAccountType.SAVINGS
        ),
        BusinessAccount(
            id = "BA003",
            name = "Business Credit Line",
            accountNumber = "11223344",
            balance = -5250.75,
            currency = "£",
            type = BusinessAccountType.CREDIT
        )
    )
}

fun getDummyBusinessTransactions(): List<BusinessTransaction> {
    return listOf(
        BusinessTransaction(
            id = "BT001",
            description = "Client Payment - ABC Corp",
            amount = 5500.00,
            date = "Today",
            type = TransactionType.INCOME,
            category = "Revenue",
            reference = "INV-2024-001"
        ),
        BusinessTransaction(
            id = "BT002",
            description = "Office Rent Payment",
            amount = 2500.00,
            date = "Yesterday",
            type = TransactionType.EXPENSE,
            category = "Operating Expenses",
            reference = "RENT-OCT-2024"
        ),
        BusinessTransaction(
            id = "BT003",
            description = "Supplier Payment - XYZ Ltd",
            amount = 1250.50,
            date = "2 days ago",
            type = TransactionType.EXPENSE,
            category = "Purchases",
            reference = "PO-2024-045"
        ),
        BusinessTransaction(
            id = "BT004",
            description = "Transfer to Savings",
            amount = 10000.00,
            date = "3 days ago",
            type = TransactionType.TRANSFER,
            category = "Internal Transfer",
            reference = "TRF-SAV-001"
        ),
        BusinessTransaction(
            id = "BT005",
            description = "Payroll Processing",
            amount = 15750.00,
            date = "1 week ago",
            type = TransactionType.EXPENSE,
            category = "Payroll",
            reference = "PAY-OCT-2024"
        )
    )
}

fun getDummyBusinessServices(): List<BusinessService> {
    return listOf(
        BusinessService(
            id = "BS001",
            name = "Invoices",
            description = "Manage invoices",
            icon = Icons.Default.Receipt,
            color = Color(0xFF2196F3)
        ),
        BusinessService(
            id = "BS002",
            name = "Payroll",
            description = "Process payroll",
            icon = Icons.Default.People,
            color = Color(0xFF4CAF50)
        ),
        BusinessService(
            id = "BS003",
            name = "Expenses",
            description = "Track expenses",
            icon = Icons.Default.Receipt,
            color = Color(0xFFFF9800)
        ),
        BusinessService(
            id = "BS004",
            name = "Reports",
            description = "Financial reports",
            icon = Icons.Default.Assessment,
            color = Color(0xFF9C27B0)
        ),
        BusinessService(
            id = "BS005",
            name = "Tax",
            description = "Tax management",
            icon = Icons.Default.AccountBalance,
            color = Color(0xFFE91E63)
        ),
        BusinessService(
            id = "BS006",
            name = "Loans",
            description = "Business loans",
            icon = Icons.Default.CreditCard,
            color = Color(0xFF607D8B)
        )
    )
}

fun getDummyQuickStats(): List<QuickStat> {
    return listOf(
        QuickStat(
            title = "Monthly Revenue",
            value = "£45,750",
            change = "+12%",
            isPositive = true,
            icon = Icons.Default.TrendingUp
        ),
        QuickStat(
            title = "Total Expenses",
            value = "£28,500",
            change = "-5%",
            isPositive = true,
            icon = Icons.Default.Receipt
        ),
        QuickStat(
            title = "Net Profit",
            value = "£17,250",
            change = "+18%",
            isPositive = true,
            icon = Icons.Default.AccountBalance
        ),
        QuickStat(
            title = "Cash Flow",
            value = "£125,000",
            change = "+15%",
            isPositive = true,
            icon = Icons.Default.Savings
        )
    )
}
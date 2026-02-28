package com.avinashpatil.app.monzobank.presentation.ui.transactions

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

data class TransactionFilter(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onSearchClick: () -> Unit = {},
    onExportClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("all") }
    var selectedPeriod by remember { mutableStateOf("month") }
    
    val filters = remember {
        listOf(
            TransactionFilter("all", "All", Icons.Default.List),
            TransactionFilter("income", "Income", Icons.Default.TrendingUp),
            TransactionFilter("expenses", "Expenses", Icons.Default.TrendingDown),
            TransactionFilter("transfers", "Transfers", Icons.Default.SwapHoriz),
            TransactionFilter("subscriptions", "Subscriptions", Icons.Default.Subscriptions)
        )
    }

    val periods = listOf(
        "week" to "This Week",
        "month" to "This Month",
        "quarter" to "This Quarter",
        "year" to "This Year",
        "all" to "All Time"
    )

    // Mock transaction data
    val allTransactions = remember {
        listOf(
            Transaction(
                id = "1",
                accountId = "acc_1",
                amount = -45.50,
                description = "Tesco Express",
                category = "Groceries",
                date = Date(),
                merchant = "Tesco",
                type = "debit"
            ),
            Transaction(
                id = "2",
                accountId = "acc_1",
                amount = 1200.00,
                description = "Salary Payment",
                category = "Income",
                date = Date(System.currentTimeMillis() - 86400000),
                merchant = "Employer Ltd",
                type = "credit"
            ),
            Transaction(
                id = "3",
                accountId = "acc_1",
                amount = -12.99,
                description = "Netflix Subscription",
                category = "Entertainment",
                date = Date(System.currentTimeMillis() - 172800000),
                merchant = "Netflix",
                type = "debit"
            ),
            Transaction(
                id = "4",
                accountId = "acc_1",
                amount = -89.99,
                description = "Amazon Purchase",
                category = "Shopping",
                date = Date(System.currentTimeMillis() - 259200000),
                merchant = "Amazon",
                type = "debit"
            ),
            Transaction(
                id = "5",
                accountId = "acc_1",
                amount = -25.00,
                description = "Uber Ride",
                category = "Transport",
                date = Date(System.currentTimeMillis() - 345600000),
                merchant = "Uber",
                type = "debit"
            ),
            Transaction(
                id = "6",
                accountId = "acc_1",
                amount = 500.00,
                description = "Freelance Payment",
                category = "Income",
                date = Date(System.currentTimeMillis() - 432000000),
                merchant = "Client ABC",
                type = "credit"
            )
        )
    }

    val filteredTransactions = remember(selectedFilter, allTransactions) {
        when (selectedFilter) {
            "income" -> allTransactions.filter { it.amount > 0 }
            "expenses" -> allTransactions.filter { it.amount < 0 }
            "transfers" -> allTransactions.filter { it.category == "Transfer" }
            "subscriptions" -> allTransactions.filter { it.description.contains("Subscription", ignoreCase = true) }
            else -> allTransactions
        }
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    val dateFormatter = SimpleDateFormat("MMM dd", Locale.UK)
    val dayFormatter = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.UK)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Transaction History") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = onExportClick) {
                    Icon(Icons.Default.Download, contentDescription = "Export")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
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
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Transaction Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SummaryItem(
                                label = "Total In",
                                amount = filteredTransactions.filter { it.amount > 0 }.sumOf { it.amount },
                                isPositive = true
                            )
                            SummaryItem(
                                label = "Total Out",
                                amount = Math.abs(filteredTransactions.filter { it.amount < 0 }.sumOf { it.amount }),
                                isPositive = false
                            )
                            SummaryItem(
                                label = "Net",
                                amount = filteredTransactions.sumOf { it.amount },
                                isPositive = filteredTransactions.sumOf { it.amount } >= 0
                            )
                        }
                    }
                }
            }

            // Period Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Time Period",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(periods) { (id, name) ->
                                FilterChip(
                                    onClick = { selectedPeriod = id },
                                    label = { Text(name) },
                                    selected = selectedPeriod == id
                                )
                            }
                        }
                    }
                }
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filters) { filter ->
                        FilterChip(
                            onClick = { selectedFilter = filter.id },
                            label = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = filter.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(filter.name)
                                }
                            },
                            selected = selectedFilter == filter.id
                        )
                    }
                }
            }

            // Transactions List
            if (filteredTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
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
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No transactions found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Try adjusting your filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                // Group transactions by date
                val groupedTransactions = filteredTransactions.groupBy { transaction ->
                    dayFormatter.format(transaction.date)
                }

                groupedTransactions.forEach { (date, transactions) ->
                    item {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(transactions) { transaction ->
                        TransactionHistoryItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: Double,
    isPositive: Boolean
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = currencyFormatter.format(amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) Color.Green else MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun TransactionHistoryItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.UK)
    val isDebit = transaction.amount < 0
    
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
                    tint = if (isDebit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = " • ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = timeFormatter.format(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = currencyFormatter.format(Math.abs(transaction.amount)),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDebit) MaterialTheme.colorScheme.onSurface else Color.Green
                )
                if (isDebit) {
                    Text(
                        text = "Debit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = "Credit",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Green.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
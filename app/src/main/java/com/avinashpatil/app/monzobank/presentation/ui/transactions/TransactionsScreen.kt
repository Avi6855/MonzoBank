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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avinashpatil.app.monzobank.data.local.entity.TransactionStatus
import com.avinashpatil.app.monzobank.data.local.entity.TransactionType
import com.avinashpatil.app.monzobank.domain.model.Transaction
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import com.avinashpatil.app.monzobank.presentation.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    accountId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTransactionDetail: (String) -> Unit,
    transactionViewModel: TransactionViewModel? = null
) {
    // Mock data for demonstration
    // val transactionUiState = remember { TransactionUiState(transactions = emptyList(), isLoading = false) }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    
    // LaunchedEffect(accountId, searchQuery, selectedCategory) {
    //     transactionViewModel?.loadTransactions(
    //         accountId = accountId,
    //         searchQuery = searchQuery.takeIf { it.isNotBlank() },
    //         category = selectedCategory
    //     )
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
                    text = "Transactions",
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
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = Icons.Default.Search, // Using Search icon instead of FilterList
                        contentDescription = "Filters",
                        tint = if (showFilters) MonzoCoralPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            // Filters Section
            if (showFilters) {
                FiltersSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onClearFilters = {
                        selectedCategory = null
                        searchQuery = ""
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Transaction Summary - using mock data
            if (false) { // Mock: no transactions to show summary
                TransactionSummary(
                    totalSpending = java.math.BigDecimal.ZERO, // transactionViewModel?.getTotalSpending() ?: BigDecimal.ZERO,
                    totalIncome = java.math.BigDecimal.ZERO, // transactionViewModel?.getTotalIncome() ?: BigDecimal.ZERO,
                    transactionCount = 0, // Mock: no transactions
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Transactions List - using mock data
            if (false) { // Mock: not loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MonzoCoralPrimary)
                }
            } else if (emptyList<Transaction>().isEmpty()) { // Mock: empty transactions
                EmptyTransactionsState(
                    hasFilters = searchQuery.isNotBlank() || selectedCategory != null,
                    onClearFilters = {
                        selectedCategory = null
                        searchQuery = ""
                    }
                )
            } else {
                TransactionsList(
                    transactions = emptyList(), // Mock: empty transactions
                    onTransactionClick = onNavigateToTransactionDetail,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    
    // Error handling - commented out for now
    // transactionUiState.error?.let { errorMessage ->
    //     LaunchedEffect(errorMessage) {
    //         // Show snackbar or handle error
    //         // transactionViewModel?.clearError()
    //     }
    // }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search transactions...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MonzoCoralPrimary,
            focusedLeadingIconColor = MonzoCoralPrimary
        )
    )
}

@Composable
fun FiltersSection(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Groceries", "Dining", "Transport", "Shopping", 
        "Entertainment", "Healthcare", "Income", "Transfers", "General"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                Text(
                    text = "Filter by Category",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                TextButton(onClick = onClearFilters) {
                    Text(
                        text = "Clear All",
                        color = MonzoCoralPrimary
                    )
                }
            }
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = {
                            onCategorySelected(
                                if (selectedCategory == category) null else category
                            )
                        },
                        label = { Text(category) },
                        selected = selectedCategory == category,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MonzoCoralPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionSummary(
    totalSpending: java.math.BigDecimal,
    totalIncome: java.math.BigDecimal,
    transactionCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "Spent",
                amount = totalSpending,
                isPositive = false,
                modifier = Modifier.weight(1f)
            )
            
            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            SummaryItem(
                label = "Received",
                amount = totalIncome,
                isPositive = true,
                modifier = Modifier.weight(1f)
            )
            
            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = transactionCount.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    amount: java.math.BigDecimal,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "£${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Group transactions by date
        val groupedTransactions = transactions.groupBy {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.transactionDate)
        }
        
        groupedTransactions.forEach { (date, dayTransactions) ->
            item {
                DateHeader(
                    date = dayTransactions.first().transactionDate,
                    transactionCount = dayTransactions.size,
                    totalAmount = dayTransactions.sumOf { 
                        if (it.transactionType == TransactionType.DEBIT) -it.amount else it.amount 
                    }
                )
            }
            
            items(dayTransactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }
    }
}

@Composable
fun DateHeader(
    date: Date,
    transactionCount: Int,
    totalAmount: java.math.BigDecimal
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())
    val isToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date) == 
                  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isToday) "Today" else dateFormatter.format(date),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = if (totalAmount >= java.math.BigDecimal.ZERO) 
                    "+£${String.format("%.2f", totalAmount)}" 
                else 
                    "-£${String.format("%.2f", totalAmount.abs())}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (totalAmount >= java.math.BigDecimal.ZERO) 
                    Color(0xFF4CAF50) 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "$transactionCount ${if (transactionCount == 1) "transaction" else "transactions"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val amountColor = if (transaction.type == TransactionType.CREDIT) {
        Color(0xFF4CAF50)
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                        text = transaction.category?.name?.take(1)?.uppercase() ?: "T",
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
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeFormatter.format(transaction.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (transaction.category != null) {
                        Text(
                            text = " • ${transaction.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (transaction.status != TransactionStatus.COMPLETED) {
                        val statusColor = when (transaction.status) {
                            TransactionStatus.PENDING -> Color(0xFFFF9500)
                            TransactionStatus.FAILED, TransactionStatus.DECLINED -> Color(0xFFFF3B30)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Text(
                            text = " • ${transaction.status.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor
                        )
                    }
                }
            }
            
            // Amount
            val formattedAmount = "£${String.format("%.2f", transaction.absoluteAmount)}"
            Text(
                text = if (transaction.type == TransactionType.CREDIT) {
                    "+$formattedAmount"
                } else {
                    "-$formattedAmount"
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}

@Composable
fun EmptyTransactionsState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasFilters) Icons.Default.Search else Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (hasFilters) "No transactions found" else "No transactions yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = if (hasFilters) {
                "Try adjusting your search or filters to find what you're looking for"
            } else {
                "Your transactions will appear here once you start spending"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        
        if (hasFilters) {
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Text("Clear Filters")
            }
        }
    }
}
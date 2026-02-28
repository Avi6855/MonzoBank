package com.avinashpatil.app.monzobank.presentation.ui.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

data class BudgetCategory(
    val id: String,
    val name: String,
    val budgetAmount: Double,
    val spentAmount: Double,
    val color: Color,
    val icon: String
)

data class SpendingInsight(
    val title: String,
    val description: String,
    val amount: Double,
    val trend: String // "up", "down", "stable"
)

// Main Budget Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (BudgetCategory) -> Unit,
    onCreateBudgetClick: () -> Unit
) {
    val budgetCategories = remember {
        listOf(
            BudgetCategory("1", "Groceries", 400.0, 285.50, Color.Green, "🛒"),
            BudgetCategory("2", "Transport", 150.0, 120.30, Color.Blue, "🚗"),
            BudgetCategory("3", "Entertainment", 200.0, 180.75, Color.Magenta, "🎬"),
            BudgetCategory("4", "Dining Out", 300.0, 350.20, Color.Red, "🍽️")
        )
    }
    
    val insights = remember {
        listOf(
            SpendingInsight("Dining Out", "You've spent 17% more than budgeted", 50.20, "up"),
            SpendingInsight("Groceries", "Great job! You're under budget", -114.50, "down"),
            SpendingInsight("Transport", "On track with your budget", 0.0, "stable")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Budget & Analytics") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onCreateBudgetClick) {
                    Icon(Icons.Default.Add, contentDescription = "Create Budget")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Monthly Overview
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "This Month",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val totalBudget = budgetCategories.sumOf { it.budgetAmount }
                        val totalSpent = budgetCategories.sumOf { it.spentAmount }
                        val remaining = totalBudget - totalSpent
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Budgeted")
                                Text(
                                    "£${String.format("%.2f", totalBudget)}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Spent")
                                Text(
                                    "£${String.format("%.2f", totalSpent)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalSpent > totalBudget) Color.Red else Color.Green
                                )
                            }
                            Column {
                                Text("Remaining")
                                Text(
                                    "£${String.format("%.2f", remaining)}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (remaining < 0) Color.Red else Color.Green
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        LinearProgressIndicator(
                            progress = (totalSpent / totalBudget).toFloat().coerceAtMost(1f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Budget Categories
            item {
                Text(
                    "Budget Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(budgetCategories) { category ->
                BudgetCategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category) }
                )
            }

            // Spending Insights
            item {
                Text(
                    "Spending Insights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(insights) { insight ->
                InsightCard(insight = insight)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCategoryCard(
    category: BudgetCategory,
    onClick: () -> Unit
) {
    val progress = (category.spentAmount / category.budgetAmount).toFloat()
    val isOverBudget = category.spentAmount > category.budgetAmount
    
    Card(
        onClick = onClick,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        category.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "£${String.format("%.2f")} of £${String.format("%.2f", category.budgetAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isOverBudget) Color.Red else Color.Gray
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        if (isOverBudget) "Over Budget" else "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isOverBudget) Color.Red else Color.Green
                    )
                    if (isOverBudget) {
                        Text(
                            "£${String.format("%.2f", category.spentAmount - category.budgetAmount)} over",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress.coerceAtMost(1f),
                modifier = Modifier.fillMaxWidth(),
                color = if (isOverBudget) Color.Red else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InsightCard(insight: SpendingInsight) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    insight.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    insight.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (insight.amount != 0.0) {
                    Text(
                        "£${String.format("%.2f", kotlin.math.abs(insight.amount))}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (insight.trend) {
                            "up" -> Color.Red
                            "down" -> Color.Green
                            else -> Color.Gray
                        }
                    )
                }
                
                Icon(
                    imageVector = when (insight.trend) {
                        "up" -> Icons.Default.TrendingUp
                        "down" -> Icons.Default.TrendingDown
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    tint = when (insight.trend) {
                        "up" -> Color.Red
                        "down" -> Color.Green
                        else -> Color.Gray
                    }
                )
            }
        }
    }
}

// Create Budget Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(
    onBackClick: () -> Unit,
    onBudgetCreated: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var budgetAmount by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("💰") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val iconOptions = listOf("💰", "🛒", "🚗", "🍽️", "🎬", "🏠", "👕", "💊")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Create Budget") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = budgetAmount,
                onValueChange = { budgetAmount = it },
                label = { Text("Monthly Budget (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Choose an icon",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                iconOptions.forEach { icon ->
                    FilterChip(
                        onClick = { selectedIcon = icon },
                        label = { Text(icon, style = MaterialTheme.typography.headlineSmall) },
                        selected = selectedIcon == icon
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(1500)
                        isLoading = false
                        onBudgetCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && categoryName.isNotBlank() && budgetAmount.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Create Budget")
                }
            }
        }
    }
}
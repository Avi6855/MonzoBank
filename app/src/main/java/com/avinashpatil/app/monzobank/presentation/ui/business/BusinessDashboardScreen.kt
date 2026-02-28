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

data class DashboardMetric(
    val title: String,
    val value: String,
    val change: String,
    val changePercentage: String,
    val isPositive: Boolean,
    val icon: ImageVector,
    val color: Color
)

data class RecentActivity(
    val id: String,
    val title: String,
    val description: String,
    val amount: String,
    val time: String,
    val type: ActivityType,
    val icon: ImageVector
)

data class BusinessGoal(
    val id: String,
    val title: String,
    val current: Double,
    val target: Double,
    val progress: Float,
    val deadline: String,
    val category: String
)

data class Alert(
    val id: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val timestamp: String,
    val isRead: Boolean
)

enum class ActivityType {
    PAYMENT_RECEIVED, PAYMENT_SENT, INVOICE_CREATED, EXPENSE_RECORDED, TRANSFER
}

enum class AlertType {
    INFO, WARNING, ERROR, SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessDashboardScreen(
    onBackClick: () -> Unit,
    onMetricClick: (String) -> Unit,
    onActivityClick: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onAlertClick: (String) -> Unit
) {
    val metrics = remember { getDummyDashboardMetrics() }
    val activities = remember { getDummyRecentActivities() }
    val goals = remember { getDummyBusinessGoals() }
    val alerts = remember { getDummyAlerts() }
    var selectedPeriod by remember { mutableStateOf("This Month") }
    val periods = listOf("Today", "This Week", "This Month", "This Quarter", "This Year")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export data */ }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                    IconButton(onClick = { /* Refresh */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
            // Period Selector
            item {
                PeriodSelectorSection(
                    periods = periods,
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { selectedPeriod = it }
                )
            }

            // Key Metrics
            item {
                KeyMetricsSection(
                    metrics = metrics,
                    onMetricClick = onMetricClick
                )
            }

            // Alerts Section
            if (alerts.isNotEmpty()) {
                item {
                    AlertsSection(
                        alerts = alerts,
                        onAlertClick = onAlertClick
                    )
                }
            }

            // Business Goals
            item {
                BusinessGoalsSection(
                    goals = goals,
                    onGoalClick = onGoalClick
                )
            }

            // Recent Activity
            item {
                RecentActivitySection(
                    activities = activities,
                    onActivityClick = onActivityClick
                )
            }

            // Quick Actions
            item {
                QuickActionsSection()
            }
        }
    }
}

@Composable
fun PeriodSelectorSection(
    periods: List<String>,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(periods) { period ->
            FilterChip(
                onClick = { onPeriodSelected(period) },
                label = { Text(period) },
                selected = period == selectedPeriod
            )
        }
    }
}

@Composable
fun KeyMetricsSection(
    metrics: List<DashboardMetric>,
    onMetricClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Key Metrics",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(metrics) { metric ->
                DashboardMetricCard(
                    metric = metric,
                    onClick = { onMetricClick(metric.title) }
                )
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    metric: DashboardMetric,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = metric.color.copy(alpha = 0.1f)
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
                    imageVector = metric.icon,
                    contentDescription = null,
                    tint = metric.color,
                    modifier = Modifier.size(24.dp)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (metric.isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (metric.isPositive) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = metric.changePercentage,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (metric.isPositive) Color.Green else Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = metric.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = metric.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = metric.change,
                style = MaterialTheme.typography.bodySmall,
                color = if (metric.isPositive) Color.Green else Color.Red
            )
        }
    }
}

@Composable
fun AlertsSection(
    alerts: List<Alert>,
    onAlertClick: (String) -> Unit
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
                text = "Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* View all alerts */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        alerts.take(3).forEach { alert ->
            AlertCard(
                alert = alert,
                onClick = { onAlertClick(alert.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun AlertCard(
    alert: Alert,
    onClick: () -> Unit
) {
    val alertColor = when (alert.type) {
        AlertType.INFO -> Color.Blue
        AlertType.WARNING -> Color(0xFFFF9800)
        AlertType.ERROR -> Color.Red
        AlertType.SUCCESS -> Color.Green
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = alertColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (alert.type) {
                    AlertType.INFO -> Icons.Default.Info
                    AlertType.WARNING -> Icons.Default.Warning
                    AlertType.ERROR -> Icons.Default.Error
                    AlertType.SUCCESS -> Icons.Default.CheckCircle
                },
                contentDescription = null,
                tint = alertColor,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = alert.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = alert.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            if (!alert.isRead) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(alertColor)
                )
            }
        }
    }
}

@Composable
fun BusinessGoalsSection(
    goals: List<BusinessGoal>,
    onGoalClick: (String) -> Unit
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
                text = "Business Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* Manage goals */ }) {
                Text("Manage")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        goals.take(3).forEach { goal ->
            BusinessGoalCard(
                goal = goal,
                onClick = { onGoalClick(goal.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun BusinessGoalCard(
    goal: BusinessGoal,
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
                        text = goal.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = goal.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "${(goal.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = goal.progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "£${String.format("%,.0f", goal.current)} / £${String.format("%,.0f", goal.target)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Due: ${goal.deadline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RecentActivitySection(
    activities: List<RecentActivity>,
    onActivityClick: (String) -> Unit
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
                text = "Recent Activity",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* View all activity */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        activities.take(5).forEach { activity ->
            RecentActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecentActivityCard(
    activity: RecentActivity,
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
                imageVector = activity.icon,
                contentDescription = null,
                tint = getActivityColor(activity.type),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = activity.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (activity.amount.isNotEmpty()) {
                    Text(
                        text = activity.amount,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = getActivityColor(activity.type)
                    )
                }
                Text(
                    text = activity.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun QuickActionsSection() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(getQuickActions()) { action ->
                QuickActionCard(
                    title = action.first,
                    icon = action.second,
                    onClick = { /* Handle action */ }
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

fun getActivityColor(type: ActivityType): Color {
    return when (type) {
        ActivityType.PAYMENT_RECEIVED -> Color.Green
        ActivityType.PAYMENT_SENT -> Color.Red
        ActivityType.INVOICE_CREATED -> Color.Blue
        ActivityType.EXPENSE_RECORDED -> Color(0xFFFF9800)
        ActivityType.TRANSFER -> Color(0xFF9C27B0)
    }
}

fun getQuickActions(): List<Pair<String, ImageVector>> {
    return listOf(
        "Invoice" to Icons.Default.Receipt,
        "Payment" to Icons.Default.Payment,
        "Expense" to Icons.Default.ShoppingCart,
        "Transfer" to Icons.Default.SwapHoriz,
        "Report" to Icons.Default.Assessment
    )
}

fun getDummyDashboardMetrics(): List<DashboardMetric> {
    return listOf(
        DashboardMetric(
            title = "Total Revenue",
            value = "£125,750",
            change = "+£15,250 from last month",
            changePercentage = "+13.8%",
            isPositive = true,
            icon = Icons.Default.TrendingUp,
            color = Color(0xFF4CAF50)
        ),
        DashboardMetric(
            title = "Net Profit",
            value = "£45,250",
            change = "+£8,750 from last month",
            changePercentage = "+24.0%",
            isPositive = true,
            icon = Icons.Default.AccountBalance,
            color = Color(0xFF2196F3)
        ),
        DashboardMetric(
            title = "Total Expenses",
            value = "£80,500",
            change = "+£6,500 from last month",
            changePercentage = "+8.8%",
            isPositive = false,
            icon = Icons.Default.Receipt,
            color = Color(0xFFFF9800)
        ),
        DashboardMetric(
            title = "Cash Flow",
            value = "£195,000",
            change = "+£25,000 from last month",
            changePercentage = "+14.7%",
            isPositive = true,
            icon = Icons.Default.Savings,
            color = Color(0xFF9C27B0)
        )
    )
}

fun getDummyRecentActivities(): List<RecentActivity> {
    return listOf(
        RecentActivity(
            id = "RA001",
            title = "Payment Received",
            description = "ABC Corp - Invoice #INV-2024-001",
            amount = "+£5,500",
            time = "2 hours ago",
            type = ActivityType.PAYMENT_RECEIVED,
            icon = Icons.Default.TrendingUp
        ),
        RecentActivity(
            id = "RA002",
            title = "Invoice Created",
            description = "XYZ Ltd - Project consultation",
            amount = "£3,250",
            time = "4 hours ago",
            type = ActivityType.INVOICE_CREATED,
            icon = Icons.Default.Receipt
        ),
        RecentActivity(
            id = "RA003",
            title = "Expense Recorded",
            description = "Office supplies - Staples",
            amount = "-£125",
            time = "6 hours ago",
            type = ActivityType.EXPENSE_RECORDED,
            icon = Icons.Default.ShoppingCart
        ),
        RecentActivity(
            id = "RA004",
            title = "Payment Sent",
            description = "Supplier payment - DEF Industries",
            amount = "-£2,750",
            time = "1 day ago",
            type = ActivityType.PAYMENT_SENT,
            icon = Icons.Default.TrendingDown
        ),
        RecentActivity(
            id = "RA005",
            title = "Transfer Completed",
            description = "To Business Savings Account",
            amount = "£10,000",
            time = "2 days ago",
            type = ActivityType.TRANSFER,
            icon = Icons.Default.SwapHoriz
        )
    )
}

fun getDummyBusinessGoals(): List<BusinessGoal> {
    return listOf(
        BusinessGoal(
            id = "BG001",
            title = "Quarterly Revenue Target",
            current = 125750.0,
            target = 150000.0,
            progress = 0.84f,
            deadline = "Dec 31, 2024",
            category = "Revenue"
        ),
        BusinessGoal(
            id = "BG002",
            title = "Emergency Fund",
            current = 45000.0,
            target = 60000.0,
            progress = 0.75f,
            deadline = "Mar 31, 2025",
            category = "Savings"
        ),
        BusinessGoal(
            id = "BG003",
            title = "Equipment Upgrade Fund",
            current = 15000.0,
            target = 25000.0,
            progress = 0.60f,
            deadline = "Jun 30, 2025",
            category = "Investment"
        )
    )
}

fun getDummyAlerts(): List<Alert> {
    return listOf(
        Alert(
            id = "AL001",
            title = "Low Cash Flow Warning",
            message = "Your cash flow is below the recommended threshold. Consider reviewing upcoming expenses.",
            type = AlertType.WARNING,
            timestamp = "2 hours ago",
            isRead = false
        ),
        Alert(
            id = "AL002",
            title = "Invoice Overdue",
            message = "Invoice #INV-2024-045 from XYZ Corp is 15 days overdue.",
            type = AlertType.ERROR,
            timestamp = "1 day ago",
            isRead = false
        ),
        Alert(
            id = "AL003",
            title = "Goal Achievement",
            message = "Congratulations! You've reached 84% of your quarterly revenue target.",
            type = AlertType.SUCCESS,
            timestamp = "3 days ago",
            isRead = true
        )
    )
}
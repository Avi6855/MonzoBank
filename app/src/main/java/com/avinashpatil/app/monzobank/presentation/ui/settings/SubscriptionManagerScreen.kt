package com.avinashpatil.app.monzobank.presentation.ui.settings

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

data class Subscription(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val billingCycle: BillingCycle,
    val status: SubscriptionStatus,
    val nextBillingDate: String,
    val startDate: String,
    val category: SubscriptionCategory,
    val icon: ImageVector,
    val color: Color,
    val features: List<String>,
    val canCancel: Boolean = true,
    val canPause: Boolean = false
)

data class SubscriptionPlan(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val billingCycle: BillingCycle,
    val features: List<String>,
    val isPopular: Boolean = false,
    val isCurrentPlan: Boolean = false,
    val icon: ImageVector,
    val color: Color
)

data class BillingHistory(
    val id: String,
    val subscriptionName: String,
    val amount: Double,
    val billingDate: String,
    val status: BillingStatus,
    val invoiceUrl: String?
)

data class SubscriptionUsage(
    val subscriptionId: String,
    val usageType: String,
    val currentUsage: Int,
    val limit: Int,
    val resetDate: String
)

enum class SubscriptionStatus {
    ACTIVE, PAUSED, CANCELLED, EXPIRED, PENDING
}

enum class BillingCycle {
    MONTHLY, YEARLY, WEEKLY
}

enum class SubscriptionCategory {
    BANKING, ENTERTAINMENT, PRODUCTIVITY, LIFESTYLE, FINANCE
}

enum class BillingStatus {
    PAID, PENDING, FAILED, REFUNDED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionManagerScreen(
    onBackClick: () -> Unit,
    onSubscriptionClick: (String) -> Unit,
    onUpgradeClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    onPauseClick: (String) -> Unit,
    onBrowsePlansClick: () -> Unit
) {
    val activeSubscriptions = remember { getDummyActiveSubscriptions() }
    val availablePlans = remember { getDummyAvailablePlans() }
    val billingHistory = remember { getDummyBillingHistory() }
    val usageData = remember { getDummyUsageData() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Active", "Plans", "Billing", "Usage")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription Manager") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Subscription settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Default.Help, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onBrowsePlansClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Browse Plans")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subscription Summary
            SubscriptionSummaryCard(
                totalSubscriptions = activeSubscriptions.size,
                monthlySpend = activeSubscriptions.filter { it.billingCycle == BillingCycle.MONTHLY }.sumOf { it.price },
                yearlySpend = activeSubscriptions.filter { it.billingCycle == BillingCycle.YEARLY }.sumOf { it.price }
            )

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
                0 -> ActiveSubscriptionsContent(
                    subscriptions = activeSubscriptions,
                    onSubscriptionClick = onSubscriptionClick,
                    onCancelClick = onCancelClick,
                    onPauseClick = onPauseClick
                )
                1 -> AvailablePlansContent(
                    plans = availablePlans,
                    onPlanClick = onUpgradeClick
                )
                2 -> BillingHistoryContent(
                    billingHistory = billingHistory,
                    onInvoiceClick = { /* Download invoice */ }
                )
                3 -> UsageContent(
                    usageData = usageData,
                    subscriptions = activeSubscriptions
                )
            }
        }
    }
}

@Composable
fun ActiveSubscriptionsContent(
    subscriptions: List<Subscription>,
    onSubscriptionClick: (String) -> Unit,
    onCancelClick: (String) -> Unit,
    onPauseClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Active Subscriptions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (subscriptions.isNotEmpty()) {
            // Group by category
            val subscriptionsByCategory = subscriptions.groupBy { it.category }
            subscriptionsByCategory.forEach { (category, categorySubscriptions) ->
                item {
                    Text(
                        text = getCategoryName(category),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                items(categorySubscriptions) { subscription ->
                    SubscriptionCard(
                        subscription = subscription,
                        onClick = { onSubscriptionClick(subscription.id) },
                        onCancelClick = { onCancelClick(subscription.id) },
                        onPauseClick = { onPauseClick(subscription.id) }
                    )
                }
            }
        } else {
            item {
                EmptySubscriptionsState()
            }
        }

        // Quick Actions
        item {
            SubscriptionQuickActions(
                onManagePaymentClick = { /* Manage payment methods */ },
                onNotificationSettingsClick = { /* Notification settings */ },
                onExportDataClick = { /* Export subscription data */ }
            )
        }
    }
}

@Composable
fun AvailablePlansContent(
    plans: List<SubscriptionPlan>,
    onPlanClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Available Plans",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Upgrade your banking experience with premium features",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(plans) { plan ->
            SubscriptionPlanCard(
                plan = plan,
                onClick = { onPlanClick(plan.id) }
            )
        }

        // Plan Comparison
        item {
            PlanComparisonCard()
        }
    }
}

@Composable
fun BillingHistoryContent(
    billingHistory: List<BillingHistory>,
    onInvoiceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Billing History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (billingHistory.isNotEmpty()) {
            items(billingHistory) { billing ->
                BillingHistoryCard(
                    billing = billing,
                    onInvoiceClick = { onInvoiceClick(billing.id) }
                )
            }
        } else {
            item {
                EmptyBillingHistoryState()
            }
        }

        // Billing Summary
        item {
            BillingSummaryCard(
                totalThisMonth = billingHistory.filter { it.status == BillingStatus.PAID }.sumOf { it.amount },
                totalThisYear = billingHistory.sumOf { it.amount }
            )
        }
    }
}

@Composable
fun UsageContent(
    usageData: List<SubscriptionUsage>,
    subscriptions: List<Subscription>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Usage & Limits",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (usageData.isNotEmpty()) {
            items(usageData) { usage ->
                val subscription = subscriptions.find { it.id == usage.subscriptionId }
                if (subscription != null) {
                    UsageCard(
                        usage = usage,
                        subscription = subscription
                    )
                }
            }
        } else {
            item {
                EmptyUsageState()
            }
        }

        // Usage Tips
        item {
            UsageTipsCard()
        }
    }
}

@Composable
fun SubscriptionSummaryCard(
    totalSubscriptions: Int,
    monthlySpend: Double,
    yearlySpend: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                    imageVector = Icons.Default.Subscriptions,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Subscription Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Manage all your subscriptions in one place",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Active",
                    value = totalSubscriptions.toString(),
                    icon = Icons.Default.CheckCircle
                )
                SummaryItem(
                    title = "Monthly",
                    value = "£${String.format("%.2f", monthlySpend)}",
                    icon = Icons.Default.CalendarMonth
                )
                SummaryItem(
                    title = "Yearly",
                    value = "£${String.format("%.2f", yearlySpend)}",
                    icon = Icons.Default.CalendarToday
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SubscriptionCard(
    subscription: Subscription,
    onClick: () -> Unit,
    onCancelClick: () -> Unit,
    onPauseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = subscription.color.copy(alpha = 0.1f)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(subscription.color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = subscription.icon,
                            contentDescription = null,
                            tint = subscription.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = subscription.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = subscription.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                SubscriptionStatusChip(status = subscription.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "£${String.format("%.2f", subscription.price)}/${getBillingCycleText(subscription.billingCycle)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = subscription.color
                    )
                    Text(
                        text = "Next billing: ${subscription.nextBillingDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subscription.canPause) {
                        OutlinedButton(
                            onClick = onPauseClick,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Pause", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    
                    if (subscription.canCancel) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Red
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Cancel", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            
            if (subscription.features.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Features: ${subscription.features.take(3).joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SubscriptionPlanCard(
    plan: SubscriptionPlan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isPopular) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = plan.icon,
                        contentDescription = null,
                        tint = plan.color,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = plan.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            if (plan.isPopular) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFFF9800).copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Popular",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFFF9800),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            if (plan.isCurrentPlan) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color.Green.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Current",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Green,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        
                        Text(
                            text = plan.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "£${String.format("%.2f", plan.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = plan.color
                    )
                    Text(
                        text = "per ${getBillingCycleText(plan.billingCycle)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Features:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            plan.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !plan.isCurrentPlan
            ) {
                Text(
                    if (plan.isCurrentPlan) "Current Plan" else "Select Plan"
                )
            }
        }
    }
}

@Composable
fun BillingHistoryCard(
    billing: BillingHistory,
    onInvoiceClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = billing.subscriptionName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = billing.billingDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "£${String.format("%.2f", billing.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                BillingStatusChip(status = billing.status)
            }
            
            if (billing.invoiceUrl != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onInvoiceClick) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download Invoice",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun UsageCard(
    usage: SubscriptionUsage,
    subscription: Subscription
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = subscription.icon,
                        contentDescription = null,
                        tint = subscription.color,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = subscription.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = usage.usageType,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "${usage.currentUsage}/${usage.limit}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = subscription.color
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = usage.currentUsage.toFloat() / usage.limit.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = subscription.color
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Resets on ${usage.resetDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SubscriptionQuickActions(
    onManagePaymentClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onExportDataClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SubscriptionQuickActionButton(
                    icon = Icons.Default.Payment,
                    label = "Payment Methods",
                    onClick = onManagePaymentClick
                )
                SubscriptionQuickActionButton(
                    icon = Icons.Default.Notifications,
                    label = "Notifications",
                    onClick = onNotificationSettingsClick
                )
                SubscriptionQuickActionButton(
                    icon = Icons.Default.Download,
                    label = "Export Data",
                    onClick = onExportDataClick
                )
            }
        }
    }
}

@Composable
fun SubscriptionQuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PlanComparisonCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Plan Comparison",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Compare features across different subscription plans to find the best fit for your needs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { /* Open comparison */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compare All Plans")
            }
        }
    }
}

@Composable
fun BillingSummaryCard(
    totalThisMonth: Double,
    totalThisYear: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Billing Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "This Month",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "£${String.format("%.2f", totalThisMonth)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "This Year",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "£${String.format("%.2f", totalThisYear)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun UsageTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Usage Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val tips = listOf(
                "Monitor your usage regularly to avoid overage charges",
                "Set up notifications when you reach 80% of your limit",
                "Consider upgrading if you consistently hit limits",
                "Review unused subscriptions monthly"
            )
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun SubscriptionStatusChip(status: SubscriptionStatus) {
    val (color, text) = when (status) {
        SubscriptionStatus.ACTIVE -> Color.Green to "Active"
        SubscriptionStatus.PAUSED -> Color(0xFFFF9800) to "Paused"
        SubscriptionStatus.CANCELLED -> Color.Red to "Cancelled"
        SubscriptionStatus.EXPIRED -> Color.Gray to "Expired"
        SubscriptionStatus.PENDING -> Color.Blue to "Pending"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BillingStatusChip(status: BillingStatus) {
    val (color, text) = when (status) {
        BillingStatus.PAID -> Color.Green to "Paid"
        BillingStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        BillingStatus.FAILED -> Color.Red to "Failed"
        BillingStatus.REFUNDED -> Color.Blue to "Refunded"
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
fun EmptySubscriptionsState() {
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
                imageVector = Icons.Default.Subscriptions,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Subscriptions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You don't have any active subscriptions. Browse available plans to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyBillingHistoryState() {
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
                text = "No Billing History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your billing history will appear here once you have active subscriptions.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyUsageState() {
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
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Usage Data",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Usage statistics will be available once you have active subscriptions with usage limits.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getCategoryName(category: SubscriptionCategory): String {
    return when (category) {
        SubscriptionCategory.BANKING -> "Banking Services"
        SubscriptionCategory.ENTERTAINMENT -> "Entertainment"
        SubscriptionCategory.PRODUCTIVITY -> "Productivity"
        SubscriptionCategory.LIFESTYLE -> "Lifestyle"
        SubscriptionCategory.FINANCE -> "Finance & Investment"
    }
}

fun getBillingCycleText(cycle: BillingCycle): String {
    return when (cycle) {
        BillingCycle.MONTHLY -> "month"
        BillingCycle.YEARLY -> "year"
        BillingCycle.WEEKLY -> "week"
    }
}

fun getDummyActiveSubscriptions(): List<Subscription> {
    return listOf(
        Subscription(
            id = "sub1",
            name = "Monzo Plus",
            description = "Premium banking features",
            price = 5.00,
            billingCycle = BillingCycle.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            nextBillingDate = "15 Jan 2024",
            startDate = "15 Dec 2023",
            category = SubscriptionCategory.BANKING,
            icon = Icons.Default.AccountBalance,
            color = Color.Blue,
            features = listOf("Custom categories", "Advanced budgeting", "Priority support"),
            canCancel = true,
            canPause = false
        ),
        Subscription(
            id = "sub2",
            name = "Investment Tracker Pro",
            description = "Advanced investment analytics",
            price = 9.99,
            billingCycle = BillingCycle.MONTHLY,
            status = SubscriptionStatus.ACTIVE,
            nextBillingDate = "20 Jan 2024",
            startDate = "20 Dec 2023",
            category = SubscriptionCategory.FINANCE,
            icon = Icons.Default.TrendingUp,
            color = Color.Green,
            features = listOf("Real-time tracking", "Portfolio analysis", "Tax reporting"),
            canCancel = true,
            canPause = true
        )
    )
}

fun getDummyAvailablePlans(): List<SubscriptionPlan> {
    return listOf(
        SubscriptionPlan(
            id = "plan1",
            name = "Monzo Basic",
            description = "Essential banking features",
            price = 0.00,
            billingCycle = BillingCycle.MONTHLY,
            features = listOf("Current account", "Debit card", "Basic budgeting", "Mobile app"),
            isPopular = false,
            isCurrentPlan = false,
            icon = Icons.Default.AccountBalance,
            color = Color.Gray
        ),
        SubscriptionPlan(
            id = "plan2",
            name = "Monzo Plus",
            description = "Enhanced banking with premium features",
            price = 5.00,
            billingCycle = BillingCycle.MONTHLY,
            features = listOf("All Basic features", "Custom categories", "Advanced budgeting", "Priority support", "Travel insurance"),
            isPopular = true,
            isCurrentPlan = true,
            icon = Icons.Default.Star,
            color = Color.Blue
        ),
        SubscriptionPlan(
            id = "plan3",
            name = "Monzo Premium",
            description = "Ultimate banking experience",
            price = 15.00,
            billingCycle = BillingCycle.MONTHLY,
            features = listOf("All Plus features", "Investment tracking", "Credit score monitoring", "Concierge service", "Global ATM access"),
            isPopular = false,
            isCurrentPlan = false,
            icon = Icons.Default.Diamond,
            color = Color(0xFF9C27B0)
        )
    )
}

fun getDummyBillingHistory(): List<BillingHistory> {
    return listOf(
        BillingHistory(
            id = "bill1",
            subscriptionName = "Monzo Plus",
            amount = 5.00,
            billingDate = "15 Dec 2023",
            status = BillingStatus.PAID,
            invoiceUrl = "https://example.com/invoice1"
        ),
        BillingHistory(
            id = "bill2",
            subscriptionName = "Investment Tracker Pro",
            amount = 9.99,
            billingDate = "20 Dec 2023",
            status = BillingStatus.PAID,
            invoiceUrl = "https://example.com/invoice2"
        ),
        BillingHistory(
            id = "bill3",
            subscriptionName = "Monzo Plus",
            amount = 5.00,
            billingDate = "15 Nov 2023",
            status = BillingStatus.PAID,
            invoiceUrl = "https://example.com/invoice3"
        )
    )
}

fun getDummyUsageData(): List<SubscriptionUsage> {
    return listOf(
        SubscriptionUsage(
            subscriptionId = "sub1",
            usageType = "Custom Categories",
            currentUsage = 8,
            limit = 20,
            resetDate = "15 Jan 2024"
        ),
        SubscriptionUsage(
            subscriptionId = "sub2",
            usageType = "API Calls",
            currentUsage = 1250,
            limit = 5000,
            resetDate = "20 Jan 2024"
        )
    )
}
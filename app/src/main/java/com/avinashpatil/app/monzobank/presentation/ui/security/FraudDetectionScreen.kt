package com.avinashpatil.app.monzobank.presentation.ui.security

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

data class FraudAlert(
    val id: String,
    val title: String,
    val description: String,
    val amount: Double,
    val merchant: String,
    val location: String,
    val timestamp: String,
    val riskLevel: RiskLevel,
    val status: FraudStatus,
    val transactionId: String,
    val suspiciousFactors: List<String>
)

data class FraudPattern(
    val id: String,
    val patternType: String,
    val description: String,
    val riskScore: Int,
    val detectedCount: Int,
    val lastDetected: String,
    val isActive: Boolean
)

data class ProtectionRule(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean,
    val ruleType: RuleType,
    val parameters: Map<String, Any>
)

data class FraudStatistics(
    val totalAlertsThisMonth: Int,
    val blockedTransactions: Int,
    val savedAmount: Double,
    val riskScore: Int,
    val protectionLevel: ProtectionLevel
)

data class SuspiciousTransaction(
    val id: String,
    val amount: Double,
    val merchant: String,
    val location: String,
    val timestamp: String,
    val riskFactors: List<String>,
    val action: TransactionAction,
    val confidence: Float
)

enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class FraudStatus {
    DETECTED, INVESTIGATING, RESOLVED, FALSE_POSITIVE
}

enum class RuleType {
    AMOUNT_LIMIT, LOCATION_BASED, TIME_BASED, MERCHANT_BASED, VELOCITY_CHECK
}

enum class ProtectionLevel {
    BASIC, STANDARD, ENHANCED, MAXIMUM
}

enum class TransactionAction {
    ALLOWED, BLOCKED, FLAGGED, REQUIRES_VERIFICATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FraudDetectionScreen(
    onBackClick: () -> Unit,
    onAlertClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit,
    onRuleClick: (String) -> Unit,
    onReportFraudClick: () -> Unit
) {
    val fraudAlerts = remember { getDummyFraudAlerts() }
    val fraudPatterns = remember { getDummyFraudPatterns() }
    val protectionRules = remember { getDummyProtectionRules() }
    val fraudStats = remember { getDummyFraudStatistics() }
    val suspiciousTransactions = remember { getDummySuspiciousTransactions() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Alerts", "Patterns", "Protection")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fraud Detection") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Fraud settings */ }) {
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
            FloatingActionButton(
                onClick = onReportFraudClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Report, contentDescription = "Report Fraud")
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
                0 -> FraudOverviewContent(
                    stats = fraudStats,
                    recentAlerts = fraudAlerts.take(3),
                    suspiciousTransactions = suspiciousTransactions.take(3),
                    onAlertClick = onAlertClick,
                    onTransactionClick = onTransactionClick
                )
                1 -> FraudAlertsContent(
                    alerts = fraudAlerts,
                    onAlertClick = onAlertClick
                )
                2 -> FraudPatternsContent(
                    patterns = fraudPatterns,
                    onPatternClick = { /* View pattern details */ }
                )
                3 -> ProtectionRulesContent(
                    rules = protectionRules,
                    onRuleClick = onRuleClick
                )
            }
        }
    }
}

@Composable
fun FraudOverviewContent(
    stats: FraudStatistics,
    recentAlerts: List<FraudAlert>,
    suspiciousTransactions: List<SuspiciousTransaction>,
    onAlertClick: (String) -> Unit,
    onTransactionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Fraud Statistics
        item {
            FraudStatisticsCard(stats = stats)
        }

        // Protection Status
        item {
            ProtectionStatusCard(protectionLevel = stats.protectionLevel)
        }

        // Recent Alerts
        if (recentAlerts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Fraud Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { /* View all alerts */ }) {
                        Text("View All")
                    }
                }
            }

            items(recentAlerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        // Suspicious Transactions
        if (suspiciousTransactions.isNotEmpty()) {
            item {
                Text(
                    text = "Flagged Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(suspiciousTransactions) { transaction ->
                SuspiciousTransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }

        // Fraud Prevention Tips
        item {
            FraudPreventionTips()
        }
    }
}

@Composable
fun FraudAlertsContent(
    alerts: List<FraudAlert>,
    onAlertClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Fraud Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Filter by risk level
        val criticalAlerts = alerts.filter { it.riskLevel == RiskLevel.CRITICAL }
        val highAlerts = alerts.filter { it.riskLevel == RiskLevel.HIGH }
        val otherAlerts = alerts.filter { it.riskLevel in listOf(RiskLevel.MEDIUM, RiskLevel.LOW) }

        if (criticalAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "Critical Risk",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
            }
            items(criticalAlerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        if (highAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "High Risk",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF9800)
                )
            }
            items(highAlerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        if (otherAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "Other Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(otherAlerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        if (alerts.isEmpty()) {
            item {
                EmptyFraudAlertsState()
            }
        }
    }
}

@Composable
fun FraudPatternsContent(
    patterns: List<FraudPattern>,
    onPatternClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Fraud Patterns",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "AI-detected patterns that may indicate fraudulent activity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(patterns) { pattern ->
            FraudPatternCard(
                pattern = pattern,
                onClick = { onPatternClick(pattern.id) }
            )
        }
    }
}

@Composable
fun ProtectionRulesContent(
    rules: List<ProtectionRule>,
    onRuleClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Protection Rules",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Customize your fraud protection settings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Group rules by type
        val rulesByType = rules.groupBy { it.ruleType }
        rulesByType.forEach { (type, typeRules) ->
            item {
                Text(
                    text = getRuleTypeName(type),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(typeRules) { rule ->
                ProtectionRuleCard(
                    rule = rule,
                    onClick = { onRuleClick(rule.id) }
                )
            }
        }
    }
}

@Composable
fun FraudStatisticsCard(stats: FraudStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Fraud Protection",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Your account is protected",
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
                StatItem(
                    title = "Alerts This Month",
                    value = stats.totalAlertsThisMonth.toString(),
                    icon = Icons.Default.Warning
                )
                StatItem(
                    title = "Blocked Transactions",
                    value = stats.blockedTransactions.toString(),
                    icon = Icons.Default.Block
                )
                StatItem(
                    title = "Amount Saved",
                    value = "£${String.format("%,.0f", stats.savedAmount)}",
                    icon = Icons.Default.Savings
                )
            }
        }
    }
}

@Composable
fun StatItem(
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
fun ProtectionStatusCard(protectionLevel: ProtectionLevel) {
    val (color, statusText, description) = when (protectionLevel) {
        ProtectionLevel.MAXIMUM -> Triple(
            Color(0xFF4CAF50),
            "Maximum Protection",
            "All fraud detection features are active"
        )
        ProtectionLevel.ENHANCED -> Triple(
            Color(0xFF8BC34A),
            "Enhanced Protection",
            "Advanced fraud detection is enabled"
        )
        ProtectionLevel.STANDARD -> Triple(
            Color(0xFFFF9800),
            "Standard Protection",
            "Basic fraud detection is active"
        )
        ProtectionLevel.BASIC -> Triple(
            Color(0xFFF44336),
            "Basic Protection",
            "Consider upgrading your protection level"
        )
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (protectionLevel != ProtectionLevel.MAXIMUM) {
                TextButton(onClick = { /* Upgrade protection */ }) {
                    Text("Upgrade")
                }
            }
        }
    }
}

@Composable
fun FraudAlertCard(
    alert: FraudAlert,
    onClick: () -> Unit
) {
    val riskColor = when (alert.riskLevel) {
        RiskLevel.CRITICAL -> Color.Red
        RiskLevel.HIGH -> Color(0xFFFF9800)
        RiskLevel.MEDIUM -> Color.Yellow
        RiskLevel.LOW -> Color.Blue
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = riskColor.copy(alpha = 0.1f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alert.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = alert.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                RiskLevelChip(riskLevel = alert.riskLevel)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount: £${String.format("%,.2f", alert.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Merchant: ${alert.merchant}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = alert.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = alert.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (alert.suspiciousFactors.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Suspicious factors: ${alert.suspiciousFactors.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = riskColor
                )
            }
        }
    }
}

@Composable
fun SuspiciousTransactionCard(
    transaction: SuspiciousTransaction,
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
                imageVector = when (transaction.action) {
                    TransactionAction.BLOCKED -> Icons.Default.Block
                    TransactionAction.FLAGGED -> Icons.Default.Flag
                    TransactionAction.REQUIRES_VERIFICATION -> Icons.Default.VerifiedUser
                    TransactionAction.ALLOWED -> Icons.Default.CheckCircle
                },
                contentDescription = null,
                tint = when (transaction.action) {
                    TransactionAction.BLOCKED -> Color.Red
                    TransactionAction.FLAGGED -> Color(0xFFFF9800)
                    TransactionAction.REQUIRES_VERIFICATION -> Color.Blue
                    TransactionAction.ALLOWED -> Color.Green
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.merchant,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${transaction.location} • ${transaction.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (transaction.riskFactors.isNotEmpty()) {
                    Text(
                        text = transaction.riskFactors.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                TransactionActionChip(action = transaction.action)
            }
        }
    }
}

@Composable
fun FraudPatternCard(
    pattern: FraudPattern,
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
                        text = pattern.patternType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = pattern.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    color = if (pattern.isActive) Color.Green.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (pattern.isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (pattern.isActive) Color.Green else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Risk Score: ${pattern.riskScore}/100",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Detected: ${pattern.detectedCount} times",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Last: ${pattern.lastDetected}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProtectionRuleCard(
    rule: ProtectionRule,
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
                imageVector = getRuleTypeIcon(rule.ruleType),
                contentDescription = null,
                tint = if (rule.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = rule.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = rule.isEnabled,
                onCheckedChange = { /* Toggle rule */ }
            )
        }
    }
}

@Composable
fun FraudPreventionTips() {
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
                text = "Fraud Prevention Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "Never share your PIN or passwords with anyone",
                "Check your statements regularly for unauthorized transactions",
                "Use secure networks when banking online",
                "Enable transaction notifications for real-time alerts"
            )
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun RiskLevelChip(riskLevel: RiskLevel) {
    val (color, text) = when (riskLevel) {
        RiskLevel.CRITICAL -> Color.Red to "Critical"
        RiskLevel.HIGH -> Color(0xFFFF9800) to "High"
        RiskLevel.MEDIUM -> Color(0xFFFF9800) to "Medium"
        RiskLevel.LOW -> Color.Blue to "Low"
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
fun TransactionActionChip(action: TransactionAction) {
    val (color, text) = when (action) {
        TransactionAction.BLOCKED -> Color.Red to "Blocked"
        TransactionAction.FLAGGED -> Color(0xFFFF9800) to "Flagged"
        TransactionAction.REQUIRES_VERIFICATION -> Color.Blue to "Verify"
        TransactionAction.ALLOWED -> Color.Green to "Allowed"
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
fun EmptyFraudAlertsState() {
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
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Fraud Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your account is secure. We'll alert you of any suspicious activity.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getRuleTypeName(ruleType: RuleType): String {
    return when (ruleType) {
        RuleType.AMOUNT_LIMIT -> "Amount Limits"
        RuleType.LOCATION_BASED -> "Location-Based Rules"
        RuleType.TIME_BASED -> "Time-Based Rules"
        RuleType.MERCHANT_BASED -> "Merchant Rules"
        RuleType.VELOCITY_CHECK -> "Velocity Checks"
    }
}

fun getRuleTypeIcon(ruleType: RuleType): ImageVector {
    return when (ruleType) {
        RuleType.AMOUNT_LIMIT -> Icons.Default.AttachMoney
        RuleType.LOCATION_BASED -> Icons.Default.LocationOn
        RuleType.TIME_BASED -> Icons.Default.Schedule
        RuleType.MERCHANT_BASED -> Icons.Default.Store
        RuleType.VELOCITY_CHECK -> Icons.Default.Speed
    }
}

fun getDummyFraudAlerts(): List<FraudAlert> {
    return listOf(
        FraudAlert(
            id = "FA001",
            title = "Suspicious Transaction Detected",
            description = "Unusual spending pattern detected",
            amount = 500.00,
            merchant = "Unknown Online Merchant",
            location = "Manchester, UK",
            timestamp = "2 hours ago",
            riskLevel = RiskLevel.HIGH,
            status = FraudStatus.DETECTED,
            transactionId = "TXN123456",
            suspiciousFactors = listOf("New location", "High amount", "Unusual time")
        ),
        FraudAlert(
            id = "FA002",
            title = "Multiple Failed Login Attempts",
            description = "Several failed login attempts from unknown device",
            amount = 0.0,
            merchant = "N/A",
            location = "Birmingham, UK",
            timestamp = "1 day ago",
            riskLevel = RiskLevel.MEDIUM,
            status = FraudStatus.INVESTIGATING,
            transactionId = "N/A",
            suspiciousFactors = listOf("Unknown device", "Multiple attempts")
        )
    )
}

fun getDummyFraudPatterns(): List<FraudPattern> {
    return listOf(
        FraudPattern(
            id = "FP001",
            patternType = "Velocity Fraud",
            description = "Multiple transactions in short time period",
            riskScore = 85,
            detectedCount = 12,
            lastDetected = "Yesterday",
            isActive = true
        ),
        FraudPattern(
            id = "FP002",
            patternType = "Location Anomaly",
            description = "Transactions from unusual locations",
            riskScore = 70,
            detectedCount = 8,
            lastDetected = "3 days ago",
            isActive = true
        ),
        FraudPattern(
            id = "FP003",
            patternType = "Amount Pattern",
            description = "Unusual transaction amounts",
            riskScore = 60,
            detectedCount = 5,
            lastDetected = "1 week ago",
            isActive = false
        )
    )
}

fun getDummyProtectionRules(): List<ProtectionRule> {
    return listOf(
        ProtectionRule(
            id = "PR001",
            name = "Daily Spending Limit",
            description = "Block transactions over £1000 per day",
            isEnabled = true,
            ruleType = RuleType.AMOUNT_LIMIT,
            parameters = mapOf("limit" to 1000.0, "period" to "daily")
        ),
        ProtectionRule(
            id = "PR002",
            name = "Foreign Transaction Alert",
            description = "Alert for transactions outside UK",
            isEnabled = true,
            ruleType = RuleType.LOCATION_BASED,
            parameters = mapOf("countries" to listOf("UK"))
        ),
        ProtectionRule(
            id = "PR003",
            name = "Night Time Restrictions",
            description = "Block large transactions between 11 PM - 6 AM",
            isEnabled = false,
            ruleType = RuleType.TIME_BASED,
            parameters = mapOf("startTime" to 23, "endTime" to 6, "threshold" to 200.0)
        ),
        ProtectionRule(
            id = "PR004",
            name = "High-Risk Merchant Block",
            description = "Block transactions from high-risk merchants",
            isEnabled = true,
            ruleType = RuleType.MERCHANT_BASED,
            parameters = mapOf("riskCategories" to listOf("gambling", "adult"))
        )
    )
}

fun getDummyFraudStatistics(): FraudStatistics {
    return FraudStatistics(
        totalAlertsThisMonth = 3,
        blockedTransactions = 2,
        savedAmount = 750.0,
        riskScore = 15,
        protectionLevel = ProtectionLevel.ENHANCED
    )
}

fun getDummySuspiciousTransactions(): List<SuspiciousTransaction> {
    return listOf(
        SuspiciousTransaction(
            id = "ST001",
            amount = 500.00,
            merchant = "Unknown Online Store",
            location = "Manchester, UK",
            timestamp = "2 hours ago",
            riskFactors = listOf("New location", "High amount"),
            action = TransactionAction.BLOCKED,
            confidence = 0.85f
        ),
        SuspiciousTransaction(
            id = "ST002",
            amount = 150.00,
            merchant = "Gas Station",
            location = "Birmingham, UK",
            timestamp = "1 day ago",
            riskFactors = listOf("Unusual time"),
            action = TransactionAction.FLAGGED,
            confidence = 0.65f
        )
    )
}
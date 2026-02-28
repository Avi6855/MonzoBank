package com.avinashpatil.app.monzobank.presentation.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

data class SecurityFeature(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isEnabled: Boolean,
    val isRecommended: Boolean,
    val category: SecurityCategory
)

data class SecurityAlert(
    val id: String,
    val title: String,
    val message: String,
    val severity: AlertSeverity,
    val timestamp: String,
    val isRead: Boolean,
    val actionRequired: Boolean
)

data class LoginActivity(
    val id: String,
    val device: String,
    val location: String,
    val timestamp: String,
    val ipAddress: String,
    val status: LoginStatus,
    val isCurrent: Boolean
)

data class SecurityScore(
    val score: Int,
    val maxScore: Int,
    val level: SecurityLevel,
    val recommendations: List<String>
)

data class TrustedDevice(
    val id: String,
    val deviceName: String,
    val deviceType: String,
    val lastUsed: String,
    val isCurrentDevice: Boolean,
    val location: String
)

enum class SecurityCategory {
    AUTHENTICATION, PRIVACY, MONITORING, BACKUP
}

enum class AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class LoginStatus {
    SUCCESS, FAILED, BLOCKED
}

enum class SecurityLevel {
    POOR, FAIR, GOOD, EXCELLENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityCenterScreen(
    onBackClick: () -> Unit,
    onFeatureClick: (String) -> Unit,
    onAlertClick: (String) -> Unit,
    onDeviceClick: (String) -> Unit,
    onFraudDetectionClick: () -> Unit
) {
    val securityFeatures = remember { getDummySecurityFeatures() }
    val securityAlerts = remember { getDummySecurityAlerts() }
    val loginActivities = remember { getDummyLoginActivities() }
    val securityScore = remember { getDummySecurityScore() }
    val trustedDevices = remember { getDummyTrustedDevices() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Alerts", "Activity", "Devices")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Center") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Security settings */ }) {
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
                0 -> SecurityOverviewContent(
                    securityScore = securityScore,
                    features = securityFeatures,
                    alerts = securityAlerts.take(3),
                    onFeatureClick = onFeatureClick,
                    onAlertClick = onAlertClick,
                    onFraudDetectionClick = onFraudDetectionClick
                )
                1 -> SecurityAlertsContent(
                    alerts = securityAlerts,
                    onAlertClick = onAlertClick
                )
                2 -> LoginActivityContent(
                    activities = loginActivities,
                    onActivityClick = { /* View activity details */ }
                )
                3 -> TrustedDevicesContent(
                    devices = trustedDevices,
                    onDeviceClick = onDeviceClick
                )
            }
        }
    }
}

@Composable
fun SecurityOverviewContent(
    securityScore: SecurityScore,
    features: List<SecurityFeature>,
    alerts: List<SecurityAlert>,
    onFeatureClick: (String) -> Unit,
    onAlertClick: (String) -> Unit,
    onFraudDetectionClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Score
        item {
            SecurityScoreCard(score = securityScore)
        }

        // Quick Actions
        item {
            SecurityQuickActions(onFraudDetectionClick = onFraudDetectionClick)
        }

        // Recent Alerts
        if (alerts.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Alerts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { /* View all alerts */ }) {
                        Text("View All")
                    }
                }
            }

            items(alerts) { alert ->
                SecurityAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        // Security Features
        item {
            Text(
                text = "Security Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(features.filter { it.isRecommended || it.isEnabled }) { feature ->
            SecurityFeatureCard(
                feature = feature,
                onClick = { onFeatureClick(feature.id) }
            )
        }
    }
}

@Composable
fun SecurityAlertsContent(
    alerts: List<SecurityAlert>,
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
                text = "Security Alerts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Filter by severity
        val criticalAlerts = alerts.filter { it.severity == AlertSeverity.CRITICAL }
        val highAlerts = alerts.filter { it.severity == AlertSeverity.HIGH }
        val otherAlerts = alerts.filter { it.severity in listOf(AlertSeverity.MEDIUM, AlertSeverity.LOW) }

        if (criticalAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "Critical Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red
                )
            }
            items(criticalAlerts) { alert ->
                SecurityAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        if (highAlerts.isNotEmpty()) {
            item {
                Text(
                    text = "High Priority Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF9800)
                )
            }
            items(highAlerts) { alert ->
                SecurityAlertCard(
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
                SecurityAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert.id) }
                )
            }
        }

        if (alerts.isEmpty()) {
            item {
                EmptyAlertsState()
            }
        }
    }
}

@Composable
fun LoginActivityContent(
    activities: List<LoginActivity>,
    onActivityClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Login Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Monitor your account access and login attempts",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(activities) { activity ->
            LoginActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )
        }
    }
}

@Composable
fun TrustedDevicesContent(
    devices: List<TrustedDevice>,
    onDeviceClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Trusted Devices",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Manage devices that can access your account",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(devices) { device ->
            TrustedDeviceCard(
                device = device,
                onClick = { onDeviceClick(device.id) }
            )
        }
    }
}

@Composable
fun SecurityScoreCard(score: SecurityScore) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (score.level) {
                SecurityLevel.EXCELLENT -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                SecurityLevel.GOOD -> Color(0xFF8BC34A).copy(alpha = 0.1f)
                SecurityLevel.FAIR -> Color(0xFFFF9800).copy(alpha = 0.1f)
                SecurityLevel.POOR -> Color(0xFFF44336).copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Security Score",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = score.level.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        color = when (score.level) {
                            SecurityLevel.EXCELLENT -> Color(0xFF4CAF50)
                            SecurityLevel.GOOD -> Color(0xFF8BC34A)
                            SecurityLevel.FAIR -> Color(0xFFFF9800)
                            SecurityLevel.POOR -> Color(0xFFF44336)
                        }
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when (score.level) {
                                SecurityLevel.EXCELLENT -> Color(0xFF4CAF50)
                                SecurityLevel.GOOD -> Color(0xFF8BC34A)
                                SecurityLevel.FAIR -> Color(0xFFFF9800)
                                SecurityLevel.POOR -> Color(0xFFF44336)
                            }.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${score.score}/${score.maxScore}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (score.level) {
                            SecurityLevel.EXCELLENT -> Color(0xFF4CAF50)
                            SecurityLevel.GOOD -> Color(0xFF8BC34A)
                            SecurityLevel.FAIR -> Color(0xFFFF9800)
                            SecurityLevel.POOR -> Color(0xFFF44336)
                        }
                    )
                }
            }
            
            if (score.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Recommendations:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                score.recommendations.take(2).forEach { recommendation ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(6.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun SecurityQuickActions(onFraudDetectionClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Change Password",
            icon = Icons.Default.Lock,
            onClick = { /* Change password */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "2FA Setup",
            icon = Icons.Default.Security,
            onClick = { /* Setup 2FA */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Fraud Detection",
            icon = Icons.Default.Shield,
            onClick = onFraudDetectionClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
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

@Composable
fun SecurityFeatureCard(
    feature: SecurityFeature,
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
                imageVector = feature.icon,
                contentDescription = null,
                tint = if (feature.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feature.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (feature.isRecommended) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Recommended",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Switch(
                checked = feature.isEnabled,
                onCheckedChange = { /* Toggle feature */ }
            )
        }
    }
}

@Composable
fun SecurityAlertCard(
    alert: SecurityAlert,
    onClick: () -> Unit
) {
    val alertColor = when (alert.severity) {
        AlertSeverity.CRITICAL -> Color.Red
        AlertSeverity.HIGH -> Color(0xFFFF9800)
        AlertSeverity.MEDIUM -> Color(0xFFFF9800)
        AlertSeverity.LOW -> Color.Blue
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
                imageVector = when (alert.severity) {
                    AlertSeverity.CRITICAL -> Icons.Default.Error
                    AlertSeverity.HIGH -> Icons.Default.Warning
                    AlertSeverity.MEDIUM -> Icons.Default.Info
                    AlertSeverity.LOW -> Icons.Default.Notifications
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
            
            Column {
                if (!alert.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(alertColor)
                    )
                }
                if (alert.actionRequired) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = alertColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Action Required",
                            style = MaterialTheme.typography.bodySmall,
                            color = alertColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginActivityCard(
    activity: LoginActivity,
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
                imageVector = when (activity.status) {
                    LoginStatus.SUCCESS -> Icons.Default.CheckCircle
                    LoginStatus.FAILED -> Icons.Default.Error
                    LoginStatus.BLOCKED -> Icons.Default.Block
                },
                contentDescription = null,
                tint = when (activity.status) {
                    LoginStatus.SUCCESS -> Color.Green
                    LoginStatus.FAILED -> Color.Red
                    LoginStatus.BLOCKED -> Color(0xFFFF9800)
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = activity.device,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (activity.isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color.Green.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
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
                    text = "${activity.location} • ${activity.ipAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = activity.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            LoginStatusChip(status = activity.status)
        }
    }
}

@Composable
fun TrustedDeviceCard(
    device: TrustedDevice,
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
                imageVector = when (device.deviceType.lowercase()) {
                    "mobile" -> Icons.Default.PhoneAndroid
                    "tablet" -> Icons.Default.Tablet
                    "desktop" -> Icons.Default.Computer
                    else -> Icons.Default.DeviceUnknown
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = device.deviceName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (device.isCurrentDevice) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color.Green.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "This Device",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Green,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${device.deviceType} • ${device.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Last used: ${device.lastUsed}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = { /* Remove device */ }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }
    }
}

@Composable
fun LoginStatusChip(status: LoginStatus) {
    val (color, text) = when (status) {
        LoginStatus.SUCCESS -> Color.Green to "Success"
        LoginStatus.FAILED -> Color.Red to "Failed"
        LoginStatus.BLOCKED -> Color(0xFFFF9800) to "Blocked"
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
fun EmptyAlertsState() {
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Security Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your account is secure. We'll notify you of any security concerns.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummySecurityFeatures(): List<SecurityFeature> {
    return listOf(
        SecurityFeature(
            id = "SF001",
            title = "Two-Factor Authentication",
            description = "Add an extra layer of security to your account",
            icon = Icons.Default.Security,
            isEnabled = true,
            isRecommended = false,
            category = SecurityCategory.AUTHENTICATION
        ),
        SecurityFeature(
            id = "SF002",
            title = "Biometric Login",
            description = "Use fingerprint or face recognition to log in",
            icon = Icons.Default.Fingerprint,
            isEnabled = true,
            isRecommended = false,
            category = SecurityCategory.AUTHENTICATION
        ),
        SecurityFeature(
            id = "SF003",
            title = "Login Notifications",
            description = "Get notified of new login attempts",
            icon = Icons.Default.Notifications,
            isEnabled = false,
            isRecommended = true,
            category = SecurityCategory.MONITORING
        ),
        SecurityFeature(
            id = "SF004",
            title = "Transaction Alerts",
            description = "Receive alerts for all transactions",
            icon = Icons.Default.NotificationsActive,
            isEnabled = true,
            isRecommended = false,
            category = SecurityCategory.MONITORING
        ),
        SecurityFeature(
            id = "SF005",
            title = "Account Backup",
            description = "Secure backup of your account data",
            icon = Icons.Default.Backup,
            isEnabled = false,
            isRecommended = true,
            category = SecurityCategory.BACKUP
        )
    )
}

fun getDummySecurityAlerts(): List<SecurityAlert> {
    return listOf(
        SecurityAlert(
            id = "SA001",
            title = "New Device Login",
            message = "Your account was accessed from a new device in London, UK",
            severity = AlertSeverity.MEDIUM,
            timestamp = "2 hours ago",
            isRead = false,
            actionRequired = true
        ),
        SecurityAlert(
            id = "SA002",
            title = "Password Change Recommended",
            message = "Your password hasn't been changed in 6 months",
            severity = AlertSeverity.LOW,
            timestamp = "1 day ago",
            isRead = false,
            actionRequired = false
        ),
        SecurityAlert(
            id = "SA003",
            title = "Suspicious Transaction Blocked",
            message = "We blocked a suspicious transaction of £500 to an unknown merchant",
            severity = AlertSeverity.HIGH,
            timestamp = "3 days ago",
            isRead = true,
            actionRequired = false
        )
    )
}

fun getDummyLoginActivities(): List<LoginActivity> {
    return listOf(
        LoginActivity(
            id = "LA001",
            device = "iPhone 14 Pro",
            location = "London, UK",
            timestamp = "2 hours ago",
            ipAddress = "192.168.1.1",
            status = LoginStatus.SUCCESS,
            isCurrent = true
        ),
        LoginActivity(
            id = "LA002",
            device = "MacBook Pro",
            location = "London, UK",
            timestamp = "Yesterday",
            ipAddress = "192.168.1.2",
            status = LoginStatus.SUCCESS,
            isCurrent = false
        ),
        LoginActivity(
            id = "LA003",
            device = "Unknown Device",
            location = "Manchester, UK",
            timestamp = "3 days ago",
            ipAddress = "203.0.113.1",
            status = LoginStatus.FAILED,
            isCurrent = false
        ),
        LoginActivity(
            id = "LA004",
            device = "iPad Air",
            location = "London, UK",
            timestamp = "1 week ago",
            ipAddress = "192.168.1.3",
            status = LoginStatus.SUCCESS,
            isCurrent = false
        )
    )
}

fun getDummySecurityScore(): SecurityScore {
    return SecurityScore(
        score = 85,
        maxScore = 100,
        level = SecurityLevel.GOOD,
        recommendations = listOf(
            "Enable login notifications for better monitoring",
            "Set up account backup for data protection",
            "Review and update your trusted devices list"
        )
    )
}

fun getDummyTrustedDevices(): List<TrustedDevice> {
    return listOf(
        TrustedDevice(
            id = "TD001",
            deviceName = "iPhone 14 Pro",
            deviceType = "Mobile",
            lastUsed = "Now",
            isCurrentDevice = true,
            location = "London, UK"
        ),
        TrustedDevice(
            id = "TD002",
            deviceName = "MacBook Pro",
            deviceType = "Desktop",
            lastUsed = "Yesterday",
            isCurrentDevice = false,
            location = "London, UK"
        ),
        TrustedDevice(
            id = "TD003",
            deviceName = "iPad Air",
            deviceType = "Tablet",
            lastUsed = "1 week ago",
            isCurrentDevice = false,
            location = "London, UK"
        )
    )
}
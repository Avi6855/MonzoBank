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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.monzobank.domain.model.*
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    fraudAlerts: List<FraudAlert>,
    securityEvents: List<SecurityEvent>,
    securityMetrics: SecurityMetrics,
    onNavigateBack: () -> Unit,
    onAlertClick: (FraudAlert) -> Unit,
    onEventClick: (SecurityEvent) -> Unit,
    onViewAllAlerts: () -> Unit,
    onViewAllEvents: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Alerts", "Events", "Compliance")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Security Center",
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
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Security Settings",
                        tint = MonzoCoralPrimary
                    )
                }
            }
        )
        
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MonzoCoralPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
        
        // Content
        when (selectedTab) {
            0 -> SecurityOverviewTab(
                securityMetrics = securityMetrics,
                recentAlerts = fraudAlerts.take(3),
                recentEvents = securityEvents.take(3),
                onViewAllAlerts = onViewAllAlerts,
                onViewAllEvents = onViewAllEvents
            )
            1 -> FraudAlertsTab(
                alerts = fraudAlerts,
                onAlertClick = onAlertClick
            )
            2 -> SecurityEventsTab(
                events = securityEvents,
                onEventClick = onEventClick
            )
            3 -> ComplianceTab()
        }
    }
}

@Composable
fun SecurityOverviewTab(
    securityMetrics: SecurityMetrics,
    recentAlerts: List<FraudAlert>,
    recentEvents: List<SecurityEvent>,
    onViewAllAlerts: () -> Unit,
    onViewAllEvents: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Status Card
        item {
            SecurityStatusCard(securityMetrics = securityMetrics)
        }
        
        // Quick Stats
        item {
            QuickStatsRow(securityMetrics = securityMetrics)
        }
        
        // Recent Alerts
        if (recentAlerts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recent Fraud Alerts",
                    actionText = "View All",
                    onActionClick = onViewAllAlerts
                )
            }
            
            items(recentAlerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { },
                    isCompact = true
                )
            }
        }
        
        // Recent Security Events
        if (recentEvents.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recent Security Events",
                    actionText = "View All",
                    onActionClick = onViewAllEvents
                )
            }
            
            items(recentEvents) { event ->
                SecurityEventCard(
                    event = event,
                    onClick = { },
                    isCompact = true
                )
            }
        }
        
        // Security Recommendations
        item {
            SecurityRecommendationsCard()
        }
    }
}

@Composable
fun FraudAlertsTab(
    alerts: List<FraudAlert>,
    onAlertClick: (FraudAlert) -> Unit
) {
    if (alerts.isEmpty()) {
        EmptySecurityAlertsState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(alerts) { alert ->
                FraudAlertCard(
                    alert = alert,
                    onClick = { onAlertClick(alert) }
                )
            }
        }
    }
}

@Composable
fun SecurityEventsTab(
    events: List<SecurityEvent>,
    onEventClick: (SecurityEvent) -> Unit
) {
    if (events.isEmpty()) {
        EmptyEventsState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(events) { event ->
                SecurityEventCard(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
fun ComplianceTab() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ComplianceOverviewCard()
        }
        
        item {
            PCIDSSComplianceCard()
        }
        
        item {
            GDPRComplianceCard()
        }
        
        item {
            SecurityCertificationsCard()
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SecurityStatusCard(
    securityMetrics: SecurityMetrics
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
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
                        text = "Security Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "All systems secure",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50)
                    )
                }
                
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color(0xFF4CAF50)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Security Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Security Score",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${securityMetrics.complianceScore.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = (securityMetrics.complianceScore / 100).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF4CAF50),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun QuickStatsRow(
    securityMetrics: SecurityMetrics
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickStatCard(
            title = "Total Events",
            value = securityMetrics.totalSecurityEvents.toString(),
            icon = Icons.Default.Event,
            modifier = Modifier.weight(1f)
        )
        
        QuickStatCard(
            title = "Critical",
            value = securityMetrics.criticalEvents.toString(),
            icon = Icons.Default.Warning,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
        
        QuickStatCard(
            title = "Resolved",
            value = "${securityMetrics.resolutionRate.toInt()}%",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = MonzoCoralPrimary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FraudAlertCard(
    alert: FraudAlert,
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = when (alert.riskLevel) {
                RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                RiskLevel.MEDIUM -> Color(0xFFFF9800).copy(alpha = 0.1f)
                RiskLevel.LOW -> MaterialTheme.colorScheme.surface
                RiskLevel.CRITICAL -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Risk Level Indicator
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = when (alert.riskLevel) {
                    RiskLevel.HIGH -> MaterialTheme.colorScheme.error
                    RiskLevel.MEDIUM -> Color(0xFFFF9800)
                    RiskLevel.LOW -> Color(0xFF4CAF50)
                    RiskLevel.CRITICAL -> Color.Red
                }
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (alert.riskLevel) {
                            RiskLevel.HIGH -> Icons.Default.Error
                            RiskLevel.MEDIUM -> Icons.Default.Warning
                            RiskLevel.LOW -> Icons.Default.Info
                            RiskLevel.CRITICAL -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = if (isCompact) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = alert.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (!isCompact) {
                    Text(
                        text = alert.riskLevel.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (alert.riskLevel) {
                            RiskLevel.HIGH -> MaterialTheme.colorScheme.error
                            RiskLevel.MEDIUM -> Color(0xFFFF9800)
                            RiskLevel.LOW -> Color(0xFF4CAF50)
                            RiskLevel.CRITICAL -> Color.Red
                        }
                    )
                }
            }
            
            if (alert.status != FraudStatus.RESOLVED) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Active",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SecurityEventCard(
    event: SecurityEvent,
    onClick: () -> Unit,
    isCompact: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getEventIcon(event.eventType),
                contentDescription = null,
                tint = getSeverityColor(event.severity),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.eventType.name.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (!isCompact) {
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(event.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = getDisplayName(event.severity),
                style = MaterialTheme.typography.labelSmall,
                color = getSeverityColor(event.severity),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SecurityRecommendationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Security Recommendations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val recommendations = listOf(
                "Enable two-factor authentication",
                "Review recent login locations",
                "Update security questions",
                "Check connected devices"
            )
            
            recommendations.forEach { recommendation ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ComplianceOverviewCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Compliance Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ComplianceItem("PCI DSS", ComplianceStatus.COMPLIANT)
            ComplianceItem("GDPR", ComplianceStatus.COMPLIANT)
            ComplianceItem("ISO 27001", ComplianceStatus.PARTIALLY_COMPLIANT)
            ComplianceItem("SOX", ComplianceStatus.UNDER_REVIEW)
        }
    }
}

@Composable
fun ComplianceItem(
    standard: String,
    status: ComplianceStatus
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = standard,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = getComplianceColor(status).copy(alpha = 0.1f)
        ) {
            Text(
                text = status.displayName,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = getComplianceColor(status),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PCIDSSComplianceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "PCI DSS Compliance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "All payment card data is encrypted and stored securely according to PCI DSS standards.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Last audit: December 2023",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun GDPRComplianceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "GDPR Compliance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "User data is processed and stored in compliance with GDPR regulations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Privacy policy updated: January 2024",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun SecurityCertificationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Security Certifications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val certifications = listOf(
                "ISO 27001:2013" to "Valid until Dec 2024",
                "SOC 2 Type II" to "Valid until Mar 2024",
                "PCI DSS Level 1" to "Valid until Jun 2024"
            )
            
            certifications.forEach { (cert, validity) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = cert,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Text(
                        text = validity,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EmptySecurityAlertsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Fraud Alerts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Your account is secure with no fraud alerts detected.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyEventsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Security Events",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "No recent security events to display.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

private fun getSeverityColor(severity: SecuritySeverity): Color {
    return when (severity) {
        SecuritySeverity.LOW -> Color(0xFF4CAF50)
        SecuritySeverity.MEDIUM -> Color(0xFFFF9800)
        SecuritySeverity.HIGH -> Color(0xFFFF5722)
        SecuritySeverity.CRITICAL -> Color(0xFFD32F2F)
    }
}

private fun getComplianceColor(status: ComplianceStatus): Color {
    return when (status) {
        ComplianceStatus.COMPLIANT -> Color(0xFF4CAF50)
        ComplianceStatus.PARTIALLY_COMPLIANT -> Color(0xFFFF9800)
        ComplianceStatus.NON_COMPLIANT -> Color(0xFFFF5722)
        ComplianceStatus.UNDER_REVIEW -> Color(0xFF2196F3)
    }
}

private fun getEventIcon(type: SecurityEventType): androidx.compose.ui.graphics.vector.ImageVector {
    return when (type) {
        SecurityEventType.LOGIN_SUCCESS -> Icons.Default.Login
        SecurityEventType.LOGIN_FAILED -> Icons.Default.Warning
        SecurityEventType.LOGIN_SUSPICIOUS -> Icons.Default.Security
        SecurityEventType.PASSWORD_CHANGED -> Icons.Default.Key
        SecurityEventType.PASSWORD_RESET_REQUESTED -> Icons.Default.Key
        SecurityEventType.PASSWORD_RESET_COMPLETED -> Icons.Default.Key
        SecurityEventType.TWO_FACTOR_ENABLED -> Icons.Default.Security
        SecurityEventType.TWO_FACTOR_DISABLED -> Icons.Default.Security
        SecurityEventType.TWO_FACTOR_FAILED -> Icons.Default.Warning
        SecurityEventType.ACCOUNT_LOCKED -> Icons.Default.Lock
        SecurityEventType.ACCOUNT_UNLOCKED -> Icons.Default.Lock
        SecurityEventType.DEVICE_REGISTERED -> Icons.Default.Devices
        SecurityEventType.DEVICE_REMOVED -> Icons.Default.Devices
        SecurityEventType.SUSPICIOUS_TRANSACTION -> Icons.Default.Report
        SecurityEventType.FRAUD_DETECTED -> Icons.Default.Report
        SecurityEventType.DATA_BREACH_ATTEMPT -> Icons.Default.Security
        SecurityEventType.UNAUTHORIZED_ACCESS -> Icons.Default.Security
        SecurityEventType.SECURITY_SETTINGS_CHANGED -> Icons.Default.Settings
        SecurityEventType.API_KEY_GENERATED -> Icons.Default.Key
        SecurityEventType.API_KEY_REVOKED -> Icons.Default.Key
        SecurityEventType.BIOMETRIC_ENABLED -> Icons.Default.Fingerprint
        SecurityEventType.BIOMETRIC_DISABLED -> Icons.Default.Fingerprint
        SecurityEventType.LOCATION_ANOMALY -> Icons.Default.LocationOn
        SecurityEventType.MULTIPLE_FAILED_ATTEMPTS -> Icons.Default.Warning
        SecurityEventType.ACCOUNT_TAKEOVER_ATTEMPT -> Icons.Default.Security
    }
}

private fun getDisplayName(severity: SecuritySeverity): String {
    return when (severity) {
        SecuritySeverity.LOW -> "Low"
        SecuritySeverity.MEDIUM -> "Medium"
        SecuritySeverity.HIGH -> "High"
        SecuritySeverity.CRITICAL -> "Critical"
    }
}
package com.avinashpatil.app.monzobank.presentation.ui.settings

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

data class SettingsSection(
    val id: String,
    val title: String,
    val items: List<SettingsItem>
)

data class SettingsItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector,
    val hasSwitch: Boolean = false,
    val switchValue: Boolean = false,
    val hasChevron: Boolean = true,
    val iconColor: Color? = null,
    val isDestructive: Boolean = false,
    val badge: String? = null
)

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String,
    val accountNumber: String,
    val memberSince: String,
    val profilePicture: String? = null
)

data class AppInfo(
    val version: String,
    val buildNumber: String,
    val lastUpdated: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onExportDataClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAboutClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val userProfile = remember { getDummyUserProfile() }
    val settingsSections = remember { getSettingsSections() }
    val appInfo = remember { getDummyAppInfo() }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var marketingEmails by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search settings */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // User Profile Section
            item {
                UserProfileCard(
                    profile = userProfile,
                    onClick = onProfileClick
                )
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    onExportDataClick = onExportDataClick,
                    onSupportClick = onSupportClick,
                    onSecurityClick = onSecurityClick
                )
            }

            // Settings Sections
            settingsSections.forEach { section ->
                item {
                    SettingsSectionHeader(title = section.title)
                }

                items(section.items) { item ->
                    SettingsItemRow(
                        item = item,
                        switchValue = when (item.id) {
                            "notifications" -> notificationsEnabled
                            "biometric" -> biometricEnabled
                            "dark_mode" -> darkModeEnabled
                            "marketing_emails" -> marketingEmails
                            else -> item.switchValue
                        },
                        onSwitchChange = { value ->
                            when (item.id) {
                                "notifications" -> notificationsEnabled = value
                                "biometric" -> biometricEnabled = value
                                "dark_mode" -> darkModeEnabled = value
                                "marketing_emails" -> marketingEmails = value
                            }
                        },
                        onClick = {
                            when (item.id) {
                                "profile" -> onProfileClick()
                                "security" -> onSecurityClick()
                                "notifications_settings" -> onNotificationsClick()
                                "privacy" -> onPrivacyClick()
                                "export_data" -> onExportDataClick()
                                "support" -> onSupportClick()
                                "about" -> onAboutClick()
                                "sign_out" -> onSignOutClick()
                            }
                        }
                    )
                }
            }

            // App Information
            item {
                AppInfoCard(appInfo = appInfo)
            }

            // Sign Out Button
            item {
                SignOutSection(onSignOutClick = onSignOutClick)
            }

            // Footer
            item {
                SettingsFooter()
            }
        }
    }
}

@Composable
fun UserProfileCard(
    profile: UserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                Text(
                    text = "Account: ${profile.accountNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
                Text(
                    text = "Member since ${profile.memberSince}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onExportDataClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSecurityClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                QuickActionButton(
                    icon = Icons.Default.Download,
                    label = "Export Data",
                    onClick = onExportDataClick
                )
                QuickActionButton(
                    icon = Icons.Default.Support,
                    label = "Support",
                    onClick = onSupportClick
                )
                QuickActionButton(
                    icon = Icons.Default.Security,
                    label = "Security",
                    onClick = onSecurityClick
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
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
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
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
fun SettingsSectionHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItemRow(
    item: SettingsItem,
    switchValue: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !item.hasSwitch) { onClick() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconColor ?: if (item.isDestructive) {
                    Color.Red
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (item.isDestructive) {
                            Color.Red
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    item.badge?.let { badge ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                item.subtitle?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (item.hasSwitch) {
                Switch(
                    checked = switchValue,
                    onCheckedChange = onSwitchChange
                )
            } else if (item.hasChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AppInfoCard(
    appInfo: AppInfo
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Monzo Bank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = "Version ${appInfo.version} (${appInfo.buildNumber})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Last updated: ${appInfo.lastUpdated}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SignOutSection(
    onSignOutClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSignOutClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = "Sign Out",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.Red
            )
        }
    }
}

@Composable
fun SettingsFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "© 2024 Monzo Bank. All rights reserved.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Licensed by the Financial Conduct Authority",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Open privacy policy */ }
            )
            
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Open terms */ }
            )
            
            Text(
                text = "Licenses",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { /* Open licenses */ }
            )
        }
    }
}

fun getDummyUserProfile(): UserProfile {
    return UserProfile(
        name = "John Smith",
        email = "john.smith@email.com",
        phone = "+44 7700 900123",
        accountNumber = "12345678",
        memberSince = "January 2020"
    )
}

fun getDummyAppInfo(): AppInfo {
    return AppInfo(
        version = "4.2.1",
        buildNumber = "421",
        lastUpdated = "2 days ago"
    )
}

fun getSettingsSections(): List<SettingsSection> {
    return listOf(
        SettingsSection(
            id = "account",
            title = "Account",
            items = listOf(
                SettingsItem(
                    id = "profile",
                    title = "Profile & Personal Info",
                    subtitle = "Manage your personal details",
                    icon = Icons.Default.Person
                ),
                SettingsItem(
                    id = "cards",
                    title = "Cards & Payments",
                    subtitle = "Manage your cards and payment methods",
                    icon = Icons.Default.CreditCard
                ),
                SettingsItem(
                    id = "accounts",
                    title = "Accounts & Balances",
                    subtitle = "View and manage your accounts",
                    icon = Icons.Default.AccountBalance
                )
            )
        ),
        SettingsSection(
            id = "security",
            title = "Security & Privacy",
            items = listOf(
                SettingsItem(
                    id = "security",
                    title = "Security Center",
                    subtitle = "Manage your security settings",
                    icon = Icons.Default.Security,
                    iconColor = Color.Green
                ),
                SettingsItem(
                    id = "biometric",
                    title = "Biometric Authentication",
                    subtitle = "Use fingerprint or face unlock",
                    icon = Icons.Default.Fingerprint,
                    hasSwitch = true,
                    switchValue = true,
                    hasChevron = false
                ),
                SettingsItem(
                    id = "privacy",
                    title = "Privacy Settings",
                    subtitle = "Control your data and privacy",
                    icon = Icons.Default.PrivacyTip
                ),
                SettingsItem(
                    id = "export_data",
                    title = "Export My Data",
                    subtitle = "Download your account data",
                    icon = Icons.Default.Download
                )
            )
        ),
        SettingsSection(
            id = "notifications",
            title = "Notifications",
            items = listOf(
                SettingsItem(
                    id = "notifications",
                    title = "Push Notifications",
                    subtitle = "Receive app notifications",
                    icon = Icons.Default.Notifications,
                    hasSwitch = true,
                    switchValue = true,
                    hasChevron = false
                ),
                SettingsItem(
                    id = "notifications_settings",
                    title = "Notification Settings",
                    subtitle = "Customize notification preferences",
                    icon = Icons.Default.NotificationsActive
                ),
                SettingsItem(
                    id = "marketing_emails",
                    title = "Marketing Emails",
                    subtitle = "Receive promotional emails",
                    icon = Icons.Default.Email,
                    hasSwitch = true,
                    switchValue = false,
                    hasChevron = false
                )
            )
        ),
        SettingsSection(
            id = "preferences",
            title = "Preferences",
            items = listOf(
                SettingsItem(
                    id = "dark_mode",
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    icon = Icons.Default.DarkMode,
                    hasSwitch = true,
                    switchValue = false,
                    hasChevron = false
                ),
                SettingsItem(
                    id = "language",
                    title = "Language",
                    subtitle = "English (UK)",
                    icon = Icons.Default.Language
                ),
                SettingsItem(
                    id = "currency",
                    title = "Currency",
                    subtitle = "British Pound (GBP)",
                    icon = Icons.Default.AttachMoney
                ),
                SettingsItem(
                    id = "accessibility",
                    title = "Accessibility",
                    subtitle = "Accessibility options",
                    icon = Icons.Default.Accessibility
                )
            )
        ),
        SettingsSection(
            id = "support",
            title = "Support & Feedback",
            items = listOf(
                SettingsItem(
                    id = "support",
                    title = "Help & Support",
                    subtitle = "Get help with your account",
                    icon = Icons.Default.Help
                ),
                SettingsItem(
                    id = "feedback",
                    title = "Send Feedback",
                    subtitle = "Help us improve the app",
                    icon = Icons.Default.Feedback
                ),
                SettingsItem(
                    id = "rate_app",
                    title = "Rate the App",
                    subtitle = "Rate us on the App Store",
                    icon = Icons.Default.Star
                )
            )
        ),
        SettingsSection(
            id = "legal",
            title = "Legal & About",
            items = listOf(
                SettingsItem(
                    id = "about",
                    title = "About Monzo",
                    subtitle = "Learn more about us",
                    icon = Icons.Default.Info
                ),
                SettingsItem(
                    id = "terms",
                    title = "Terms & Conditions",
                    subtitle = "Read our terms of service",
                    icon = Icons.Default.Description
                ),
                SettingsItem(
                    id = "privacy_policy",
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    icon = Icons.Default.Policy
                ),
                SettingsItem(
                    id = "licenses",
                    title = "Open Source Licenses",
                    subtitle = "Third-party software licenses",
                    icon = Icons.Default.Code
                )
            )
        )
    )
}
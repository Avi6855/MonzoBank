package com.avinashpatil.app.monzobank.presentation.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.domain.model.Payee
import com.avinashpatil.app.monzobank.domain.model.PayeeCategory
import com.avinashpatil.app.monzobank.domain.model.displayName
import com.avinashpatil.app.monzobank.domain.model.ScheduledPayment
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillPaymentScreen(
    accounts: List<Account>,
    payees: List<Payee>,
    scheduledPayments: List<ScheduledPayment>,
    onNavigateBack: () -> Unit,
    onAddPayee: () -> Unit,
    onPayeeSelected: (Payee) -> Unit,
    onSchedulePayment: (ScheduledPaymentData) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pay Bills", "Payees", "Scheduled")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Bill Payments",
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
                if (selectedTab == 1) {
                    IconButton(onClick = onAddPayee) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Payee",
                            tint = MonzoCoralPrimary
                        )
                    }
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
            0 -> PayBillsTab(
                accounts = accounts,
                payees = payees,
                onPayeeSelected = onPayeeSelected
            )
            1 -> PayeesTab(
                payees = payees,
                onPayeeSelected = onPayeeSelected,
                onAddPayee = onAddPayee
            )
            2 -> ScheduledPaymentsTab(
                scheduledPayments = scheduledPayments,
                onSchedulePayment = onSchedulePayment
            )
        }
    }
}

@Composable
fun PayBillsTab(
    accounts: List<Account>,
    payees: List<Payee>,
    onPayeeSelected: (Payee) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Actions
        item {
            QuickActionsCard()
        }
        
        // Recent Payees
        if (payees.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Payees",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(payees.take(5)) { payee ->
                PayeeCard(
                    payee = payee,
                    onClick = { onPayeeSelected(payee) }
                )
            }
        }
        
        // Categories
        item {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        item {
            PayeeCategoriesGrid(
                onCategorySelected = { category ->
                    // Filter payees by category
                }
            )
        }
    }
}

@Composable
fun PayeesTab(
    payees: List<Payee>,
    onPayeeSelected: (Payee) -> Unit,
    onAddPayee: () -> Unit
) {
    if (payees.isEmpty()) {
        EmptyPayeesState(onAddPayee = onAddPayee)
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(payees) { payee ->
                PayeeCard(
                    payee = payee,
                    onClick = { onPayeeSelected(payee) },
                    showCategory = true
                )
            }
        }
    }
}

@Composable
fun ScheduledPaymentsTab(
    scheduledPayments: List<ScheduledPayment>,
    onSchedulePayment: (ScheduledPaymentData) -> Unit
) {
    if (scheduledPayments.isEmpty()) {
        EmptyScheduledPaymentsState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(scheduledPayments) { payment ->
                ScheduledPaymentCard(payment = payment)
            }
        }
    }
}

@Composable
fun QuickActionsCard() {
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
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Receipt,
                    label = "Pay Bill",
                    onClick = { }
                )
                
                QuickActionButton(
                    icon = Icons.Default.Schedule,
                    label = "Schedule",
                    onClick = { }
                )
                
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "Add Payee",
                    onClick = { }
                )
                
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = "History",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = MonzoCoralPrimary.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MonzoCoralPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PayeeCard(
    payee: Payee,
    onClick: () -> Unit,
    showCategory: Boolean = false
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
            // Payee Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = payee.initials,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = payee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = payee.accountNumber ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (showCategory) {
                    Text(
                        text = "General",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PayeeCategoriesGrid(
    onCategorySelected: (PayeeCategory) -> Unit
) {
    val categories = PayeeCategory.values()
    
    Column {
        for (i in categories.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryCard(
                    category = categories[i],
                    modifier = Modifier.weight(1f),
                    onClick = { onCategorySelected(categories[i]) }
                )
                
                if (i + 1 < categories.size) {
                    CategoryCard(
                        category = categories[i + 1],
                        modifier = Modifier.weight(1f),
                        onClick = { onCategorySelected(categories[i + 1]) }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            
            if (i + 2 < categories.size) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: PayeeCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = getCategoryColor(category).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                tint = getCategoryColor(category),
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.displayName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = getCategoryColor(category)
            )
        }
    }
}

@Composable
fun ScheduledPaymentCard(
    payment: ScheduledPayment
) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = payment.payeeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "Next: ${payment.nextPaymentDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "£${payment.amount.setScale(2)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MonzoCoralPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = payment.frequency.displayName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = if (payment.isActive) "Active" else "Paused",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (payment.isActive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmptyPayeesState(
    onAddPayee: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Payees Yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Add payees to quickly send money and pay bills",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onAddPayee,
            colors = ButtonDefaults.buttonColors(
                containerColor = MonzoCoralPrimary
            )
        ) {
            Text("Add Your First Payee")
        }
    }
}

@Composable
fun EmptyScheduledPaymentsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Scheduled Payments",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Set up recurring payments to never miss a bill",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPayeeScreen(
    onNavigateBack: () -> Unit,
    onPayeeAdded: (PayeeData) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var sortCode by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PayeeCategory.OTHER) }
    var reference by remember { mutableStateOf("") }
    
    val isValid = name.isNotBlank() && accountNumber.isNotBlank() && sortCode.isNotBlank()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Add Payee",
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
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
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
                            text = "Payee Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Payee Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { accountNumber = it },
                            label = { Text("Account Number") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = sortCode,
                            onValueChange = { sortCode = it },
                            label = { Text("Sort Code") },
                            placeholder = { Text("XX-XX-XX") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        OutlinedTextField(
                            value = reference,
                            onValueChange = { reference = it },
                            label = { Text("Reference (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }
            
            item {
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
                            text = "Category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        PayeeCategory.values().forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCategory = category }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MonzoCoralPrimary
                                    )
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Icon(
                                    imageVector = getCategoryIcon(category),
                                    contentDescription = null,
                                    tint = getCategoryColor(category),
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                    text = category.displayName(),
                    style = MaterialTheme.typography.bodyMedium
                )
                            }
                        }
                    }
                }
            }
            
            item {
                Button(
                    onClick = {
                        val payeeData = PayeeData(
                            name = name,
                            accountNumber = accountNumber,
                            sortCode = sortCode,
                            category = selectedCategory,
                            reference = reference.ifBlank { null }
                        )
                        onPayeeAdded(payeeData)
                    },
                    enabled = isValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text(
                        text = "Add Payee",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

fun getCategoryColor(category: PayeeCategory): Color {
    return when (category) {
        PayeeCategory.UTILITIES -> Color(0xFF2196F3)
        PayeeCategory.RENT_MORTGAGE -> Color(0xFF4CAF50)
        PayeeCategory.INSURANCE -> Color(0xFF9C27B0)
        PayeeCategory.SUBSCRIPTIONS -> Color(0xFFFF9800)
        PayeeCategory.LOANS -> Color(0xFFF44336)
        PayeeCategory.GOVERNMENT -> Color(0xFF607D8B)
        PayeeCategory.CREDIT_CARDS -> Color(0xFFE91E63)
        PayeeCategory.HEALTHCARE -> Color(0xFF00BCD4)
        PayeeCategory.EDUCATION -> Color(0xFF8BC34A)
        PayeeCategory.CHARITY -> Color(0xFFFFEB3B)
        PayeeCategory.FAMILY_FRIENDS -> Color(0xFF03DAC5)
        PayeeCategory.BUSINESS -> Color(0xFF6200EE)
        PayeeCategory.INVESTMENT -> Color(0xFF018786)
        PayeeCategory.OTHER -> Color(0xFF795548)
    }
}

fun getCategoryIcon(category: PayeeCategory): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category) {
        PayeeCategory.UTILITIES -> Icons.Default.ElectricBolt
        PayeeCategory.RENT_MORTGAGE -> Icons.Default.Home
        PayeeCategory.INSURANCE -> Icons.Default.Security
        PayeeCategory.SUBSCRIPTIONS -> Icons.Default.Subscriptions
        PayeeCategory.LOANS -> Icons.Default.AccountBalance
        PayeeCategory.GOVERNMENT -> Icons.Default.AccountBalance
        PayeeCategory.CREDIT_CARDS -> Icons.Default.CreditCard
        PayeeCategory.HEALTHCARE -> Icons.Default.LocalHospital
        PayeeCategory.EDUCATION -> Icons.Default.School
        PayeeCategory.CHARITY -> Icons.Default.Favorite
        PayeeCategory.FAMILY_FRIENDS -> Icons.Default.People
        PayeeCategory.BUSINESS -> Icons.Default.Business
        PayeeCategory.INVESTMENT -> Icons.Default.TrendingUp
        PayeeCategory.OTHER -> Icons.Default.Category
    }
}

data class PayeeData(
    val name: String,
    val accountNumber: String,
    val sortCode: String,
    val category: PayeeCategory,
    val reference: String?
)

data class ScheduledPaymentData(
    val payee: Payee,
    val amount: BigDecimal,
    val frequency: PaymentFrequency,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val reference: String?
)

enum class PaymentFrequency(val displayName: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly")
}
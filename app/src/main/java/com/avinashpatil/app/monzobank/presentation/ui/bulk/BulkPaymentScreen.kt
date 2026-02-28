package com.avinashpatil.app.monzobank.presentation.ui.bulk

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkPaymentScreen(
    accounts: List<Account>,
    payees: List<Payee>,
    onNavigateBack: () -> Unit,
    onBulkPaymentSubmitted: (BulkPaymentData) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Create Batch", "Templates", "History")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Bulk Payments",
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
                        imageVector = Icons.Default.Upload,
                        contentDescription = "Import CSV",
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
            0 -> CreateBatchTab(
                accounts = accounts,
                payees = payees,
                onBulkPaymentSubmitted = onBulkPaymentSubmitted
            )
            1 -> TemplatesTab()
            2 -> HistoryTab()
        }
    }
}

@Composable
fun CreateBatchTab(
    accounts: List<Account>,
    payees: List<Payee>,
    onBulkPaymentSubmitted: (BulkPaymentData) -> Unit
) {
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var batchName by remember { mutableStateOf("") }
    var executionDate by remember { mutableStateOf(LocalDate.now()) }
    var payments by remember { mutableStateOf(listOf<BulkPaymentItem>()) }
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    
    val totalAmount = payments.sumOf { it.amount }
    val isValid = batchName.isNotBlank() && payments.isNotEmpty() && 
            selectedAccount?.balance?.let { it >= totalAmount } == true
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Batch Configuration
        item {
            BatchConfigurationCard(
                accounts = accounts,
                selectedAccount = selectedAccount,
                onAccountChange = { selectedAccount = it },
                batchName = batchName,
                onBatchNameChange = { batchName = it },
                executionDate = executionDate,
                onExecutionDateChange = { executionDate = it }
            )
        }
        
        // Batch Summary
        item {
            BatchSummaryCard(
                totalPayments = payments.size,
                totalAmount = totalAmount,
                selectedAccount = selectedAccount
            )
        }
        
        // Add Payment Button
        item {
            OutlinedButton(
                onClick = { showAddPaymentDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Payment")
            }
        }
        
        // Payment List
        if (payments.isNotEmpty()) {
            item {
                Text(
                    text = "Payments (${payments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(payments) { payment ->
                BulkPaymentItemCard(
                    payment = payment,
                    onEdit = { },
                    onDelete = {
                        payments = payments.filter { it.id != payment.id }
                    }
                )
            }
        }
        
        // Submit Button
        if (payments.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        val bulkPaymentData = BulkPaymentData(
                            batchName = batchName,
                            fromAccountId = selectedAccount!!.id,
                            executionDate = executionDate,
                            payments = payments
                        )
                        onBulkPaymentSubmitted(bulkPaymentData)
                    },
                    enabled = isValid,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text(
                        text = "Submit Batch (${payments.size} payments)",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
    
    // Add Payment Dialog
    if (showAddPaymentDialog) {
        AddPaymentDialog(
            payees = payees,
            onDismiss = { showAddPaymentDialog = false },
            onPaymentAdded = { payment ->
                payments = payments + payment
                showAddPaymentDialog = false
            }
        )
    }
}

@Composable
fun TemplatesTab() {
    // Mock template data
    val templates = remember {
        listOf(
            BulkPaymentTemplate(
                id = "template_1",
                name = "Monthly Supplier Payments",
                description = "Regular payments to suppliers",
                paymentCount = 8,
                totalAmount = BigDecimal("15420.00"),
                lastUsed = LocalDateTime.now().minusDays(30),
                frequency = TemplateFrequency.MONTHLY
            ),
            BulkPaymentTemplate(
                id = "template_2",
                name = "Employee Expense Reimbursements",
                description = "Weekly expense reimbursements",
                paymentCount = 12,
                totalAmount = BigDecimal("3250.00"),
                lastUsed = LocalDateTime.now().minusDays(7),
                frequency = TemplateFrequency.WEEKLY
            ),
            BulkPaymentTemplate(
                id = "template_3",
                name = "Quarterly Contractor Payments",
                description = "Quarterly payments to contractors",
                paymentCount = 5,
                totalAmount = BigDecimal("28750.00"),
                lastUsed = LocalDateTime.now().minusDays(90),
                frequency = TemplateFrequency.QUARTERLY
            )
        )
    }
    
    if (templates.isEmpty()) {
        EmptyTemplatesState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Saved Templates",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(templates) { template ->
                TemplateCard(
                    template = template,
                    onUse = { /* TODO: Implement template usage */ },
                    onEdit = { /* TODO: Implement template editing */ },
                    onDelete = { /* TODO: Implement template deletion */ }
                )
            }
        }
    }
}

@Composable
fun HistoryTab() {
    // Mock history data
    val batches = remember {
        listOf(
            BulkPaymentBatch(
                id = "batch_1",
                name = "Monthly Supplier Payments - Dec 2023",
                totalPayments = 8,
                successfulPayments = 8,
                failedPayments = 0,
                totalAmount = BigDecimal("15420.00"),
                status = BatchStatus.COMPLETED,
                createdAt = LocalDateTime.now().minusDays(5),
                executedAt = LocalDateTime.now().minusDays(5)
            ),
            BulkPaymentBatch(
                id = "batch_2",
                name = "Employee Reimbursements - Week 50",
                totalPayments = 12,
                successfulPayments = 11,
                failedPayments = 1,
                totalAmount = BigDecimal("3250.00"),
                status = BatchStatus.PARTIALLY_FAILED,
                createdAt = LocalDateTime.now().minusDays(12),
                executedAt = LocalDateTime.now().minusDays(12)
            ),
            BulkPaymentBatch(
                id = "batch_3",
                name = "Vendor Payments - Batch 001",
                totalPayments = 15,
                successfulPayments = 0,
                failedPayments = 0,
                totalAmount = BigDecimal("8750.00"),
                status = BatchStatus.SCHEDULED,
                createdAt = LocalDateTime.now().minusHours(2),
                executedAt = null
            )
        )
    }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Batch History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        items(batches) { batch ->
            BatchHistoryCard(
                batch = batch,
                onClick = { /* TODO: Implement batch details view */ }
            )
        }
    }
}

@Composable
fun BatchConfigurationCard(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountChange: (Account) -> Unit,
    batchName: String,
    onBatchNameChange: (String) -> Unit,
    executionDate: LocalDate,
    onExecutionDateChange: (LocalDate) -> Unit
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
            Text(
                text = "Batch Configuration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = batchName,
                onValueChange = onBatchNameChange,
                label = { Text("Batch Name") },
                placeholder = { Text("e.g., Monthly Supplier Payments") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Account Selection
            Text(
                text = "From Account",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            accounts.forEach { account ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAccountChange(account) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAccount?.id == account.id,
                        onClick = { onAccountChange(account) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MonzoCoralPrimary
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = account.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = account.formattedBalance,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Execution Date
            OutlinedTextField(
                value = executionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onValueChange = { },
                label = { Text("Execution Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Date"
                    )
                }
            )
        }
    }
}

@Composable
fun BatchSummaryCard(
    totalPayments: Int,
    totalAmount: BigDecimal,
    selectedAccount: Account?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MonzoCoralPrimary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Batch Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MonzoCoralPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Payments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = totalPayments.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${totalAmount.setScale(2)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MonzoCoralPrimary
                    )
                }
            }
            
            selectedAccount?.let { account ->
                Spacer(modifier = Modifier.height(8.dp))
                
                val remainingBalance = account.balance.subtract(totalAmount)
                val hasEnoughFunds = remainingBalance >= BigDecimal.ZERO
                
                Text(
                    text = if (hasEnoughFunds) {
                        "Remaining balance: £${remainingBalance.setScale(2)}"
                    } else {
                        "Insufficient funds: £${remainingBalance.abs().setScale(2)} short"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (hasEnoughFunds) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun BulkPaymentItemCard(
    payment: BulkPaymentItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = payment.payeeName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = payment.reference,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Text(
                text = "£${payment.amount.setScale(2)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MonzoCoralPrimary
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(18.dp)
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddPaymentDialog(
    payees: List<Payee>,
    onDismiss: () -> Unit,
    onPaymentAdded: (BulkPaymentItem) -> Unit
) {
    var selectedPayee by remember { mutableStateOf<Payee?>(null) }
    var amount by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    
    val isValid = selectedPayee != null && amount.toBigDecimalOrNull() != null &&
            amount.toBigDecimalOrNull()!! > BigDecimal.ZERO && reference.isNotBlank()
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Payment")
        },
        text = {
            Column {
                // Payee Selection
                Text(
                    text = "Select Payee",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                payees.take(3).forEach { payee ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayee = payee }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPayee?.id == payee.id,
                            onClick = { selectedPayee = payee },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MonzoCoralPrimary
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = payee.name,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (£)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Reference") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val payment = BulkPaymentItem(
                        id = "payment_${System.currentTimeMillis()}",
                        payeeId = selectedPayee!!.id,
                        payeeName = selectedPayee!!.name,
                        amount = amount.toBigDecimal(),
                        reference = reference
                    )
                    onPaymentAdded(payment)
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Data classes and enums
data class BulkPaymentData(
    val batchName: String,
    val fromAccountId: String,
    val executionDate: LocalDate,
    val payments: List<BulkPaymentItem>
)

data class BulkPaymentItem(
    val id: String,
    val payeeId: String,
    val payeeName: String,
    val amount: BigDecimal,
    val reference: String
)

data class BulkPaymentTemplate(
    val id: String,
    val name: String,
    val description: String,
    val paymentCount: Int,
    val totalAmount: BigDecimal,
    val lastUsed: LocalDateTime,
    val frequency: TemplateFrequency
)

enum class TemplateFrequency {
    WEEKLY, MONTHLY, QUARTERLY, YEARLY;
    
    val displayName: String
        get() = when (this) {
            WEEKLY -> "Weekly"
            MONTHLY -> "Monthly"
            QUARTERLY -> "Quarterly"
            YEARLY -> "Yearly"
        }
}

data class BulkPaymentBatch(
    val id: String,
    val name: String,
    val totalPayments: Int,
    val successfulPayments: Int,
    val failedPayments: Int,
    val totalAmount: BigDecimal,
    val status: BatchStatus,
    val createdAt: LocalDateTime,
    val executedAt: LocalDateTime?
) {
    val successRate: Double
        get() = if (totalPayments > 0) (successfulPayments.toDouble() / totalPayments) * 100 else 0.0
}

enum class BatchStatus {
    DRAFT, SCHEDULED, PROCESSING, COMPLETED, PARTIALLY_FAILED, FAILED, CANCELLED;
    
    val displayName: String
        get() = when (this) {
            DRAFT -> "Draft"
            SCHEDULED -> "Scheduled"
            PROCESSING -> "Processing"
            COMPLETED -> "Completed"
            PARTIALLY_FAILED -> "Partially Failed"
            FAILED -> "Failed"
            CANCELLED -> "Cancelled"
        }
}

// Empty state composables
@Composable
fun EmptyTemplatesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Templates",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Create payment batches to save them as templates for future use",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TemplateCard(
    template: BulkPaymentTemplate,
    onUse: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = template.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Template",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${template.paymentCount} payments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${template.totalAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Frequency",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = template.frequency.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Edit")
                }
                
                Button(
                    onClick = onUse,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Use Template")
                }
            }
        }
    }
}

@Composable
fun BatchHistoryCard(
    batch: BulkPaymentBatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = batch.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${batch.totalPayments} payments • £${batch.totalAmount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (batch.status) {
                        BatchStatus.COMPLETED -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                        BatchStatus.PARTIALLY_FAILED -> Color(0xFFFF9800).copy(alpha = 0.1f)
                        BatchStatus.FAILED -> Color(0xFFF44336).copy(alpha = 0.1f)
                        BatchStatus.PROCESSING -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        BatchStatus.SCHEDULED -> Color(0xFF9C27B0).copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = batch.status.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = when (batch.status) {
                            BatchStatus.COMPLETED -> Color(0xFF4CAF50)
                            BatchStatus.PARTIALLY_FAILED -> Color(0xFFFF9800)
                            BatchStatus.FAILED -> Color(0xFFF44336)
                            BatchStatus.PROCESSING -> Color(0xFF2196F3)
                            BatchStatus.SCHEDULED -> Color(0xFF9C27B0)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Success Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${batch.successRate.toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (batch.successRate >= 100) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column {
                    Text(
                        text = "Created",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = batch.createdAt.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
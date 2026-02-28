package com.avinashpatil.app.monzobank.presentation.ui.payments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkPaymentScreen(
    onBackClick: () -> Unit,
    onPaymentsComplete: () -> Unit,
    onImportFile: () -> Unit
) {
    var payments by remember { mutableStateOf(getBulkPayments()) }
    var isLoading by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    
    val totalAmount = payments.sumOf { it.amount }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Bulk Payments") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onImportFile) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Import CSV")
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BulkPaymentSummaryCard(
                    paymentCount = payments.size,
                    totalAmount = totalAmount
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Payment")
                    }
                    
                    OutlinedButton(
                        onClick = onImportFile,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Import CSV")
                    }
                }
            }
            
            item {
                Text(
                    text = "Payment List (${payments.size})",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(payments) { payment ->
                BulkPaymentCard(
                    payment = payment,
                    onDelete = {
                        payments = payments.filter { it.id != payment.id }
                    }
                )
            }
            
            if (payments.isNotEmpty()) {
                item {
                    Button(
                        onClick = {
                            isLoading = true
                            // Simulate bulk payment process
                            onPaymentsComplete()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Process ${payments.size} Payments (£${String.format("%.2f", totalAmount)})")
                        }
                    }
                }
            }
            
            item {
                BulkPaymentInfoCard()
            }
        }
    }
    
    if (showAddDialog) {
        AddPaymentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { newPayment ->
                payments = payments + newPayment
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BulkPaymentSummaryCard(
    paymentCount: Int,
    totalAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Payment Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Payments:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = paymentCount.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Amount:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "£${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun BulkPaymentCard(
    payment: BulkPayment,
    onDelete: () -> Unit
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = payment.recipientName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = payment.accountNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (payment.reference.isNotEmpty()) {
                    Text(
                        text = payment.reference,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%.2f", payment.amount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun BulkPaymentInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Bulk Payment Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val info = listOf(
                "Process multiple payments at once to save time",
                "Import payments from CSV files for convenience",
                "All payments are processed securely",
                "You'll receive confirmation for each payment",
                "Failed payments will be highlighted for review"
            )
            
            info.forEach { item ->
                Text(
                    text = "• $item",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun AddPaymentDialog(
    onDismiss: () -> Unit,
    onAdd: (BulkPayment) -> Unit
) {
    var recipientName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }
    var sortCode by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Payment") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = recipientName,
                    onValueChange = { recipientName = it },
                    label = { Text("Recipient Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = sortCode,
                    onValueChange = { sortCode = it },
                    label = { Text("Sort Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = newValue
                        }
                    },
                    label = { Text("Amount (£)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Reference (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newPayment = BulkPayment(
                        id = System.currentTimeMillis().toString(),
                        recipientName = recipientName,
                        accountNumber = accountNumber,
                        sortCode = sortCode,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        reference = reference
                    )
                    onAdd(newPayment)
                },
                enabled = recipientName.isNotEmpty() && accountNumber.isNotEmpty() && 
                         sortCode.isNotEmpty() && amount.isNotEmpty() && 
                         amount.toDoubleOrNull() != null && amount.toDouble() > 0
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

data class BulkPayment(
    val id: String,
    val recipientName: String,
    val accountNumber: String,
    val sortCode: String,
    val amount: Double,
    val reference: String
)

fun getBulkPayments(): List<BulkPayment> {
    return listOf(
        BulkPayment(
            id = "1",
            recipientName = "John Smith",
            accountNumber = "12345678",
            sortCode = "12-34-56",
            amount = 250.00,
            reference = "Invoice #001"
        ),
        BulkPayment(
            id = "2",
            recipientName = "Sarah Johnson",
            accountNumber = "87654321",
            sortCode = "65-43-21",
            amount = 150.50,
            reference = "Freelance work"
        )
    )
}
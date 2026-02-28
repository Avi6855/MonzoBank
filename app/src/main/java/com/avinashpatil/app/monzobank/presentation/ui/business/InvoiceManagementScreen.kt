package com.avinashpatil.app.monzobank.presentation.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceManagementScreen(
    onBackClick: () -> Unit,
    onInvoiceClick: (String) -> Unit,
    onEditInvoiceClick: (String) -> Unit,
    onDeleteInvoiceClick: (String) -> Unit,
    onSendInvoiceClick: (String) -> Unit,
    onDuplicateInvoiceClick: (String) -> Unit
) {
    val invoices = remember { getDummyInvoiceManagementData() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Management") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add new invoice */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Invoice")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(invoices) { invoice ->
                InvoiceManagementCard(
                    invoice = invoice,
                    onInvoiceClick = { onInvoiceClick(invoice.id) },
                    onEditClick = { onEditInvoiceClick(invoice.id) },
                    onDeleteClick = { onDeleteInvoiceClick(invoice.id) },
                    onSendClick = { onSendInvoiceClick(invoice.id) },
                    onDuplicateClick = { onDuplicateInvoiceClick(invoice.id) }
                )
            }
        }
    }
}

@Composable
fun InvoiceManagementCard(
    invoice: InvoiceManagement,
    onInvoiceClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSendClick: () -> Unit,
    onDuplicateClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInvoiceClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Invoice #${invoice.number}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = invoice.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Surface(
                    color = getInvoiceStatusColor(invoice.status).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = invoice.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = getInvoiceStatusColor(invoice.status),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount: £${invoice.amount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Due: ${invoice.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                
                OutlinedButton(
                    onClick = onSendClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Send")
                }
                
                OutlinedButton(
                    onClick = onDuplicateClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
            }
        }
    }
}

data class InvoiceManagement(
    val id: String,
    val number: String,
    val clientName: String,
    val amount: String,
    val status: String,
    val dueDate: String,
    val createdDate: String
)

fun getInvoiceStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "paid" -> Color(0xFF4CAF50)
        "pending" -> Color(0xFFFF9800)
        "overdue" -> Color(0xFFF44336)
        "draft" -> Color(0xFF9E9E9E)
        else -> Color(0xFF2196F3)
    }
}

fun getDummyInvoiceManagementData(): List<InvoiceManagement> {
    return listOf(
        InvoiceManagement(
            id = "inv_001",
            number = "INV-2024-001",
            clientName = "ABC Corporation",
            amount = "2,500.00",
            status = "Paid",
            dueDate = "15 Jan 2024",
            createdDate = "01 Jan 2024"
        ),
        InvoiceManagement(
            id = "inv_002",
            number = "INV-2024-002",
            clientName = "XYZ Ltd",
            amount = "1,750.00",
            status = "Pending",
            dueDate = "20 Jan 2024",
            createdDate = "05 Jan 2024"
        ),
        InvoiceManagement(
            id = "inv_003",
            number = "INV-2024-003",
            clientName = "Tech Solutions",
            amount = "3,200.00",
            status = "Overdue",
            dueDate = "10 Jan 2024",
            createdDate = "25 Dec 2023"
        )
    )
}
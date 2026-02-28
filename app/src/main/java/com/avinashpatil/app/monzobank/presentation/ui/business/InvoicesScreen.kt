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

data class Invoice(
    val id: String,
    val invoiceNumber: String,
    val clientName: String,
    val clientEmail: String,
    val amount: Double,
    val currency: String,
    val issueDate: String,
    val dueDate: String,
    val status: InvoiceStatus,
    val description: String,
    val items: List<InvoiceItem>
)

data class InvoiceItem(
    val id: String,
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val total: Double
)

data class InvoiceTemplate(
    val id: String,
    val name: String,
    val description: String,
    val isDefault: Boolean
)

enum class InvoiceStatus {
    DRAFT, SENT, VIEWED, PAID, OVERDUE, CANCELLED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen(
    onBackClick: () -> Unit,
    onInvoiceClick: (String) -> Unit,
    onCreateInvoiceClick: () -> Unit,
    onTemplateClick: (String) -> Unit
) {
    val invoices = remember { getDummyInvoices() }
    val templates = remember { getDummyInvoiceTemplates() }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    val tabs = listOf("All Invoices", "Templates")
    val filters = listOf("All", "Draft", "Sent", "Paid", "Overdue")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoices") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateInvoiceClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Invoice")
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
                0 -> InvoicesContent(
                    invoices = invoices,
                    filters = filters,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    onInvoiceClick = onInvoiceClick
                )
                1 -> TemplatesContent(
                    templates = templates,
                    onTemplateClick = onTemplateClick,
                    onCreateTemplateClick = { /* Create template */ }
                )
            }
        }
    }
}

@Composable
fun InvoicesContent(
    invoices: List<Invoice>,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onInvoiceClick: (String) -> Unit
) {
    val filteredInvoices = when (selectedFilter) {
        "All" -> invoices
        "Draft" -> invoices.filter { it.status == InvoiceStatus.DRAFT }
        "Sent" -> invoices.filter { it.status == InvoiceStatus.SENT }
        "Paid" -> invoices.filter { it.status == InvoiceStatus.PAID }
        "Overdue" -> invoices.filter { it.status == InvoiceStatus.OVERDUE }
        else -> invoices
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Invoice Summary
        item {
            InvoiceSummaryCard(invoices = invoices)
        }

        // Filter Chips
        item {
            FilterChipsRow(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
        }

        // Invoices List
        if (filteredInvoices.isEmpty()) {
            item {
                EmptyInvoicesState(selectedFilter = selectedFilter)
            }
        } else {
            items(filteredInvoices) { invoice ->
                InvoiceCard(
                    invoice = invoice,
                    onClick = { onInvoiceClick(invoice.id) }
                )
            }
        }
    }
}

@Composable
fun TemplatesContent(
    templates: List<InvoiceTemplate>,
    onTemplateClick: (String) -> Unit,
    onCreateTemplateClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Invoice Templates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onCreateTemplateClick) {
                    Text("Create New")
                }
            }
        }

        item {
            Text(
                text = "Save time by creating reusable invoice templates",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (templates.isEmpty()) {
            item {
                EmptyTemplatesState(onCreateClick = onCreateTemplateClick)
            }
        } else {
            items(templates) { template ->
                InvoiceTemplateCard(
                    template = template,
                    onClick = { onTemplateClick(template.id) }
                )
            }
        }
    }
}

@Composable
fun InvoiceSummaryCard(invoices: List<Invoice>) {
    val totalAmount = invoices.sumOf { it.amount }
    val paidAmount = invoices.filter { it.status == InvoiceStatus.PAID }.sumOf { it.amount }
    val pendingAmount = invoices.filter { it.status in listOf(InvoiceStatus.SENT, InvoiceStatus.VIEWED) }.sumOf { it.amount }
    val overdueAmount = invoices.filter { it.status == InvoiceStatus.OVERDUE }.sumOf { it.amount }

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
            Text(
                text = "Invoice Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Total",
                    amount = totalAmount,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                SummaryItem(
                    title = "Paid",
                    amount = paidAmount,
                    color = Color.Green
                )
                SummaryItem(
                    title = "Pending",
                    amount = pendingAmount,
                    color = Color(0xFFFF9800)
                )
                SummaryItem(
                    title = "Overdue",
                    amount = overdueAmount,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    amount: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "£${String.format("%,.2f", amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = color.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun FilterChipsRow(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                selected = filter == selectedFilter
            )
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: Invoice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = invoice.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InvoiceStatusChip(status = invoice.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = invoice.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${invoice.currency} ${String.format("%,.2f", invoice.amount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Due Date",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = invoice.dueDate,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceTemplateCard(
    template: InvoiceTemplate,
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
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (template.isDefault) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Default",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = template.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }
    }
}

@Composable
fun InvoiceStatusChip(status: InvoiceStatus) {
    val (color, text) = when (status) {
        InvoiceStatus.DRAFT -> Color.Gray to "Draft"
        InvoiceStatus.SENT -> Color.Blue to "Sent"
        InvoiceStatus.VIEWED -> Color(0xFFFF9800) to "Viewed"
        InvoiceStatus.PAID -> Color.Green to "Paid"
        InvoiceStatus.OVERDUE -> Color.Red to "Overdue"
        InvoiceStatus.CANCELLED -> Color.Gray to "Cancelled"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
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
fun EmptyInvoicesState(selectedFilter: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
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
                text = if (selectedFilter == "All") "No Invoices" else "No $selectedFilter Invoices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (selectedFilter == "All") 
                    "Create your first invoice to get started with billing your clients." 
                else 
                    "No invoices found with $selectedFilter status.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyTemplatesState(onCreateClick: () -> Unit) {
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
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Templates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create invoice templates to save time when billing recurring clients.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreateClick) {
                Text("Create Template")
            }
        }
    }
}

fun getDummyInvoices(): List<Invoice> {
    return listOf(
        Invoice(
            id = "INV001",
            invoiceNumber = "INV-2024-001",
            clientName = "ABC Corporation",
            clientEmail = "billing@abccorp.com",
            amount = 5500.00,
            currency = "£",
            issueDate = "Oct 1, 2024",
            dueDate = "Oct 31, 2024",
            status = InvoiceStatus.PAID,
            description = "Web development services for Q3 2024",
            items = listOf(
                InvoiceItem("1", "Frontend Development", 40, 100.0, 4000.0),
                InvoiceItem("2", "Backend Integration", 15, 100.0, 1500.0)
            )
        ),
        Invoice(
            id = "INV002",
            invoiceNumber = "INV-2024-002",
            clientName = "XYZ Ltd",
            clientEmail = "accounts@xyzltd.com",
            amount = 3250.00,
            currency = "£",
            issueDate = "Oct 15, 2024",
            dueDate = "Nov 15, 2024",
            status = InvoiceStatus.SENT,
            description = "Mobile app consultation and design",
            items = listOf(
                InvoiceItem("1", "UI/UX Design", 25, 80.0, 2000.0),
                InvoiceItem("2", "Consultation", 25, 50.0, 1250.0)
            )
        ),
        Invoice(
            id = "INV003",
            invoiceNumber = "INV-2024-003",
            clientName = "DEF Industries",
            clientEmail = "finance@defindustries.com",
            amount = 7500.00,
            currency = "£",
            issueDate = "Sep 20, 2024",
            dueDate = "Oct 20, 2024",
            status = InvoiceStatus.OVERDUE,
            description = "E-commerce platform development",
            items = listOf(
                InvoiceItem("1", "Platform Development", 50, 120.0, 6000.0),
                InvoiceItem("2", "Payment Integration", 15, 100.0, 1500.0)
            )
        ),
        Invoice(
            id = "INV004",
            invoiceNumber = "INV-2024-004",
            clientName = "GHI Startup",
            clientEmail = "billing@ghistartup.com",
            amount = 2750.00,
            currency = "£",
            issueDate = "Oct 25, 2024",
            dueDate = "Nov 25, 2024",
            status = InvoiceStatus.DRAFT,
            description = "MVP development and testing",
            items = listOf(
                InvoiceItem("1", "MVP Development", 20, 100.0, 2000.0),
                InvoiceItem("2", "Testing & QA", 15, 50.0, 750.0)
            )
        )
    )
}

fun getDummyInvoiceTemplates(): List<InvoiceTemplate> {
    return listOf(
        InvoiceTemplate(
            id = "TEMP001",
            name = "Standard Service Invoice",
            description = "Default template for service-based invoicing",
            isDefault = true
        ),
        InvoiceTemplate(
            id = "TEMP002",
            name = "Product Sales Invoice",
            description = "Template for product sales with tax calculations",
            isDefault = false
        ),
        InvoiceTemplate(
            id = "TEMP003",
            name = "Consulting Invoice",
            description = "Hourly-based consulting services template",
            isDefault = false
        )
    )
}
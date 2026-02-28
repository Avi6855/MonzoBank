package com.avinashpatil.app.monzobank.presentation.ui.business

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollSystemScreen(
    onBackClick: () -> Unit,
    onEmployeeClick: (String) -> Unit,
    onPayrollRunClick: (String) -> Unit,
    onReportClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onTaxCalculationClick: () -> Unit
) {
    val employees = remember { getDummyPayrollSystemEmployees() }
    val payrollRuns = remember { getDummyPayrollRunsData() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payroll System") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Payroll Summary Card
            item {
                PayrollSummaryCard(
                    onTaxCalculationClick = onTaxCalculationClick,
                    onReportClick = onReportClick
                )
            }
            
            // Recent Payroll Runs
            item {
                Text(
                    text = "Recent Payroll Runs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            items(payrollRuns.take(3)) { payrollRun ->
                PayrollSystemRunCard(
                    payrollRun = payrollRun,
                    onClick = { onPayrollRunClick(payrollRun.id) }
                )
            }
            
            // Employees Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Employees (${employees.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = { /* View all employees */ }) {
                        Text("View All")
                    }
                }
            }
            
            items(employees.take(5)) { employee ->
                PayrollEmployeeCard(
                    employee = employee,
                    onClick = { onEmployeeClick(employee.id) }
                )
            }
        }
    }
}

@Composable
fun PayrollSummaryCard(
    onTaxCalculationClick: () -> Unit,
    onReportClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This Month's Payroll",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PayrollSummaryItem(
                    label = "Total Gross",
                    value = "£45,250"
                )
                PayrollSummaryItem(
                    label = "Total Tax",
                    value = "£9,050"
                )
                PayrollSummaryItem(
                    label = "Net Pay",
                    value = "£36,200"
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTaxCalculationClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tax Calc")
                }
                
                OutlinedButton(
                    onClick = { onReportClick("monthly") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reports")
                }
            }
        }
    }
}

@Composable
fun PayrollSummaryItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun PayrollSystemRunCard(
    payrollRun: PayrollSystemRun,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = payrollRun.period,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${payrollRun.employeeCount} employees • ${payrollRun.processedDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${payrollRun.totalAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    color = getPayrollStatusColor(payrollRun.status).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = payrollRun.status,
                        style = MaterialTheme.typography.labelSmall,
                        color = getPayrollStatusColor(payrollRun.status),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PayrollEmployeeCard(
    employee: Employee,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
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
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.initials,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${employee.position} • ${employee.department}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${employee.salary}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Monthly",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

data class PayrollSystemRun(
    val id: String,
    val period: String,
    val employeeCount: Int,
    val totalAmount: String,
    val status: String,
    val processedDate: String
)

data class Employee(
    val id: String,
    val name: String,
    val initials: String,
    val position: String,
    val department: String,
    val salary: String,
    val email: String
)

fun getPayrollStatusColor(status: String): Color {
    return when (status.lowercase()) {
        "completed" -> Color(0xFF4CAF50)
        "processing" -> Color(0xFFFF9800)
        "pending" -> Color(0xFF2196F3)
        "failed" -> Color(0xFFF44336)
        else -> Color(0xFF9E9E9E)
    }
}

fun getDummyPayrollRunsData(): List<PayrollSystemRun> {
    return listOf(
        PayrollSystemRun(
            id = "pr_001",
            period = "January 2024",
            employeeCount = 15,
            totalAmount = "36,200",
            status = "Completed",
            processedDate = "31 Jan 2024"
        ),
        PayrollSystemRun(
            id = "pr_002",
            period = "December 2023",
            employeeCount = 14,
            totalAmount = "34,800",
            status = "Completed",
            processedDate = "31 Dec 2023"
        ),
        PayrollSystemRun(
            id = "pr_003",
            period = "November 2023",
            employeeCount = 14,
            totalAmount = "34,800",
            status = "Completed",
            processedDate = "30 Nov 2023"
        )
    )
}

fun getDummyPayrollSystemEmployees(): List<Employee> {
    return listOf(
        Employee(
            id = "emp_001",
            name = "John Smith",
            initials = "JS",
            position = "Software Engineer",
            department = "Engineering",
            salary = "3,500",
            email = "john.smith@company.com"
        ),
        Employee(
            id = "emp_002",
            name = "Sarah Johnson",
            initials = "SJ",
            position = "Product Manager",
            department = "Product",
            salary = "4,200",
            email = "sarah.johnson@company.com"
        ),
        Employee(
            id = "emp_003",
            name = "Mike Davis",
            initials = "MD",
            position = "Designer",
            department = "Design",
            salary = "3,200",
            email = "mike.davis@company.com"
        ),
        Employee(
            id = "emp_004",
            name = "Emily Brown",
            initials = "EB",
            position = "Marketing Manager",
            department = "Marketing",
            salary = "3,800",
            email = "emily.brown@company.com"
        ),
        Employee(
            id = "emp_005",
            name = "David Wilson",
            initials = "DW",
            position = "Sales Representative",
            department = "Sales",
            salary = "2,800",
            email = "david.wilson@company.com"
        )
    )
}
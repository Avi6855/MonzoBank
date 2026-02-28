package com.avinashpatil.app.monzobank.presentation.ui.business

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

data class PayrollEmployee(
    val id: String,
    val name: String,
    val email: String,
    val position: String,
    val department: String,
    val salary: Double,
    val employeeId: String,
    val startDate: String,
    val status: EmployeeStatus,
    val paymentMethod: PaymentMethod
)

data class PayrollRun(
    val id: String,
    val payPeriod: String,
    val payDate: String,
    val totalAmount: Double,
    val employeeCount: Int,
    val status: PayrollStatus,
    val createdDate: String
)

data class PayrollSummary(
    val totalEmployees: Int,
    val totalSalaries: Double,
    val totalTaxes: Double,
    val totalBenefits: Double,
    val netPayroll: Double
)

data class PayStub(
    val id: String,
    val employeeId: String,
    val employeeName: String,
    val payPeriod: String,
    val grossPay: Double,
    val taxes: Double,
    val deductions: Double,
    val netPay: Double,
    val payDate: String
)

enum class EmployeeStatus {
    ACTIVE, INACTIVE, ON_LEAVE, TERMINATED
}

enum class PaymentMethod {
    BANK_TRANSFER, CHECK, DIRECT_DEPOSIT
}

enum class PayrollStatus {
    DRAFT, PROCESSING, COMPLETED, FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayrollScreen(
    onBackClick: () -> Unit,
    onEmployeeClick: (String) -> Unit,
    onPayrollRunClick: (String) -> Unit,
    onAddEmployeeClick: () -> Unit,
    onRunPayrollClick: () -> Unit
) {
    val employees = remember { getDummyPayrollEmployees() }
    val payrollRuns = remember { getDummyPayrollRuns() }
    val payrollSummary = remember { getDummyPayrollSummary() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Employees", "Payroll Runs")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payroll Management") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Reports */ }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reports")
                    }
                    IconButton(onClick = { /* Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            when (selectedTab) {
                1 -> FloatingActionButton(
                    onClick = onAddEmployeeClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add Employee")
                }
                2 -> FloatingActionButton(
                    onClick = onRunPayrollClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run Payroll")
                }
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
                0 -> PayrollOverviewContent(
                    summary = payrollSummary,
                    recentPayrollRuns = payrollRuns.take(3),
                    onPayrollRunClick = onPayrollRunClick,
                    onRunPayrollClick = onRunPayrollClick
                )
                1 -> EmployeesContent(
                    employees = employees,
                    onEmployeeClick = onEmployeeClick
                )
                2 -> PayrollRunsContent(
                    payrollRuns = payrollRuns,
                    onPayrollRunClick = onPayrollRunClick
                )
            }
        }
    }
}

@Composable
fun PayrollOverviewContent(
    summary: PayrollSummary,
    recentPayrollRuns: List<PayrollRun>,
    onPayrollRunClick: (String) -> Unit,
    onRunPayrollClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Payroll Summary
        item {
            PayrollSummaryCard(summary = summary)
        }

        // Quick Actions
        item {
            PayrollQuickActions(onRunPayrollClick = onRunPayrollClick)
        }

        // Recent Payroll Runs
        item {
            Text(
                text = "Recent Payroll Runs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        if (recentPayrollRuns.isEmpty()) {
            item {
                EmptyPayrollRunsState()
            }
        } else {
            items(recentPayrollRuns) { payrollRun ->
                PayrollRunCard(
                    payrollRun = payrollRun,
                    onClick = { onPayrollRunClick(payrollRun.id) }
                )
            }
        }
    }
}

@Composable
fun EmployeesContent(
    employees: List<PayrollEmployee>,
    onEmployeeClick: (String) -> Unit
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
                    text = "Employees (${employees.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* Filter employees */ }) {
                    Text("Filter")
                }
            }
        }

        if (employees.isEmpty()) {
            item {
                EmptyEmployeesState()
            }
        } else {
            items(employees) { employee ->
                EmployeeCard(
                    employee = employee,
                    onClick = { onEmployeeClick(employee.id) }
                )
            }
        }
    }
}

@Composable
fun PayrollRunsContent(
    payrollRuns: List<PayrollRun>,
    onPayrollRunClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Payroll History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (payrollRuns.isEmpty()) {
            item {
                EmptyPayrollRunsState()
            }
        } else {
            items(payrollRuns) { payrollRun ->
                PayrollRunCard(
                    payrollRun = payrollRun,
                    onClick = { onPayrollRunClick(payrollRun.id) }
                )
            }
        }
    }
}

@Composable
fun PayrollSummaryCard(summary: PayrollSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Payroll Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PayrollSummaryItem(
                    title = "Employees",
                    value = summary.totalEmployees.toString(),
                    icon = Icons.Default.People
                )
                PayrollSummaryItem(
                    title = "Gross Pay",
                    value = "£${String.format("%,.0f", summary.totalSalaries)}",
                    icon = Icons.Default.AttachMoney
                )
                PayrollSummaryItem(
                    title = "Net Payroll",
                    value = "£${String.format("%,.0f", summary.netPayroll)}",
                    icon = Icons.Default.AccountBalance
                )
            }
        }
    }
}

@Composable
fun PayrollSummaryItem(
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
fun PayrollQuickActions(onRunPayrollClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Run Payroll",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRunPayrollClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start")
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reports",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Generate reports */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate")
                }
            }
        }
    }
}

@Composable
fun EmployeeCard(
    employee: PayrollEmployee,
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${employee.position} • ${employee.department}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "ID: ${employee.employeeId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%,.0f", employee.salary)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                EmployeeStatusChip(status = employee.status)
            }
        }
    }
}

@Composable
fun PayrollRunCard(
    payrollRun: PayrollRun,
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
                        text = payrollRun.payPeriod,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Pay Date: ${payrollRun.payDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                PayrollStatusChip(status = payrollRun.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%,.2f", payrollRun.totalAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Employees",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = payrollRun.employeeCount.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EmployeeStatusChip(status: EmployeeStatus) {
    val (color, text) = when (status) {
        EmployeeStatus.ACTIVE -> Color.Green to "Active"
        EmployeeStatus.INACTIVE -> Color.Gray to "Inactive"
        EmployeeStatus.ON_LEAVE -> Color(0xFFFF9800) to "On Leave"
        EmployeeStatus.TERMINATED -> Color.Red to "Terminated"
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
fun PayrollStatusChip(status: PayrollStatus) {
    val (color, text) = when (status) {
        PayrollStatus.DRAFT -> Color.Gray to "Draft"
        PayrollStatus.PROCESSING -> Color(0xFFFF9800) to "Processing"
        PayrollStatus.COMPLETED -> Color.Green to "Completed"
        PayrollStatus.FAILED -> Color.Red to "Failed"
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
fun EmptyEmployeesState() {
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
                imageVector = Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Employees",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add your first employee to start managing payroll.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyPayrollRunsState() {
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
                imageVector = Icons.Default.Payment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Payroll Runs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Run your first payroll to see the history here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummyPayrollEmployees(): List<PayrollEmployee> {
    return listOf(
        PayrollEmployee(
            id = "EMP001",
            name = "John Smith",
            email = "john.smith@company.com",
            position = "Software Engineer",
            department = "Engineering",
            salary = 65000.0,
            employeeId = "ENG001",
            startDate = "Jan 15, 2023",
            status = EmployeeStatus.ACTIVE,
            paymentMethod = PaymentMethod.DIRECT_DEPOSIT
        ),
        PayrollEmployee(
            id = "EMP002",
            name = "Sarah Johnson",
            email = "sarah.johnson@company.com",
            position = "Product Manager",
            department = "Product",
            salary = 75000.0,
            employeeId = "PRD001",
            startDate = "Mar 10, 2023",
            status = EmployeeStatus.ACTIVE,
            paymentMethod = PaymentMethod.DIRECT_DEPOSIT
        ),
        PayrollEmployee(
            id = "EMP003",
            name = "Mike Davis",
            email = "mike.davis@company.com",
            position = "UX Designer",
            department = "Design",
            salary = 58000.0,
            employeeId = "DES001",
            startDate = "May 20, 2023",
            status = EmployeeStatus.ACTIVE,
            paymentMethod = PaymentMethod.BANK_TRANSFER
        ),
        PayrollEmployee(
            id = "EMP004",
            name = "Emily Wilson",
            email = "emily.wilson@company.com",
            position = "Marketing Specialist",
            department = "Marketing",
            salary = 52000.0,
            employeeId = "MKT001",
            startDate = "Jul 5, 2023",
            status = EmployeeStatus.ON_LEAVE,
            paymentMethod = PaymentMethod.DIRECT_DEPOSIT
        ),
        PayrollEmployee(
            id = "EMP005",
            name = "David Brown",
            email = "david.brown@company.com",
            position = "Sales Manager",
            department = "Sales",
            salary = 68000.0,
            employeeId = "SAL001",
            startDate = "Sep 12, 2023",
            status = EmployeeStatus.ACTIVE,
            paymentMethod = PaymentMethod.DIRECT_DEPOSIT
        )
    )
}

fun getDummyPayrollRuns(): List<PayrollRun> {
    return listOf(
        PayrollRun(
            id = "PR001",
            payPeriod = "October 2024",
            payDate = "Oct 31, 2024",
            totalAmount = 27400.0,
            employeeCount = 5,
            status = PayrollStatus.COMPLETED,
            createdDate = "Oct 25, 2024"
        ),
        PayrollRun(
            id = "PR002",
            payPeriod = "September 2024",
            payDate = "Sep 30, 2024",
            totalAmount = 26800.0,
            employeeCount = 5,
            status = PayrollStatus.COMPLETED,
            createdDate = "Sep 25, 2024"
        ),
        PayrollRun(
            id = "PR003",
            payPeriod = "August 2024",
            payDate = "Aug 31, 2024",
            totalAmount = 26200.0,
            employeeCount = 4,
            status = PayrollStatus.COMPLETED,
            createdDate = "Aug 25, 2024"
        ),
        PayrollRun(
            id = "PR004",
            payPeriod = "November 2024",
            payDate = "Nov 30, 2024",
            totalAmount = 28000.0,
            employeeCount = 5,
            status = PayrollStatus.PROCESSING,
            createdDate = "Nov 25, 2024"
        )
    )
}

fun getDummyPayrollSummary(): PayrollSummary {
    return PayrollSummary(
        totalEmployees = 5,
        totalSalaries = 318000.0,
        totalTaxes = 63600.0,
        totalBenefits = 31800.0,
        netPayroll = 222600.0
    )
}
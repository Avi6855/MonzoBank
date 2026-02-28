package com.avinashpatil.app.monzobank.presentation.ui.loans

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Loan(
    val id: String,
    val type: LoanType,
    val amount: Double,
    val remainingAmount: Double,
    val interestRate: Double,
    val monthlyPayment: Double,
    val nextPaymentDate: String,
    val status: LoanStatus,
    val termMonths: Int,
    val remainingMonths: Int
)

enum class LoanType {
    PERSONAL, MORTGAGE, AUTO, BUSINESS, STUDENT
}

enum class LoanStatus {
    ACTIVE, PENDING, COMPLETED, OVERDUE
}

data class LoanProduct(
    val id: String,
    val name: String,
    val type: LoanType,
    val minAmount: Double,
    val maxAmount: Double,
    val interestRate: Double,
    val maxTerm: Int,
    val description: String,
    val features: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoansScreen(
    onBackClick: () -> Unit,
    onLoanClick: (Loan) -> Unit,
    onApplyLoanClick: () -> Unit,
    onCreditScoreClick: () -> Unit
) {
    val loans = remember { getDummyLoans() }
    val loanProducts = remember { getDummyLoanProducts() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Loans", "Apply", "Calculator")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loans & Credit") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onCreditScoreClick) {
                        Icon(Icons.Default.Assessment, contentDescription = "Credit Score")
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
                0 -> MyLoansContent(
                    loans = loans,
                    onLoanClick = onLoanClick,
                    onCreditScoreClick = onCreditScoreClick
                )
                1 -> ApplyLoanContent(
                    loanProducts = loanProducts,
                    onApplyClick = onApplyLoanClick
                )
                2 -> LoanCalculatorContent()
            }
        }
    }
}

@Composable
fun MyLoansContent(
    loans: List<Loan>,
    onLoanClick: (Loan) -> Unit,
    onCreditScoreClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Credit Score Card
        item {
            CreditScoreCard(onCreditScoreClick = onCreditScoreClick)
        }

        if (loans.isEmpty()) {
            item {
                EmptyLoansState()
            }
        } else {
            // Loan Summary
            item {
                LoanSummaryCard(loans = loans)
            }

            // Active Loans
            item {
                Text(
                    text = "Your Loans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(loans) { loan ->
                LoanCard(
                    loan = loan,
                    onClick = { onLoanClick(loan) }
                )
            }
        }
    }
}

@Composable
fun ApplyLoanContent(
    loanProducts: List<LoanProduct>,
    onApplyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Loan Products",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(loanProducts) { product ->
            LoanProductCard(
                product = product,
                onApplyClick = onApplyClick
            )
        }
    }
}

@Composable
fun LoanCalculatorContent() {
    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var loanTerm by remember { mutableStateOf("") }
    var monthlyPayment by remember { mutableStateOf(0.0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Loan Calculator",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = loanAmount,
                        onValueChange = { loanAmount = it },
                        label = { Text("Loan Amount (£)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        label = { Text("Interest Rate (%)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = loanTerm,
                        onValueChange = { loanTerm = it },
                        label = { Text("Loan Term (months)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val amount = loanAmount.toDoubleOrNull() ?: 0.0
                            val rate = (interestRate.toDoubleOrNull() ?: 0.0) / 100 / 12
                            val term = loanTerm.toIntOrNull() ?: 0
                            
                            if (amount > 0 && rate > 0 && term > 0) {
                                monthlyPayment = calculateMonthlyPayment(amount, rate, term)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calculate")
                    }

                    if (monthlyPayment > 0) {
                        Divider()
                        Text(
                            text = "Monthly Payment: £${String.format("%.2f", monthlyPayment)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreditScoreCard(onCreditScoreClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCreditScoreClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Credit Score",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "742",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Excellent",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Green
                )
            }
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LoanSummaryCard(loans: List<Loan>) {
    val totalDebt = loans.sumOf { it.remainingAmount }
    val totalMonthlyPayment = loans.sumOf { it.monthlyPayment }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Loan Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Debt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£${String.format("%.2f", totalDebt)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Monthly Payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£${String.format("%.2f", totalMonthlyPayment)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Active Loans",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = loans.size.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun LoanCard(
    loan: Loan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getLoanTypeName(loan.type),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "£${String.format("%.2f", loan.amount)} loan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LoanStatusChip(status = loan.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%.2f", loan.remainingAmount)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Monthly Payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%.2f", loan.monthlyPayment)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Next Payment",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = loan.nextPaymentDate,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar
            val progress = (loan.amount - loan.remainingAmount) / loan.amount
            LinearProgressIndicator(
                progress = progress.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LoanProductCard(
    product: LoanProduct,
    onApplyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Amount Range",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%.0f", product.minAmount)} - £${String.format("%.0f", product.maxAmount)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Interest Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${String.format("%.2f", product.interestRate)}% APR",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Features:",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            product.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onApplyClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Apply Now")
            }
        }
    }
}

@Composable
fun LoanStatusChip(status: LoanStatus) {
    val (color, text) = when (status) {
        LoanStatus.ACTIVE -> Color.Green to "Active"
        LoanStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        LoanStatus.COMPLETED -> Color.Blue to "Completed"
        LoanStatus.OVERDUE -> Color.Red to "Overdue"
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
fun EmptyLoansState() {
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
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Loans",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Apply for a loan to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getLoanTypeName(type: LoanType): String {
    return when (type) {
        LoanType.PERSONAL -> "Personal Loan"
        LoanType.MORTGAGE -> "Mortgage"
        LoanType.AUTO -> "Auto Loan"
        LoanType.BUSINESS -> "Business Loan"
        LoanType.STUDENT -> "Student Loan"
    }
}

fun calculateMonthlyPayment(principal: Double, monthlyRate: Double, termMonths: Int): Double {
    return if (monthlyRate == 0.0) {
        principal / termMonths
    } else {
        val factor = Math.pow(1 + monthlyRate, termMonths.toDouble())
        principal * (monthlyRate * factor) / (factor - 1)
    }
}

fun getDummyLoans(): List<Loan> {
    return listOf(
        Loan(
            id = "1",
            type = LoanType.PERSONAL,
            amount = 15000.0,
            remainingAmount = 8500.0,
            interestRate = 5.9,
            monthlyPayment = 287.50,
            nextPaymentDate = "15 Oct",
            status = LoanStatus.ACTIVE,
            termMonths = 60,
            remainingMonths = 32
        ),
        Loan(
            id = "2",
            type = LoanType.AUTO,
            amount = 25000.0,
            remainingAmount = 18750.0,
            interestRate = 3.2,
            monthlyPayment = 445.20,
            nextPaymentDate = "20 Oct",
            status = LoanStatus.ACTIVE,
            termMonths = 72,
            remainingMonths = 48
        )
    )
}

fun getDummyLoanProducts(): List<LoanProduct> {
    return listOf(
        LoanProduct(
            id = "1",
            name = "Personal Loan",
            type = LoanType.PERSONAL,
            minAmount = 1000.0,
            maxAmount = 50000.0,
            interestRate = 5.9,
            maxTerm = 84,
            description = "Flexible personal loan for any purpose",
            features = listOf(
                "No collateral required",
                "Fixed interest rate",
                "Flexible repayment terms",
                "Quick approval"
            )
        ),
        LoanProduct(
            id = "2",
            name = "Auto Loan",
            type = LoanType.AUTO,
            minAmount = 5000.0,
            maxAmount = 100000.0,
            interestRate = 3.2,
            maxTerm = 84,
            description = "Competitive rates for new and used vehicles",
            features = listOf(
                "Low interest rates",
                "Up to 7 years to repay",
                "New and used cars",
                "Pre-approval available"
            )
        ),
        LoanProduct(
            id = "3",
            name = "Business Loan",
            type = LoanType.BUSINESS,
            minAmount = 10000.0,
            maxAmount = 500000.0,
            interestRate = 4.5,
            maxTerm = 120,
            description = "Grow your business with flexible financing",
            features = listOf(
                "Competitive rates",
                "Flexible terms",
                "Business credit building",
                "Dedicated support"
            )
        )
    )
}
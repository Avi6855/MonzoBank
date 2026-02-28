package com.avinashpatil.app.monzobank.presentation.ui.loans

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class LoanApplication(
    val loanType: LoanType = LoanType.PERSONAL,
    val amount: String = "",
    val purpose: String = "",
    val term: String = "",
    val employmentStatus: String = "",
    val annualIncome: String = "",
    val monthlyExpenses: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanApplicationScreen(
    onBackClick: () -> Unit,
    onSubmitApplication: (LoanApplication) -> Unit
) {
    var application by remember { mutableStateOf(LoanApplication()) }
    var currentStep by remember { mutableStateOf(0) }
    val totalSteps = 4
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Loan Application") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Progress indicator
            LinearProgressIndicator(
                progress = (currentStep + 1).toFloat() / totalSteps,
                modifier = Modifier.fillMaxWidth()
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Step ${currentStep + 1} of $totalSteps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                when (currentStep) {
                    0 -> item { LoanDetailsStep(application) { application = it } }
                    1 -> item { FinancialInfoStep(application) { application = it } }
                    2 -> item { PersonalInfoStep(application) { application = it } }
                    3 -> item { ReviewStep(application) }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentStep > 0) {
                            OutlinedButton(
                                onClick = { currentStep-- }
                            ) {
                                Text("Previous")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }
                        
                        Button(
                            onClick = {
                                if (currentStep < totalSteps - 1) {
                                    currentStep++
                                } else {
                                    onSubmitApplication(application)
                                }
                            },
                            enabled = isStepValid(currentStep, application)
                        ) {
                            Text(if (currentStep < totalSteps - 1) "Next" else "Submit Application")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoanDetailsStep(
    application: LoanApplication,
    onUpdate: (LoanApplication) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Loan Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Loan Type Selection
            Text(
                text = "Loan Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            LoanType.values().forEach { type ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = application.loanType == type,
                            onClick = { onUpdate(application.copy(loanType = type)) }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = application.loanType == type,
                        onClick = { onUpdate(application.copy(loanType = type)) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = getLoanTypeName(type))
                }
            }
            
            OutlinedTextField(
                value = application.amount,
                onValueChange = { onUpdate(application.copy(amount = it)) },
                label = { Text("Loan Amount (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = application.purpose,
                onValueChange = { onUpdate(application.copy(purpose = it)) },
                label = { Text("Purpose of Loan") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = application.term,
                onValueChange = { onUpdate(application.copy(term = it)) },
                label = { Text("Loan Term (months)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun FinancialInfoStep(
    application: LoanApplication,
    onUpdate: (LoanApplication) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Financial Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            var expandedEmployment by remember { mutableStateOf(false) }
            val employmentOptions = listOf(
                "Full-time Employee",
                "Part-time Employee",
                "Self-employed",
                "Unemployed",
                "Retired",
                "Student"
            )
            
            ExposedDropdownMenuBox(
                expanded = expandedEmployment,
                onExpandedChange = { expandedEmployment = !expandedEmployment }
            ) {
                OutlinedTextField(
                    value = application.employmentStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Employment Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEmployment)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedEmployment,
                    onDismissRequest = { expandedEmployment = false }
                ) {
                    employmentOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onUpdate(application.copy(employmentStatus = option))
                                expandedEmployment = false
                            }
                        )
                    }
                }
            }
            
            OutlinedTextField(
                value = application.annualIncome,
                onValueChange = { onUpdate(application.copy(annualIncome = it)) },
                label = { Text("Annual Income (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = application.monthlyExpenses,
                onValueChange = { onUpdate(application.copy(monthlyExpenses = it)) },
                label = { Text("Monthly Expenses (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Financial tip
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tip: Include all sources of income for better loan terms",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PersonalInfoStep(
    application: LoanApplication,
    onUpdate: (LoanApplication) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = application.firstName,
                    onValueChange = { onUpdate(application.copy(firstName = it)) },
                    label = { Text("First Name") },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = application.lastName,
                    onValueChange = { onUpdate(application.copy(lastName = it)) },
                    label = { Text("Last Name") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            OutlinedTextField(
                value = application.email,
                onValueChange = { onUpdate(application.copy(email = it)) },
                label = { Text("Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = application.phone,
                onValueChange = { onUpdate(application.copy(phone = it)) },
                label = { Text("Phone Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = application.address,
                onValueChange = { onUpdate(application.copy(address = it)) },
                label = { Text("Address") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = application.city,
                    onValueChange = { onUpdate(application.copy(city = it)) },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = application.postalCode,
                    onValueChange = { onUpdate(application.copy(postalCode = it)) },
                    label = { Text("Postal Code") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReviewStep(application: LoanApplication) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Review Your Application",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Loan Details Summary
            ReviewSection(
                title = "Loan Details",
                items = listOf(
                    "Type" to getLoanTypeName(application.loanType),
                    "Amount" to "£${application.amount}",
                    "Purpose" to application.purpose,
                    "Term" to "${application.term} months"
                )
            )
            
            // Financial Information Summary
            ReviewSection(
                title = "Financial Information",
                items = listOf(
                    "Employment" to application.employmentStatus,
                    "Annual Income" to "£${application.annualIncome}",
                    "Monthly Expenses" to "£${application.monthlyExpenses}"
                )
            )
            
            // Personal Information Summary
            ReviewSection(
                title = "Personal Information",
                items = listOf(
                    "Name" to "${application.firstName} ${application.lastName}",
                    "Email" to application.email,
                    "Phone" to application.phone,
                    "Address" to "${application.address}, ${application.city} ${application.postalCode}"
                )
            )
            
            // Terms and Conditions
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Terms and Conditions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By submitting this application, you agree to our terms and conditions and authorize us to perform a credit check.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewSection(
    title: String,
    items: List<Pair<String, String>>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        items.forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider()
    }
}

fun isStepValid(step: Int, application: LoanApplication): Boolean {
    return when (step) {
        0 -> application.amount.isNotBlank() && 
             application.purpose.isNotBlank() && 
             application.term.isNotBlank()
        1 -> application.employmentStatus.isNotBlank() && 
             application.annualIncome.isNotBlank() && 
             application.monthlyExpenses.isNotBlank()
        2 -> application.firstName.isNotBlank() && 
             application.lastName.isNotBlank() && 
             application.email.isNotBlank() && 
             application.phone.isNotBlank() && 
             application.address.isNotBlank() && 
             application.city.isNotBlank() && 
             application.postalCode.isNotBlank()
        3 -> true
        else -> false
    }
}
package com.avinashpatil.app.monzobank.presentation.ui.accounts

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

data class AccountType(
    val id: String,
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val features: List<String>,
    val minimumDeposit: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onBackClick: () -> Unit,
    onAccountCreated: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(0) }
    var selectedAccountType by remember { mutableStateOf<AccountType?>(null) }
    var accountName by remember { mutableStateOf("") }
    var initialDeposit by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val accountTypes = remember {
        listOf(
            AccountType(
                id = "current",
                name = "Current Account",
                description = "For everyday spending and transactions",
                icon = Icons.Default.AccountBalance,
                features = listOf(
                    "Instant notifications",
                    "Contactless payments",
                    "Direct debits & standing orders",
                    "Overdraft available"
                )
            ),
            AccountType(
                id = "savings",
                name = "Savings Account",
                description = "Earn interest on your savings",
                icon = Icons.Default.Savings,
                features = listOf(
                    "Competitive interest rates",
                    "No monthly fees",
                    "Easy access to funds",
                    "Goal tracking"
                ),
                minimumDeposit = 1.0
            ),
            AccountType(
                id = "joint",
                name = "Joint Account",
                description = "Share an account with someone",
                icon = Icons.Default.Group,
                features = listOf(
                    "Shared spending insights",
                    "Individual cards",
                    "Split bills easily",
                    "Joint savings goals"
                )
            ),
            AccountType(
                id = "business",
                name = "Business Account",
                description = "For your business needs",
                icon = Icons.Default.Business,
                features = listOf(
                    "Business insights",
                    "Expense categorization",
                    "Invoice management",
                    "Tax reporting"
                ),
                minimumDeposit = 100.0
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { 
                Text(
                    when (currentStep) {
                        0 -> "Choose Account Type"
                        1 -> "Account Details"
                        2 -> "Review & Create"
                        else -> "Create Account"
                    }
                ) 
            },
            navigationIcon = {
                IconButton(onClick = {
                    if (currentStep > 0) {
                        currentStep--
                    } else {
                        onBackClick()
                    }
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Progress Indicator
        LinearProgressIndicator(
            progress = (currentStep + 1) / 3f,
            modifier = Modifier.fillMaxWidth()
        )

        when (currentStep) {
            0 -> {
                // Step 1: Choose Account Type
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "What type of account would you like to open?",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    items(accountTypes) { accountType ->
                        AccountTypeCard(
                            accountType = accountType,
                            isSelected = selectedAccountType?.id == accountType.id,
                            onClick = { selectedAccountType = accountType }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { currentStep = 1 },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedAccountType != null
                        ) {
                            Text("Continue")
                        }
                    }
                }
            }

            1 -> {
                // Step 2: Account Details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Account Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { 
                            accountName = it
                            errorMessage = null
                        },
                        label = { Text("Account Name") },
                        placeholder = { Text(selectedAccountType?.name ?: "") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = errorMessage != null
                    )

                    if (selectedAccountType?.minimumDeposit ?: 0.0 > 0) {
                        OutlinedTextField(
                            value = initialDeposit,
                            onValueChange = { 
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    initialDeposit = it
                                    errorMessage = null
                                }
                            },
                            label = { Text("Initial Deposit (£)") },
                            placeholder = { Text("Minimum £${selectedAccountType?.minimumDeposit}") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = errorMessage != null
                        )
                    }

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            // Validate inputs
                            when {
                                accountName.isBlank() -> {
                                    errorMessage = "Please enter an account name"
                                }
                                selectedAccountType?.minimumDeposit ?: 0.0 > 0 && 
                                (initialDeposit.toDoubleOrNull() ?: 0.0) < (selectedAccountType?.minimumDeposit ?: 0.0) -> {
                                    errorMessage = "Minimum deposit is £${selectedAccountType?.minimumDeposit}"
                                }
                                else -> {
                                    currentStep = 2
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                }
            }

            2 -> {
                // Step 3: Review & Create
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Review Your Account",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ReviewItem("Account Type", selectedAccountType?.name ?: "")
                            ReviewItem("Account Name", accountName)
                            if (selectedAccountType?.minimumDeposit ?: 0.0 > 0) {
                                ReviewItem("Initial Deposit", "£$initialDeposit")
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Account Features",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            selectedAccountType?.features?.forEach { feature ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = feature,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            isLoading = true
                            coroutineScope.launch {
                                delay(3000) // Simulate account creation
                                isLoading = false
                                // Generate a new account ID
                                val newAccountId = "acc_${System.currentTimeMillis()}"
                                onAccountCreated(newAccountId)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Creating Account...")
                            }
                        } else {
                            Text("Create Account")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountTypeCard(
    accountType: AccountType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = accountType.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accountType.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = accountType.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (accountType.minimumDeposit > 0) {
                    Text(
                        text = "Min. deposit: £${accountType.minimumDeposit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ReviewItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
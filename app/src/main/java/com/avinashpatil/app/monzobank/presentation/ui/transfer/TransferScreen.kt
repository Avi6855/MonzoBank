package com.avinashpatil.app.monzobank.presentation.ui.transfer

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.utils.*
import com.avinashpatil.app.monzobank.data.local.entity.AccountType
import com.avinashpatil.app.monzobank.data.local.entity.AccountStatus
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import java.math.BigDecimal
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    onNavigateBack: () -> Unit,
    fromAccount: Account? = null
) {
    var amount by remember { mutableStateOf("") }
    var recipient by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var selectedFromAccount by remember { mutableStateOf(fromAccount) }
    var selectedToAccount by remember { mutableStateOf<Account?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Validation functions
    val validateTransferForm = {
        val amountError = selectedFromAccount?.let { account ->
            validateTransferAmount(
                amount = amount,
                accountBalance = account.balance,
                overdraftLimit = account.overdraftLimit,
                dailyLimit = BigDecimal("5000.00")
            )
        } ?: validateAmount(amount)
        
        val nameError = validateName(recipient)
        val referenceError = validateReference(reference)
        
        listOfNotNull(amountError, nameError, referenceError).firstOrNull()
    }
    
    // Mock accounts for demonstration
    val mockAccounts = remember {
        listOf(
            Account(
                id = "acc1",
                userId = "user123",
                name = "Current Account",
                type = AccountType.CURRENT,
                balance = BigDecimal("1250.50"),
                availableBalance = BigDecimal("1250.50"),
                currency = "GBP",
                accountNumber = "12345678",
                sortCode = "04-00-04",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal.ZERO,
                interestRate = BigDecimal.ZERO,
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            ),
            Account(
                id = "acc2",
                userId = "user123",
                name = "Savings Account",
                type = AccountType.SAVINGS,
                balance = BigDecimal("5000.00"),
                availableBalance = BigDecimal("5000.00"),
                currency = "GBP",
                accountNumber = "87654321",
                sortCode = "04-00-04",
                iban = "GB29 NWBK 6016 1331 9268 19",
                status = AccountStatus.ACTIVE,
                overdraftLimit = BigDecimal.ZERO,
                interestRate = BigDecimal.ZERO,
                createdAt = Date(),
                updatedAt = Date(),
                isDefault = false,
                metadata = emptyMap()
            )
        )
    }
    
    if (selectedFromAccount == null && mockAccounts.isNotEmpty()) {
        selectedFromAccount = mockAccounts.first()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Transfer Money",
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
        
        if (showSuccess) {
            TransferSuccessContent(
                amount = amount,
                recipient = recipient,
                onDone = onNavigateBack
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error Message
                if (errorMessage != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // From Account Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        selectedFromAccount?.let { account ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = account.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${account.sortCode} • ${account.accountNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                Text(
                                    text = account.formattedBalance,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MonzoCoralPrimary
                                )
                            }
                        }
                    }
                }
                
                // Amount Input with Validation
                ValidatedTextField(
                    value = amount,
                    onValueChange = { 
                        amount = SecurityUtils.sanitizeInput(it)
                        errorMessage = null
                    },
                    label = "Amount (£)",
                    modifier = Modifier.fillMaxWidth(),
                    validator = { value ->
                        selectedFromAccount?.let { account ->
                            validateTransferAmount(
                                amount = value,
                                accountBalance = account.balance,
                                overdraftLimit = account.overdraftLimit,
                                dailyLimit = BigDecimal("5000.00")
                            )
                        } ?: validateAmount(value)
                    },
                    keyboardType = KeyboardType.Decimal,
                    leadingIcon = {
                        Text(
                            text = "£",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
                
                // Recipient Input with Validation
                ValidatedTextField(
                    value = recipient,
                    onValueChange = { 
                        recipient = SecurityUtils.sanitizeInput(it)
                        errorMessage = null
                    },
                    label = "Recipient Name or Account",
                    modifier = Modifier.fillMaxWidth(),
                    validator = ::validateName,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    }
                )
                
                // Reference Input with Validation
                ValidatedTextField(
                    value = reference,
                    onValueChange = { 
                        reference = SecurityUtils.sanitizeInput(it)
                        errorMessage = null
                    },
                    label = "Reference (Optional)",
                    modifier = Modifier.fillMaxWidth(),
                    validator = ::validateReference,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Transfer Button
                Button(
                    onClick = {
                        val validationError = validateTransferForm()
                        if (validationError != null) {
                            errorMessage = validationError
                            return@Button
                        }
                        
                        // Security checks
                         try {
                             val amountDecimal = BigDecimal(amount)
                             val isSuspicious = SecurityUtils.isSuspiciousTransaction(
                                 amount = amountDecimal,
                                 averageTransactionAmount = BigDecimal("100.00"),
                                 isInternational = false,
                                 isUnusualTime = java.time.LocalTime.now().hour < 6 || java.time.LocalTime.now().hour > 22
                             )
                             
                             if (isSuspicious) {
                                 errorMessage = "Transaction flagged for security review. Please contact support."
                                 return@Button
                             }
                             
                             isLoading = true
                             // Simulate transfer processing
                             // In real app, this would call the transfer use case
                             showSuccess = true
                             isLoading = false
                         } catch (e: Exception) {
                             errorMessage = "Invalid amount format"
                         }
                    },
                    enabled = !isLoading && amount.isNotBlank() && recipient.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Transfer £$amount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransferSuccessContent(
    amount: String,
    recipient: String,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Icon
        Card(
            modifier = Modifier.size(80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(40.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Transfer Successful!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "£$amount has been sent to $recipient",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MonzoCoralPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
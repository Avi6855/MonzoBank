package com.avinashpatil.app.monzobank.presentation.ui.international

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.avinashpatil.app.monzobank.domain.model.*
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InternationalTransferScreen(
    accounts: List<Account>,
    currencies: List<Currency>,
    exchangeRates: List<ExchangeRate>,
    onNavigateBack: () -> Unit,
    onTransferComplete: (InternationalTransferRequest) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Send Money", "Exchange Rates", "My Transfers")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "International",
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
                        imageVector = Icons.Default.History,
                        contentDescription = "Transfer History",
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
            0 -> SendMoneyTab(
                accounts = accounts,
                currencies = currencies,
                exchangeRates = exchangeRates,
                onTransferComplete = onTransferComplete
            )
            1 -> ExchangeRatesTab(
                currencies = currencies,
                exchangeRates = exchangeRates
            )
            2 -> MyTransfersTab()
        }
    }
}

@Composable
fun SendMoneyTab(
    accounts: List<Account>,
    currencies: List<Currency>,
    exchangeRates: List<ExchangeRate>,
    onTransferComplete: (InternationalTransferRequest) -> Unit
) {
    var currentStep by remember { mutableStateOf(TransferStep.RECIPIENT) }
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var recipientDetails by remember { mutableStateOf(RecipientDetails("", "", "", "", "")) }
    var transferDetails by remember { mutableStateOf(TransferDetails(BigDecimal.ZERO, "USD", TransferPurpose.FAMILY_SUPPORT, "")) }
    var complianceInfo by remember { mutableStateOf(ComplianceInfo("", "", "")) }
    var selectedCurrency by remember { mutableStateOf("USD") }
    var amount by remember { mutableStateOf("") }
    var conversion by remember { mutableStateOf<CurrencyConversion?>(null) }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Indicator
        item {
            TransferProgressIndicator(
                currentStep = currentStep,
                completedSteps = getCompletedSteps(currentStep)
            )
        }
        
        when (currentStep) {
            TransferStep.RECIPIENT -> {
                item {
                    RecipientDetailsStep(
                        recipientDetails = recipientDetails,
                        onDetailsChange = { recipientDetails = it },
                        onNext = { currentStep = TransferStep.AMOUNT }
                    )
                }
            }
            
            TransferStep.AMOUNT -> {
                item {
                    AmountAndCurrencyStep(
                        accounts = accounts,
                        currencies = currencies,
                        exchangeRates = exchangeRates,
                        selectedAccount = selectedAccount,
                        onAccountChange = { selectedAccount = it },
                        selectedCurrency = selectedCurrency,
                        onCurrencyChange = { selectedCurrency = it },
                        amount = amount,
                        onAmountChange = { amount = it },
                        conversion = conversion,
                        onConversionChange = { conversion = it },
                        onBack = { currentStep = TransferStep.RECIPIENT },
                        onNext = { currentStep = TransferStep.PURPOSE }
                    )
                }
            }
            
            TransferStep.PURPOSE -> {
                item {
                    PurposeAndComplianceStep(
                        transferDetails = transferDetails,
                        onTransferDetailsChange = { transferDetails = it },
                        complianceInfo = complianceInfo,
                        onComplianceInfoChange = { complianceInfo = it },
                        onBack = { currentStep = TransferStep.AMOUNT },
                        onNext = { currentStep = TransferStep.REVIEW }
                    )
                }
            }
            
            TransferStep.REVIEW -> {
                item {
                    ReviewTransferStep(
                        selectedAccount = selectedAccount!!,
                        recipientDetails = recipientDetails,
                        transferDetails = transferDetails,
                        complianceInfo = complianceInfo,
                        conversion = conversion,
                        onBack = { currentStep = TransferStep.PURPOSE },
                        onConfirm = {
                            val request = InternationalTransferRequest(
                                fromAccountId = selectedAccount!!.id,
                                recipientDetails = recipientDetails,
                                transferDetails = transferDetails,
                                complianceInfo = complianceInfo
                            )
                            onTransferComplete(request)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeRatesTab(
    currencies: List<Currency>,
    exchangeRates: List<ExchangeRate>
) {
    var baseCurrency by remember { mutableStateOf("GBP") }
    var searchQuery by remember { mutableStateOf("") }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Base Currency Selector
        item {
            BaseCurrencySelector(
                currencies = currencies,
                selectedCurrency = baseCurrency,
                onCurrencySelected = { baseCurrency = it }
            )
        }
        
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search currencies") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        
        // Popular Pairs
        item {
            PopularCurrencyPairs(
                baseCurrency = baseCurrency,
                exchangeRates = exchangeRates
            )
        }
        
        // All Exchange Rates
        item {
            Text(
                text = "All Rates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        val filteredRates = exchangeRates.filter {
            it.fromCurrency == baseCurrency &&
                    (searchQuery.isEmpty() || it.toCurrency.contains(searchQuery, ignoreCase = true))
        }
        
        items(filteredRates) { rate ->
            ExchangeRateCard(rate = rate)
        }
    }
}

@Composable
fun MyTransfersTab() {
    // Mock transfer data
    val transfers = remember {
        listOf(
            InternationalTransfer(
                id = "transfer_1",
                fromAccountId = "acc_1",
                recipientName = "John Smith",
                recipientAddress = "123 Main St, New York, NY",
                recipientBankName = "Chase Bank",
                recipientBankAddress = "456 Bank St, New York, NY",
                recipientAccountNumber = "1234567890",
                recipientRoutingNumber = "021000021",
                fromAmount = BigDecimal("1000.00"),
                fromCurrency = "GBP",
                toAmount = BigDecimal("1270.00"),
                toCurrency = "USD",
                exchangeRate = BigDecimal("1.27"),
                transferFee = BigDecimal("5.00"),
                purpose = TransferPurpose.FAMILY_SUPPORT,
                reference = "Monthly support",
                status = TransferStatus.COMPLETED,
                createdAt = java.time.LocalDateTime.now().minusDays(2),
                estimatedArrival = java.time.LocalDateTime.now().minusDays(1),
                actualArrival = java.time.LocalDateTime.now().minusDays(1)
            ),
            InternationalTransfer(
                id = "transfer_2",
                fromAccountId = "acc_1",
                recipientName = "Marie Dubois",
                recipientAddress = "45 Rue de la Paix, Paris",
                recipientBankName = "BNP Paribas",
                recipientBankAddress = "12 Avenue des Champs, Paris",
                recipientAccountNumber = "FR1420041010050500013M02606",
                recipientIBAN = "FR1420041010050500013M02606",
                fromAmount = BigDecimal("500.00"),
                fromCurrency = "GBP",
                toAmount = BigDecimal("585.00"),
                toCurrency = "EUR",
                exchangeRate = BigDecimal("1.17"),
                transferFee = BigDecimal("3.50"),
                purpose = TransferPurpose.GIFT,
                reference = "Birthday gift",
                status = TransferStatus.PROCESSING,
                createdAt = java.time.LocalDateTime.now().minusHours(6),
                estimatedArrival = java.time.LocalDateTime.now().plusDays(1)
            )
        )
    }
    
    if (transfers.isEmpty()) {
        EmptyTransfersState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(transfers) { transfer ->
                TransferCard(transfer = transfer)
            }
        }
    }
}

@Composable
fun TransferProgressIndicator(
    currentStep: TransferStep,
    completedSteps: List<TransferStep>
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransferStep.values().forEachIndexed { index, step ->
                StepIndicator(
                    step = step,
                    isActive = step == currentStep,
                    isCompleted = step in completedSteps,
                    isLast = index == TransferStep.values().size - 1
                )
            }
        }
    }
}

@Composable
fun StepIndicator(
    step: TransferStep,
    isActive: Boolean,
    isCompleted: Boolean,
    isLast: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = when {
                isCompleted -> Color(0xFF4CAF50)
                isActive -> MonzoCoralPrimary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = (step.ordinal + 1).toString(),
                        color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(
                        if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
fun RecipientDetailsStep(
    recipientDetails: RecipientDetails,
    onDetailsChange: (RecipientDetails) -> Unit,
    onNext: () -> Unit
) {
    var name by remember { mutableStateOf(recipientDetails.name) }
    var address by remember { mutableStateOf(recipientDetails.address) }
    var bankName by remember { mutableStateOf(recipientDetails.bankName) }
    var accountNumber by remember { mutableStateOf(recipientDetails.accountNumber) }
    var swiftCode by remember { mutableStateOf(recipientDetails.swiftCode ?: "") }
    
    val isValid = name.isNotBlank() && address.isNotBlank() && 
            bankName.isNotBlank() && accountNumber.isNotBlank()
    
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
                text = "Recipient Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    onDetailsChange(recipientDetails.copy(name = it))
                },
                label = { Text("Recipient Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = address,
                onValueChange = { 
                    address = it
                    onDetailsChange(recipientDetails.copy(address = it))
                },
                label = { Text("Recipient Address") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = bankName,
                onValueChange = { 
                    bankName = it
                    onDetailsChange(recipientDetails.copy(bankName = it))
                },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { 
                    accountNumber = it
                    onDetailsChange(recipientDetails.copy(accountNumber = it))
                },
                label = { Text("Account Number / IBAN") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = swiftCode,
                onValueChange = { 
                    swiftCode = it
                    onDetailsChange(recipientDetails.copy(swiftCode = it.ifBlank { null }))
                },
                label = { Text("SWIFT/BIC Code (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onNext,
                enabled = isValid,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Text(
                    text = "Continue",
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AmountAndCurrencyStep(
    accounts: List<Account>,
    currencies: List<Currency>,
    exchangeRates: List<ExchangeRate>,
    selectedAccount: Account?,
    onAccountChange: (Account) -> Unit,
    selectedCurrency: String,
    onCurrencyChange: (String) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit,
    conversion: CurrencyConversion?,
    onConversionChange: (CurrencyConversion?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val amountValue = amount.toBigDecimalOrNull()
    val isValidAmount = amountValue != null && amountValue > BigDecimal.ZERO
    val hasEnoughBalance = selectedAccount?.let { 
        amountValue?.let { amt -> it.balance >= amt } 
    } ?: false
    
    // Mock conversion calculation
    LaunchedEffect(amountValue, selectedCurrency) {
        if (amountValue != null && amountValue > BigDecimal.ZERO) {
            val mockConversion = CurrencyConversion(
                id = "conv_${System.currentTimeMillis()}",
                fromAmount = amountValue,
                fromCurrency = "GBP",
                toAmount = amountValue.multiply(BigDecimal("1.27")),
                toCurrency = selectedCurrency,
                exchangeRate = BigDecimal("1.27"),
                fee = amountValue.multiply(BigDecimal("0.005")),
                feePercentage = BigDecimal("0.5"),
                timestamp = java.time.LocalDateTime.now(),
                expiresAt = java.time.LocalDateTime.now().plusMinutes(15)
            )
            onConversionChange(mockConversion)
        }
    }
    
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
                text = "Amount & Currency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Selection
            Text(
                text = "From Account",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accounts) { account ->
                    AccountChip(
                        account = account,
                        isSelected = account.id == selectedAccount?.id,
                        onClick = { onAccountChange(account) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Amount Input
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("Amount (£)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = amount.isNotEmpty() && (!isValidAmount || !hasEnoughBalance),
                supportingText = {
                    when {
                        amount.isNotEmpty() && !isValidAmount -> {
                            Text(
                                text = "Please enter a valid amount",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        amount.isNotEmpty() && isValidAmount && !hasEnoughBalance -> {
                            Text(
                                text = "Insufficient funds",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        selectedAccount != null -> {
                            Text(
                                text = "Available: ${selectedAccount.formattedBalance}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Currency Selection
            CurrencySelector(
                currencies = currencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = onCurrencyChange
            )
            
            // Conversion Preview
            conversion?.let {
                Spacer(modifier = Modifier.height(16.dp))
                ConversionPreviewCard(conversion = it)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                
                Button(
                    onClick = onNext,
                    enabled = isValidAmount && hasEnoughBalance,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun ConversionPreviewCard(
    conversion: CurrencyConversion
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MonzoCoralPrimary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Conversion Preview",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MonzoCoralPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "You send",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "£${conversion.fromAmount.setScale(2)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transfer fee",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "£${conversion.fee.setScale(2)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recipient gets",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${conversion.toCurrency} ${conversion.toAmount.setScale(2)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MonzoCoralPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Rate expires in ${conversion.minutesUntilExpiry} minutes",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class TransferStep {
    RECIPIENT, AMOUNT, PURPOSE, REVIEW
}

fun getCompletedSteps(currentStep: TransferStep): List<TransferStep> {
    return TransferStep.values().take(currentStep.ordinal)
}

@Composable
fun PurposeAndComplianceStep(
    transferDetails: TransferDetails,
    onTransferDetailsChange: (TransferDetails) -> Unit,
    complianceInfo: ComplianceInfo,
    onComplianceInfoChange: (ComplianceInfo) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
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
                text = "Transfer Purpose & Compliance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = transferDetails.reference,
                onValueChange = { onTransferDetailsChange(transferDetails.copy(reference = it)) },
                label = { Text("Reference") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                
                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
fun ReviewTransferStep(
    selectedAccount: Account,
    recipientDetails: RecipientDetails,
    transferDetails: TransferDetails,
    complianceInfo: ComplianceInfo,
    conversion: CurrencyConversion?,
    onBack: () -> Unit,
    onConfirm: () -> Unit
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
                text = "Review Transfer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "From: ${selectedAccount.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "To: ${recipientDetails.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            conversion?.let {
                Text(
                    text = "Amount: ${it.toCurrency} ${it.toAmount.setScale(2)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
                
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Confirm Transfer")
                }
            }
        }
    }
}

@Composable
fun BaseCurrencySelector(
    currencies: List<Currency>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
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
                text = "Base Currency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currencies.take(5)) { currency ->
                    FilterChip(
                        onClick = { onCurrencySelected(currency.code) },
                        label = { Text(currency.code) },
                        selected = currency.code == selectedCurrency
                    )
                }
            }
        }
    }
}

@Composable
fun PopularCurrencyPairs(
    baseCurrency: String,
    exchangeRates: List<ExchangeRate>
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
                text = "Popular Pairs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val popularPairs = exchangeRates.filter { it.fromCurrency == baseCurrency }.take(4)
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(popularPairs) { rate ->
                    ExchangeRateCard(rate = rate)
                }
            }
        }
    }
}

@Composable
fun ExchangeRateCard(
    rate: ExchangeRate
) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${rate.fromCurrency}/${rate.toCurrency}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = rate.rate.setScale(4).toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyTransfersState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No transfers yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Your international transfers will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransferCard(
    transfer: InternationalTransfer
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = transfer.recipientName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${transfer.fromCurrency} ${transfer.fromAmount.setScale(2)} → ${transfer.toCurrency} ${transfer.toAmount.setScale(2)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = transfer.status.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = when (transfer.status) {
                        TransferStatus.COMPLETED -> Color(0xFF4CAF50)
                        TransferStatus.PROCESSING -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
fun AccountChip(
    account: Account,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        onClick = onClick,
        label = {
            Column {
                Text(
                    text = account.name,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = account.formattedBalance,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        selected = isSelected
    )
}

@Composable
fun CurrencySelector(
    currencies: List<Currency>,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = { },
            readOnly = true,
            label = { Text("Currency") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text("${currency.code} - ${currency.name}")
                    },
                    onClick = {
                        onCurrencySelected(currency.code)
                        expanded = false
                    }
                )
            }
        }
    }
}
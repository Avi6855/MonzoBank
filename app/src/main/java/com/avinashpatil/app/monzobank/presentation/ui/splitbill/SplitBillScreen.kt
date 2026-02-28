package com.avinashpatil.app.monzobank.presentation.ui.splitbill

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
import com.avinashpatil.app.monzobank.domain.model.Account
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    accounts: List<Account>,
    onNavigateBack: () -> Unit,
    onBillCreated: (SplitBillData) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Create Bill", "My Bills", "Requests")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Split Bills",
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
                        contentDescription = "Bill History",
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
            0 -> CreateBillTab(
                accounts = accounts,
                onBillCreated = onBillCreated
            )
            1 -> MyBillsTab()
            2 -> RequestsTab()
        }
    }
}

@Composable
fun CreateBillTab(
    accounts: List<Account>,
    onBillCreated: (SplitBillData) -> Unit
) {
    var currentStep by remember { mutableStateOf(BillStep.DETAILS) }
    var billTitle by remember { mutableStateOf("") }
    var billDescription by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var selectedAccount by remember { mutableStateOf(accounts.firstOrNull()) }
    var participants by remember { mutableStateOf(listOf<BillParticipant>()) }
    var splitMethod by remember { mutableStateOf(SplitMethod.EQUAL) }
    var billCategory by remember { mutableStateOf(BillCategory.DINING) }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Indicator
        item {
            BillProgressIndicator(
                currentStep = currentStep,
                completedSteps = getBillCompletedSteps(currentStep)
            )
        }
        
        when (currentStep) {
            BillStep.DETAILS -> {
                item {
                    BillDetailsStep(
                        accounts = accounts,
                        billTitle = billTitle,
                        onTitleChange = { billTitle = it },
                        billDescription = billDescription,
                        onDescriptionChange = { billDescription = it },
                        totalAmount = totalAmount,
                        onAmountChange = { totalAmount = it },
                        selectedAccount = selectedAccount,
                        onAccountChange = { selectedAccount = it },
                        billCategory = billCategory,
                        onCategoryChange = { billCategory = it },
                        onNext = { currentStep = BillStep.PARTICIPANTS }
                    )
                }
            }
            
            BillStep.PARTICIPANTS -> {
                item {
                    ParticipantsStep(
                        participants = participants,
                        onParticipantsChange = { participants = it },
                        onBack = { currentStep = BillStep.DETAILS },
                        onNext = { currentStep = BillStep.SPLIT }
                    )
                }
            }
            
            BillStep.SPLIT -> {
                item {
                    SplitMethodStep(
                        totalAmount = totalAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                        participants = participants,
                        onParticipantsChange = { participants = it },
                        splitMethod = splitMethod,
                        onSplitMethodChange = { splitMethod = it },
                        onBack = { currentStep = BillStep.PARTICIPANTS },
                        onNext = { currentStep = BillStep.REVIEW }
                    )
                }
            }
            
            BillStep.REVIEW -> {
                item {
                    ReviewBillStep(
                        billTitle = billTitle,
                        billDescription = billDescription,
                        totalAmount = totalAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                        selectedAccount = selectedAccount!!,
                        participants = participants,
                        splitMethod = splitMethod,
                        billCategory = billCategory,
                        onBack = { currentStep = BillStep.SPLIT },
                        onConfirm = {
                            val billData = SplitBillData(
                                title = billTitle,
                                description = billDescription,
                                totalAmount = totalAmount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                                payerAccountId = selectedAccount!!.id,
                                participants = participants,
                                splitMethod = splitMethod,
                                category = billCategory
                            )
                            onBillCreated(billData)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyBillsTab() {
    // Mock bill data
    val bills = remember {
        listOf(
            SplitBill(
                id = "bill_1",
                title = "Dinner at Italian Restaurant",
                description = "Team dinner celebration",
                totalAmount = BigDecimal("120.00"),
                payerId = "user_1",
                payerName = "You",
                participants = listOf(
                    BillParticipant(
                        id = "user_1",
                        name = "You",
                        email = "you@example.com",
                        amount = BigDecimal("30.00"),
                        status = ParticipantStatus.PAID
                    ),
                    BillParticipant(
                        id = "user_2",
                        name = "Alice Johnson",
                        email = "alice@example.com",
                        amount = BigDecimal("30.00"),
                        status = ParticipantStatus.PAID
                    ),
                    BillParticipant(
                        id = "user_3",
                        name = "Bob Smith",
                        email = "bob@example.com",
                        amount = BigDecimal("30.00"),
                        status = ParticipantStatus.PENDING
                    ),
                    BillParticipant(
                        id = "user_4",
                        name = "Carol Davis",
                        email = "carol@example.com",
                        amount = BigDecimal("30.00"),
                        status = ParticipantStatus.PENDING
                    )
                ),
                status = BillStatus.PARTIALLY_PAID,
                category = BillCategory.DINING,
                createdAt = LocalDateTime.now().minusDays(2),
                dueDate = LocalDateTime.now().plusDays(5)
            ),
            SplitBill(
                id = "bill_2",
                title = "Vacation House Rental",
                description = "Weekend getaway accommodation",
                totalAmount = BigDecimal("800.00"),
                payerId = "user_1",
                payerName = "You",
                participants = listOf(
                    BillParticipant(
                        id = "user_1",
                        name = "You",
                        email = "you@example.com",
                        amount = BigDecimal("200.00"),
                        status = ParticipantStatus.PAID
                    ),
                    BillParticipant(
                        id = "user_2",
                        name = "Alice Johnson",
                        email = "alice@example.com",
                        amount = BigDecimal("200.00"),
                        status = ParticipantStatus.PAID
                    ),
                    BillParticipant(
                        id = "user_3",
                        name = "Bob Smith",
                        email = "bob@example.com",
                        amount = BigDecimal("200.00"),
                        status = ParticipantStatus.PAID
                    ),
                    BillParticipant(
                        id = "user_4",
                        name = "Carol Davis",
                        email = "carol@example.com",
                        amount = BigDecimal("200.00"),
                        status = ParticipantStatus.PAID
                    )
                ),
                status = BillStatus.FULLY_PAID,
                category = BillCategory.TRAVEL,
                createdAt = LocalDateTime.now().minusDays(7),
                dueDate = LocalDateTime.now().minusDays(2)
            )
        )
    }
    
    if (bills.isEmpty()) {
        EmptyBillsState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(bills) { bill ->
                BillCard(
                    bill = bill,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun RequestsTab() {
    // Mock request data
    val requests = remember {
        listOf(
            BillRequest(
                id = "req_1",
                billId = "bill_3",
                billTitle = "Coffee Shop Visit",
                requesterName = "David Wilson",
                amount = BigDecimal("12.50"),
                message = "Coffee and pastries for the team",
                status = RequestStatus.PENDING,
                createdAt = LocalDateTime.now().minusHours(3),
                dueDate = LocalDateTime.now().plusDays(3)
            ),
            BillRequest(
                id = "req_2",
                billId = "bill_4",
                billTitle = "Uber Ride Share",
                requesterName = "Emma Thompson",
                amount = BigDecimal("8.75"),
                message = "Shared ride to the airport",
                status = RequestStatus.PENDING,
                createdAt = LocalDateTime.now().minusDays(1),
                dueDate = LocalDateTime.now().plusDays(2)
            )
        )
    }
    
    if (requests.isEmpty()) {
        EmptyRequestsState()
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requests) { request ->
                BillRequestCard(
                    request = request,
                    onAccept = { },
                    onDecline = { }
                )
            }
        }
    }
}

@Composable
fun BillProgressIndicator(
    currentStep: BillStep,
    completedSteps: List<BillStep>
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
            BillStep.values().forEachIndexed { index, step ->
                BillStepIndicator(
                    step = step,
                    isActive = step == currentStep,
                    isCompleted = step in completedSteps,
                    isLast = index == BillStep.values().size - 1
                )
            }
        }
    }
}

@Composable
fun BillStepIndicator(
    step: BillStep,
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
fun BillDetailsStep(
    accounts: List<Account>,
    billTitle: String,
    onTitleChange: (String) -> Unit,
    billDescription: String,
    onDescriptionChange: (String) -> Unit,
    totalAmount: String,
    onAmountChange: (String) -> Unit,
    selectedAccount: Account?,
    onAccountChange: (Account) -> Unit,
    billCategory: BillCategory,
    onCategoryChange: (BillCategory) -> Unit,
    onNext: () -> Unit
) {
    val isValid = billTitle.isNotBlank() && totalAmount.toBigDecimalOrNull() != null &&
            totalAmount.toBigDecimalOrNull()!! > BigDecimal.ZERO
    
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
                text = "Bill Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = billTitle,
                onValueChange = onTitleChange,
                label = { Text("Bill Title") },
                placeholder = { Text("e.g., Dinner at Restaurant") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = billDescription,
                onValueChange = onDescriptionChange,
                label = { Text("Description (Optional)") },
                placeholder = { Text("Add details about the expense") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = totalAmount,
                onValueChange = onAmountChange,
                label = { Text("Total Amount (£)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                leadingIcon = {
                    Text(
                        text = "£",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Account Selection
            Text(
                text = "Pay from Account",
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
            
            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BillCategory.values()) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = category == billCategory,
                        onClick = { onCategoryChange(category) }
                    )
                }
            }
            
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
                    text = "Add Participants",
                    modifier = Modifier.padding(vertical = 4.dp)
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
    Card(
        modifier = Modifier
            .clickable { onClick() }
            .width(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MonzoCoralPrimary else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = account.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = account.formattedBalance,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CategoryChip(
    category: BillCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MonzoCoralPrimary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.icon,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
    }
}

// Data classes and enums
data class SplitBillData(
    val title: String,
    val description: String,
    val totalAmount: BigDecimal,
    val payerAccountId: String,
    val participants: List<BillParticipant>,
    val splitMethod: SplitMethod,
    val category: BillCategory
)

data class BillParticipant(
    val id: String,
    val name: String,
    val email: String,
    val amount: BigDecimal,
    val status: ParticipantStatus = ParticipantStatus.PENDING
) {
    val initials: String
        get() = name.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString("")
}

enum class ParticipantStatus {
    PENDING, PAID, DECLINED;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            PAID -> "Paid"
            DECLINED -> "Declined"
        }
}

enum class SplitMethod {
    EQUAL, CUSTOM, PERCENTAGE;
    
    val displayName: String
        get() = when (this) {
            EQUAL -> "Split Equally"
            CUSTOM -> "Custom Amounts"
            PERCENTAGE -> "By Percentage"
        }
}

enum class BillCategory(val displayName: String, val icon: String) {
    DINING("Dining", "🍽️"),
    TRAVEL("Travel", "✈️"),
    ENTERTAINMENT("Entertainment", "🎬"),
    SHOPPING("Shopping", "🛍️"),
    UTILITIES("Utilities", "⚡"),
    GROCERIES("Groceries", "🛒"),
    OTHER("Other", "📝")
}

@Composable
fun ParticipantsStep(
    participants: List<BillParticipant>,
    onParticipantsChange: (List<BillParticipant>) -> Unit,
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
                text = "Add Participants",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            participants.forEach { participant ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = participant.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Text(
                        text = "£${participant.amount.setScale(2)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
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
fun SplitMethodStep(
    totalAmount: BigDecimal,
    participants: List<BillParticipant>,
    onParticipantsChange: (List<BillParticipant>) -> Unit,
    splitMethod: SplitMethod,
    onSplitMethodChange: (SplitMethod) -> Unit,
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
                text = "Split Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Total: £${totalAmount.setScale(2)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SplitMethod.values().forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSplitMethodChange(method) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = splitMethod == method,
                        onClick = { onSplitMethodChange(method) }
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = method.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
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
fun ReviewBillStep(
    billTitle: String,
    billDescription: String,
    totalAmount: BigDecimal,
    selectedAccount: Account,
    participants: List<BillParticipant>,
    splitMethod: SplitMethod,
    billCategory: BillCategory,
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
                text = "Review Bill",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Title: $billTitle",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Total: £${totalAmount.setScale(2)}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Participants: ${participants.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "Split: ${splitMethod.displayName}",
                style = MaterialTheme.typography.bodyMedium
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
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Create Bill")
                }
            }
        }
    }
}

@Composable
fun EmptyBillsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Receipt,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No bills yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Your split bills will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BillCard(
    bill: SplitBill,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        text = bill.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "${bill.participants.size} participants",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "£${bill.totalAmount.setScale(2)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MonzoCoralPrimary
                    )
                    
                    Text(
                        text = bill.status.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (bill.status) {
                            BillStatus.FULLY_PAID -> Color(0xFF4CAF50)
                            BillStatus.PARTIALLY_PAID -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyRequestsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.RequestPage,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No requests yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Text(
            text = "Payment requests will appear here",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BillRequestCard(
    request: BillRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
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
                        text = request.billTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    Text(
                        text = "From: ${request.requesterName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (request.message.isNotBlank()) {
                        Text(
                            text = request.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "£${request.amount.setScale(2)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MonzoCoralPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Decline")
                }
                
                Button(
                    onClick = onAccept,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonzoCoralPrimary
                    )
                ) {
                    Text("Accept")
                }
            }
        }
    }
}



enum class BillStep {
    DETAILS, PARTICIPANTS, SPLIT, REVIEW
}

fun getBillCompletedSteps(currentStep: BillStep): List<BillStep> {
    return BillStep.values().take(currentStep.ordinal)
}

data class SplitBill(
    val id: String,
    val title: String,
    val description: String,
    val totalAmount: BigDecimal,
    val payerId: String,
    val payerName: String,
    val participants: List<BillParticipant>,
    val status: BillStatus,
    val category: BillCategory,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime
) {
    val paidAmount: BigDecimal
        get() = participants.filter { it.status == ParticipantStatus.PAID }.sumOf { it.amount }
    
    val remainingAmount: BigDecimal
        get() = totalAmount.subtract(paidAmount)
    
    val paidParticipants: Int
        get() = participants.count { it.status == ParticipantStatus.PAID }
    
    val totalParticipants: Int
        get() = participants.size
}

enum class BillStatus {
    PENDING, PARTIALLY_PAID, FULLY_PAID, CANCELLED;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            PARTIALLY_PAID -> "Partially Paid"
            FULLY_PAID -> "Fully Paid"
            CANCELLED -> "Cancelled"
        }
}

data class BillRequest(
    val id: String,
    val billId: String,
    val billTitle: String,
    val requesterName: String,
    val amount: BigDecimal,
    val message: String,
    val status: RequestStatus,
    val createdAt: LocalDateTime,
    val dueDate: LocalDateTime
)

enum class RequestStatus {
    PENDING, ACCEPTED, DECLINED, EXPIRED;
    
    val displayName: String
        get() = when (this) {
            PENDING -> "Pending"
            ACCEPTED -> "Accepted"
            DECLINED -> "Declined"
            EXPIRED -> "Expired"
        }
}

// Additional composables would continue here for the remaining steps and components
// Including ParticipantsStep, SplitMethodStep, ReviewBillStep, BillCard, etc.
// Due to length constraints, showing the core structure and key components
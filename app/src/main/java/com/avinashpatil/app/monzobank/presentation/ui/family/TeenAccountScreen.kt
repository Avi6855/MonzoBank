package com.avinashpatil.app.monzobank.presentation.ui.family

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

data class TeenAccount(
    val id: String,
    val teenName: String,
    val age: Int,
    val balance: Double,
    val monthlyAllowance: Double,
    val spendingLimit: Double,
    val parentalControls: ParentalControls,
    val savingsGoals: List<SavingsGoal>,
    val achievements: List<Achievement>,
    val isActive: Boolean
)

data class ParentalControls(
    val dailySpendingLimit: Double,
    val weeklySpendingLimit: Double,
    val blockedCategories: List<String>,
    val allowedMerchants: List<String>,
    val requireApprovalForPurchases: Boolean,
    val approvalThreshold: Double,
    val curfewHours: Pair<Int, Int>, // Start and end hour
    val locationRestrictions: List<String>
)

data class SavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String,
    val category: String,
    val priority: GoalPriority
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val earnedDate: String,
    val points: Int,
    val category: AchievementCategory
)

data class TeenTransaction(
    val id: String,
    val description: String,
    val amount: Double,
    val date: String,
    val category: String,
    val merchant: String,
    val status: TeenTransactionStatus,
    val requiresApproval: Boolean,
    val parentApproval: Boolean?
)

data class FinancialLesson(
    val id: String,
    val title: String,
    val description: String,
    val duration: String,
    val difficulty: LessonDifficulty,
    val points: Int,
    val isCompleted: Boolean,
    val category: String
)

enum class GoalPriority {
    LOW, MEDIUM, HIGH
}

enum class AchievementCategory {
    SAVING, SPENDING, LEARNING, GOALS
}

enum class TeenTransactionStatus {
    PENDING_APPROVAL, APPROVED, DECLINED, COMPLETED
}

enum class LessonDifficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeenAccountScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onLessonClick: (String) -> Unit,
    onRequestMoneyClick: () -> Unit,
    onParentalControlsClick: () -> Unit
) {
    val teenAccount = remember { getDummyTeenAccount() }
    val recentTransactions = remember { getDummyTeenTransactions() }
    val financialLessons = remember { getDummyFinancialLessons() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Transactions", "Goals", "Learn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${teenAccount.teenName}'s Account") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onParentalControlsClick) {
                        Icon(Icons.Default.Security, contentDescription = "Parental Controls")
                    }
                    IconButton(onClick = { /* Teen settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onRequestMoneyClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.RequestQuote, contentDescription = "Request Money")
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
                0 -> TeenOverviewContent(
                    account = teenAccount,
                    onGoalClick = onGoalClick,
                    onRequestMoneyClick = onRequestMoneyClick
                )
                1 -> TeenTransactionsContent(
                    transactions = recentTransactions,
                    onTransactionClick = onTransactionClick
                )
                2 -> TeenGoalsContent(
                    goals = teenAccount.savingsGoals,
                    onGoalClick = onGoalClick
                )
                3 -> FinancialEducationContent(
                    lessons = financialLessons,
                    onLessonClick = onLessonClick
                )
            }
        }
    }
}

@Composable
fun TeenOverviewContent(
    account: TeenAccount,
    onGoalClick: (String) -> Unit,
    onRequestMoneyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Account Balance Card
        item {
            TeenAccountBalanceCard(account = account)
        }

        // Quick Actions
        item {
            TeenQuickActions(onRequestMoneyClick = onRequestMoneyClick)
        }

        // Spending Overview
        item {
            SpendingOverviewCard(account = account)
        }

        // Active Goals Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Savings Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { /* View all goals */ }) {
                    Text("View All")
                }
            }
        }

        items(account.savingsGoals.take(2)) { goal ->
            TeenSavingsGoalCard(
                goal = goal,
                onClick = { onGoalClick(goal.id) }
            )
        }

        // Recent Achievements
        item {
            RecentAchievementsSection(achievements = account.achievements)
        }
    }
}

@Composable
fun TeenTransactionsContent(
    transactions: List<TeenTransaction>,
    onTransactionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Pending Approvals
        val pendingTransactions = transactions.filter { it.status == TeenTransactionStatus.PENDING_APPROVAL }
        if (pendingTransactions.isNotEmpty()) {
            item {
                Text(
                    text = "Pending Approval (${pendingTransactions.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF9800)
                )
            }
            
            items(pendingTransactions) { transaction ->
                TeenTransactionCard(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }
        }

        // All Transactions
        item {
            Text(
                text = "All Transactions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(transactions) { transaction ->
            TeenTransactionCard(
                transaction = transaction,
                onClick = { onTransactionClick(transaction.id) }
            )
        }
    }
}

@Composable
fun TeenGoalsContent(
    goals: List<SavingsGoal>,
    onGoalClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "My Savings Goals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (goals.isEmpty()) {
            item {
                EmptyGoalsState()
            }
        } else {
            items(goals) { goal ->
                TeenSavingsGoalCard(
                    goal = goal,
                    onClick = { onGoalClick(goal.id) }
                )
            }
        }
    }
}

@Composable
fun FinancialEducationContent(
    lessons: List<FinancialLesson>,
    onLessonClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Financial Education",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Learn about money management and earn points!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Progress Overview
        item {
            LearningProgressCard(lessons = lessons)
        }

        // Lessons by Category
        val categories = lessons.groupBy { it.category }
        categories.forEach { (category, categoryLessons) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            items(categoryLessons) { lesson ->
                FinancialLessonCard(
                    lesson = lesson,
                    onClick = { onLessonClick(lesson.id) }
                )
            }
        }
    }
}

@Composable
fun TeenAccountBalanceCard(account: TeenAccount) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Available Balance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "£${String.format("%,.2f", account.balance)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Teen Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = account.teenName.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Monthly Allowance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£${String.format("%.2f", account.monthlyAllowance)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Spending Limit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£${String.format("%.2f", account.spendingLimit)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun TeenQuickActions(onRequestMoneyClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Request Money",
            icon = Icons.Default.RequestQuote,
            onClick = onRequestMoneyClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Send to Friend",
            icon = Icons.Default.Send,
            onClick = { /* Send to friend */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Save Money",
            icon = Icons.Default.Savings,
            onClick = { /* Save money */ },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SpendingOverviewCard(account: TeenAccount) {
    val spentThisWeek = account.parentalControls.weeklySpendingLimit * 0.6 // Mock data
    val remainingWeekly = account.parentalControls.weeklySpendingLimit - spentThisWeek
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Weekly Spending",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = (spentThisWeek / account.parentalControls.weeklySpendingLimit).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = if (remainingWeekly < 20) Color.Red else MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Spent: £${String.format("%.2f", spentThisWeek)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Remaining: £${String.format("%.2f", remainingWeekly)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (remainingWeekly < 20) Color.Red else Color.Green
                )
            }
        }
    }
}

@Composable
fun TeenSavingsGoalCard(
    goal: SavingsGoal,
    onClick: () -> Unit
) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    
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
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = goal.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                GoalPriorityChip(priority = goal.priority)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = when (goal.priority) {
                    GoalPriority.HIGH -> Color.Red
                    GoalPriority.MEDIUM -> Color(0xFFFF9800)
                    GoalPriority.LOW -> Color.Green
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "£${String.format("%.0f", goal.currentAmount)} / £${String.format("%.0f", goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Due: ${goal.targetDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TeenTransactionCard(
    transaction: TeenTransaction,
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
                imageVector = when (transaction.status) {
                    TeenTransactionStatus.PENDING_APPROVAL -> Icons.Default.HourglassEmpty
                    TeenTransactionStatus.APPROVED -> Icons.Default.CheckCircle
                    TeenTransactionStatus.DECLINED -> Icons.Default.Cancel
                    TeenTransactionStatus.COMPLETED -> Icons.Default.Done
                },
                contentDescription = null,
                tint = when (transaction.status) {
                    TeenTransactionStatus.PENDING_APPROVAL -> Color(0xFFFF9800)
                    TeenTransactionStatus.APPROVED -> Color.Green
                    TeenTransactionStatus.DECLINED -> Color.Red
                    TeenTransactionStatus.COMPLETED -> Color.Blue
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${transaction.merchant} • ${transaction.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "-£${String.format("%,.2f", transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                TeenTransactionStatusChip(status = transaction.status)
            }
        }
    }
}

@Composable
fun RecentAchievementsSection(achievements: List<Achievement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Achievements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements.take(3)) { achievement ->
                    AchievementCard(achievement = achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "+${achievement.points} pts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun LearningProgressCard(lessons: List<FinancialLesson>) {
    val completedLessons = lessons.count { it.isCompleted }
    val totalPoints = lessons.filter { it.isCompleted }.sumOf { it.points }
    
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
                text = "Learning Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = completedLessons.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Lessons Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = totalPoints.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Points Earned",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialLessonCard(
    lesson: FinancialLesson,
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
                imageVector = if (lesson.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircleOutline,
                contentDescription = null,
                tint = if (lesson.isCompleted) Color.Green else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${lesson.duration} • ${lesson.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "+${lesson.points} pts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (lesson.isCompleted) {
                    Surface(
                        color = Color.Green.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Green,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalPriorityChip(priority: GoalPriority) {
    val (color, text) = when (priority) {
        GoalPriority.HIGH -> Color.Red to "High"
        GoalPriority.MEDIUM -> Color(0xFFFF9800) to "Medium"
        GoalPriority.LOW -> Color.Green to "Low"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TeenTransactionStatusChip(status: TeenTransactionStatus) {
    val (color, text) = when (status) {
        TeenTransactionStatus.PENDING_APPROVAL -> Color(0xFFFF9800) to "Pending"
        TeenTransactionStatus.APPROVED -> Color.Green to "Approved"
        TeenTransactionStatus.DECLINED -> Color.Red to "Declined"
        TeenTransactionStatus.COMPLETED -> Color.Blue to "Completed"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

fun getDummyTeenAccount(): TeenAccount {
    return TeenAccount(
        id = "TA001",
        teenName = "Emma Smith",
        age = 16,
        balance = 350.00,
        monthlyAllowance = 100.0,
        spendingLimit = 200.0,
        parentalControls = ParentalControls(
            dailySpendingLimit = 25.0,
            weeklySpendingLimit = 100.0,
            blockedCategories = listOf("Gambling", "Alcohol", "Adult Content"),
            allowedMerchants = listOf("School Cafeteria", "Local Library", "Bus Service"),
            requireApprovalForPurchases = true,
            approvalThreshold = 20.0,
            curfewHours = Pair(22, 6), // 10 PM to 6 AM
            locationRestrictions = listOf("School Zone", "Home Area")
        ),
        savingsGoals = listOf(
            SavingsGoal(
                id = "SG001",
                title = "New iPhone",
                targetAmount = 800.0,
                currentAmount = 450.0,
                targetDate = "Dec 2024",
                category = "Electronics",
                priority = GoalPriority.HIGH
            ),
            SavingsGoal(
                id = "SG002",
                title = "Summer Camp",
                targetAmount = 500.0,
                currentAmount = 200.0,
                targetDate = "Jun 2025",
                category = "Activities",
                priority = GoalPriority.MEDIUM
            )
        ),
        achievements = listOf(
            Achievement(
                id = "ACH001",
                title = "First Saver",
                description = "Saved your first £50",
                icon = Icons.Default.Savings,
                earnedDate = "Oct 15, 2024",
                points = 50,
                category = AchievementCategory.SAVING
            ),
            Achievement(
                id = "ACH002",
                title = "Budget Master",
                description = "Stayed under budget for a month",
                icon = Icons.Default.TrendingDown,
                earnedDate = "Oct 20, 2024",
                points = 100,
                category = AchievementCategory.SPENDING
            ),
            Achievement(
                id = "ACH003",
                title = "Learning Star",
                description = "Completed 5 financial lessons",
                icon = Icons.Default.School,
                earnedDate = "Oct 25, 2024",
                points = 75,
                category = AchievementCategory.LEARNING
            )
        ),
        isActive = true
    )
}

fun getDummyTeenTransactions(): List<TeenTransaction> {
    return listOf(
        TeenTransaction(
            id = "TT001",
            description = "Coffee with friends",
            amount = 12.50,
            date = "Today",
            category = "Food & Drink",
            merchant = "Starbucks",
            status = TeenTransactionStatus.COMPLETED,
            requiresApproval = false,
            parentApproval = null
        ),
        TeenTransaction(
            id = "TT002",
            description = "Online game purchase",
            amount = 25.00,
            date = "1 hour ago",
            category = "Entertainment",
            merchant = "Steam",
            status = TeenTransactionStatus.PENDING_APPROVAL,
            requiresApproval = true,
            parentApproval = null
        ),
        TeenTransaction(
            id = "TT003",
            description = "School lunch",
            amount = 4.50,
            date = "Yesterday",
            category = "Food & Drink",
            merchant = "School Cafeteria",
            status = TeenTransactionStatus.COMPLETED,
            requiresApproval = false,
            parentApproval = null
        ),
        TeenTransaction(
            id = "TT004",
            description = "Movie tickets",
            amount = 15.00,
            date = "2 days ago",
            category = "Entertainment",
            merchant = "Cinema",
            status = TeenTransactionStatus.APPROVED,
            requiresApproval = true,
            parentApproval = true
        )
    )
}

fun getDummyFinancialLessons(): List<FinancialLesson> {
    return listOf(
        FinancialLesson(
            id = "FL001",
            title = "Understanding Budgets",
            description = "Learn how to create and stick to a budget",
            duration = "10 min",
            difficulty = LessonDifficulty.BEGINNER,
            points = 25,
            isCompleted = true,
            category = "Budgeting"
        ),
        FinancialLesson(
            id = "FL002",
            title = "The Power of Saving",
            description = "Discover why saving money is important",
            duration = "8 min",
            difficulty = LessonDifficulty.BEGINNER,
            points = 20,
            isCompleted = true,
            category = "Saving"
        ),
        FinancialLesson(
            id = "FL003",
            title = "Smart Spending Habits",
            description = "Learn to make better spending decisions",
            duration = "12 min",
            difficulty = LessonDifficulty.INTERMEDIATE,
            points = 30,
            isCompleted = false,
            category = "Spending"
        ),
        FinancialLesson(
            id = "FL004",
            title = "Introduction to Investing",
            description = "Basic concepts of investing for teens",
            duration = "15 min",
            difficulty = LessonDifficulty.INTERMEDIATE,
            points = 40,
            isCompleted = false,
            category = "Investing"
        ),
        FinancialLesson(
            id = "FL005",
            title = "Credit and Debt Basics",
            description = "Understanding credit scores and debt",
            duration = "18 min",
            difficulty = LessonDifficulty.ADVANCED,
            points = 50,
            isCompleted = false,
            category = "Credit"
        )
    )
}
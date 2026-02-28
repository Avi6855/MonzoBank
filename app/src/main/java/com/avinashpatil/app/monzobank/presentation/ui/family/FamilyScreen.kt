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

data class FamilyMember(
    val id: String,
    val name: String,
    val relationship: String,
    val age: Int,
    val accountType: FamilyAccountType,
    val balance: Double,
    val allowance: Double,
    val spendingLimit: Double,
    val isActive: Boolean,
    val avatar: String? = null
)

data class FamilyGoal(
    val id: String,
    val title: String,
    val description: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String,
    val contributors: List<String>,
    val category: GoalCategory
)

data class FamilyActivity(
    val id: String,
    val memberName: String,
    val action: String,
    val amount: Double,
    val timestamp: String,
    val category: String,
    val icon: ImageVector
)

data class FamilyChore(
    val id: String,
    val title: String,
    val description: String,
    val assignedTo: String,
    val reward: Double,
    val dueDate: String,
    val isCompleted: Boolean,
    val difficulty: ChoreDifficulty
)

enum class FamilyAccountType {
    PARENT, TEEN, CHILD, JOINT
}

enum class GoalCategory {
    VACATION, EDUCATION, EMERGENCY, ENTERTAINMENT, OTHER
}

enum class ChoreDifficulty {
    EASY, MEDIUM, HARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScreen(
    onBackClick: () -> Unit,
    onMemberClick: (String) -> Unit,
    onGoalClick: (String) -> Unit,
    onActivityClick: (String) -> Unit,
    onAddMemberClick: () -> Unit,
    onCreateGoalClick: () -> Unit
) {
    val familyMembers = remember { getDummyFamilyMembers() }
    val familyGoals = remember { getDummyFamilyGoals() }
    val familyActivities = remember { getDummyFamilyActivities() }
    val familyChores = remember { getDummyFamilyChores() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Members", "Goals", "Activities")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Family Banking") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Family settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
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
                    onClick = onAddMemberClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = "Add Member")
                }
                2 -> FloatingActionButton(
                    onClick = onCreateGoalClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Goal")
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
                0 -> FamilyOverviewContent(
                    members = familyMembers,
                    goals = familyGoals,
                    chores = familyChores,
                    onMemberClick = onMemberClick,
                    onGoalClick = onGoalClick
                )
                1 -> FamilyMembersContent(
                    members = familyMembers,
                    onMemberClick = onMemberClick
                )
                2 -> FamilyGoalsContent(
                    goals = familyGoals,
                    onGoalClick = onGoalClick
                )
                3 -> FamilyActivitiesContent(
                    activities = familyActivities,
                    onActivityClick = onActivityClick
                )
            }
        }
    }
}

@Composable
fun FamilyOverviewContent(
    members: List<FamilyMember>,
    goals: List<FamilyGoal>,
    chores: List<FamilyChore>,
    onMemberClick: (String) -> Unit,
    onGoalClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Family Summary
        item {
            FamilySummaryCard(members = members)
        }

        // Quick Actions
        item {
            FamilyQuickActions()
        }

        // Family Members Preview
        item {
            Text(
                text = "Family Members",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(members.take(4)) { member ->
                    FamilyMemberPreviewCard(
                        member = member,
                        onClick = { onMemberClick(member.id) }
                    )
                }
            }
        }

        // Active Goals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Goals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { /* View all goals */ }) {
                    Text("View All")
                }
            }
        }

        items(goals.take(2)) { goal ->
            FamilyGoalCard(
                goal = goal,
                onClick = { onGoalClick(goal.id) }
            )
        }

        // Chores & Rewards
        item {
            ChoresRewardsSection(chores = chores)
        }
    }
}

@Composable
fun FamilyMembersContent(
    members: List<FamilyMember>,
    onMemberClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Family Members (${members.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(members) { member ->
            FamilyMemberCard(
                member = member,
                onClick = { onMemberClick(member.id) }
            )
        }
    }
}

@Composable
fun FamilyGoalsContent(
    goals: List<FamilyGoal>,
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
                text = "Family Goals",
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
                FamilyGoalCard(
                    goal = goal,
                    onClick = { onGoalClick(goal.id) }
                )
            }
        }
    }
}

@Composable
fun FamilyActivitiesContent(
    activities: List<FamilyActivity>,
    onActivityClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Recent Activities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(activities) { activity ->
            FamilyActivityCard(
                activity = activity,
                onClick = { onActivityClick(activity.id) }
            )
        }
    }
}

@Composable
fun FamilySummaryCard(members: List<FamilyMember>) {
    val totalBalance = members.sumOf { it.balance }
    val totalAllowance = members.sumOf { it.allowance }
    val activeMembers = members.count { it.isActive }
    
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Family Overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Managing finances together",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Total Balance",
                    value = "£${String.format("%,.2f", totalBalance)}",
                    icon = Icons.Default.AccountBalance
                )
                SummaryItem(
                    title = "Monthly Allowance",
                    value = "£${String.format("%,.2f", totalAllowance)}",
                    icon = Icons.Default.Savings
                )
                SummaryItem(
                    title = "Active Members",
                    value = activeMembers.toString(),
                    icon = Icons.Default.People
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
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
fun FamilyQuickActions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Send Money",
            icon = Icons.Default.Send,
            onClick = { /* Send money */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Set Allowance",
            icon = Icons.Default.Schedule,
            onClick = { /* Set allowance */ },
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            title = "Track Spending",
            icon = Icons.Default.Analytics,
            onClick = { /* Track spending */ },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun FamilyMemberPreviewCard(
    member: FamilyMember,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getAccountTypeColor(member.accountType).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getAccountTypeColor(member.accountType)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = member.relationship,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "£${String.format("%.0f", member.balance)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMember,
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
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(getAccountTypeColor(member.accountType).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = getAccountTypeColor(member.accountType)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${member.relationship} • Age ${member.age}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FamilyAccountTypeChip(type = member.accountType)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (!member.isActive) {
                        Surface(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Inactive",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%,.2f", member.balance)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (member.allowance > 0) {
                    Text(
                        text = "£${String.format("%.0f", member.allowance)}/month",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun FamilyGoalCard(
    goal: FamilyGoal,
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
                        text = goal.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                GoalCategoryChip(category = goal.category)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "£${String.format("%,.0f", goal.currentAmount)} / £${String.format("%,.0f", goal.targetAmount)}",
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
fun FamilyActivityCard(
    activity: FamilyActivity,
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
                imageVector = activity.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.action,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${activity.memberName} • ${activity.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (activity.amount != 0.0) {
                    Text(
                        text = "${if (activity.amount > 0) "+" else ""}£${String.format("%.2f", activity.amount)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (activity.amount > 0) Color.Green else Color.Red
                    )
                }
                Text(
                    text = activity.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ChoresRewardsSection(chores: List<FamilyChore>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                Text(
                    text = "Chores & Rewards",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                TextButton(onClick = { /* Manage chores */ }) {
                    Text("Manage")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val pendingChores = chores.filter { !it.isCompleted }
            val completedChores = chores.filter { it.isCompleted }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = pendingChores.size.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Pending",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = completedChores.size.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "£${String.format("%.0f", completedChores.sumOf { it.reward })}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Earned",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun FamilyAccountTypeChip(type: FamilyAccountType) {
    val (color, text) = when (type) {
        FamilyAccountType.PARENT -> Color(0xFF2196F3) to "Parent"
        FamilyAccountType.TEEN -> Color(0xFF9C27B0) to "Teen"
        FamilyAccountType.CHILD -> Color(0xFF4CAF50) to "Child"
        FamilyAccountType.JOINT -> Color(0xFFFF9800) to "Joint"
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
fun GoalCategoryChip(category: GoalCategory) {
    val (color, text) = when (category) {
        GoalCategory.VACATION -> Color(0xFF2196F3) to "Vacation"
        GoalCategory.EDUCATION -> Color(0xFF4CAF50) to "Education"
        GoalCategory.EMERGENCY -> Color(0xFFFF5722) to "Emergency"
        GoalCategory.ENTERTAINMENT -> Color(0xFF9C27B0) to "Entertainment"
        GoalCategory.OTHER -> Color(0xFF607D8B) to "Other"
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
fun EmptyGoalsState() {
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
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Family Goals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first family goal to start saving together.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getAccountTypeColor(type: FamilyAccountType): Color {
    return when (type) {
        FamilyAccountType.PARENT -> Color(0xFF2196F3)
        FamilyAccountType.TEEN -> Color(0xFF9C27B0)
        FamilyAccountType.CHILD -> Color(0xFF4CAF50)
        FamilyAccountType.JOINT -> Color(0xFFFF9800)
    }
}

fun getDummyFamilyMembers(): List<FamilyMember> {
    return listOf(
        FamilyMember(
            id = "FM001",
            name = "John Smith",
            relationship = "Parent",
            age = 42,
            accountType = FamilyAccountType.PARENT,
            balance = 2500.00,
            allowance = 0.0,
            spendingLimit = 5000.0,
            isActive = true
        ),
        FamilyMember(
            id = "FM002",
            name = "Sarah Smith",
            relationship = "Parent",
            age = 39,
            accountType = FamilyAccountType.PARENT,
            balance = 1800.00,
            allowance = 0.0,
            spendingLimit = 3000.0,
            isActive = true
        ),
        FamilyMember(
            id = "FM003",
            name = "Emma Smith",
            relationship = "Daughter",
            age = 16,
            accountType = FamilyAccountType.TEEN,
            balance = 350.00,
            allowance = 100.0,
            spendingLimit = 200.0,
            isActive = true
        ),
        FamilyMember(
            id = "FM004",
            name = "Jake Smith",
            relationship = "Son",
            age = 12,
            accountType = FamilyAccountType.CHILD,
            balance = 125.00,
            allowance = 50.0,
            spendingLimit = 100.0,
            isActive = true
        )
    )
}

fun getDummyFamilyGoals(): List<FamilyGoal> {
    return listOf(
        FamilyGoal(
            id = "FG001",
            title = "Summer Vacation 2025",
            description = "Family trip to Spain",
            targetAmount = 5000.0,
            currentAmount = 2750.0,
            targetDate = "Jun 2025",
            contributors = listOf("John", "Sarah", "Emma"),
            category = GoalCategory.VACATION
        ),
        FamilyGoal(
            id = "FG002",
            title = "Emma's University Fund",
            description = "Saving for Emma's education",
            targetAmount = 15000.0,
            currentAmount = 8500.0,
            targetDate = "Sep 2026",
            contributors = listOf("John", "Sarah"),
            category = GoalCategory.EDUCATION
        ),
        FamilyGoal(
            id = "FG003",
            title = "Emergency Fund",
            description = "Family emergency savings",
            targetAmount = 10000.0,
            currentAmount = 6200.0,
            targetDate = "Dec 2024",
            contributors = listOf("John", "Sarah"),
            category = GoalCategory.EMERGENCY
        )
    )
}

fun getDummyFamilyActivities(): List<FamilyActivity> {
    return listOf(
        FamilyActivity(
            id = "FA001",
            memberName = "Emma",
            action = "Received allowance",
            amount = 100.0,
            timestamp = "2 hours ago",
            category = "Allowance",
            icon = Icons.Default.Savings
        ),
        FamilyActivity(
            id = "FA002",
            memberName = "Jake",
            action = "Completed chore",
            amount = 10.0,
            timestamp = "5 hours ago",
            category = "Chores",
            icon = Icons.Default.CheckCircle
        ),
        FamilyActivity(
            id = "FA003",
            memberName = "Sarah",
            action = "Added to vacation fund",
            amount = 200.0,
            timestamp = "1 day ago",
            category = "Goals",
            icon = Icons.Default.Savings
        ),
        FamilyActivity(
            id = "FA004",
            memberName = "Emma",
            action = "Spent on shopping",
            amount = -45.0,
            timestamp = "2 days ago",
            category = "Shopping",
            icon = Icons.Default.ShoppingBag
        ),
        FamilyActivity(
            id = "FA005",
            memberName = "John",
            action = "Transferred to emergency fund",
            amount = 500.0,
            timestamp = "3 days ago",
            category = "Goals",
            icon = Icons.Default.Security
        )
    )
}

fun getDummyFamilyChores(): List<FamilyChore> {
    return listOf(
        FamilyChore(
            id = "FC001",
            title = "Take out trash",
            description = "Empty all trash bins and take to curb",
            assignedTo = "Jake",
            reward = 5.0,
            dueDate = "Today",
            isCompleted = false,
            difficulty = ChoreDifficulty.EASY
        ),
        FamilyChore(
            id = "FC002",
            title = "Clean room",
            description = "Organize and vacuum bedroom",
            assignedTo = "Emma",
            reward = 10.0,
            dueDate = "Tomorrow",
            isCompleted = true,
            difficulty = ChoreDifficulty.MEDIUM
        ),
        FamilyChore(
            id = "FC003",
            title = "Wash dishes",
            description = "Clean all dishes after dinner",
            assignedTo = "Emma",
            reward = 8.0,
            dueDate = "Today",
            isCompleted = false,
            difficulty = ChoreDifficulty.EASY
        ),
        FamilyChore(
            id = "FC004",
            title = "Mow lawn",
            description = "Cut grass in front and back yard",
            assignedTo = "Jake",
            reward = 15.0,
            dueDate = "This weekend",
            isCompleted = true,
            difficulty = ChoreDifficulty.HARD
        )
    )
}
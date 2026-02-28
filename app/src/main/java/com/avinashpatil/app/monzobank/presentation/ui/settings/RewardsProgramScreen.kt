package com.avinashpatil.app.monzobank.presentation.ui.settings

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

data class RewardProgram(
    val id: String,
    val name: String,
    val description: String,
    val currentPoints: Int,
    val totalEarned: Int,
    val membershipLevel: MembershipLevel,
    val nextLevelPoints: Int?,
    val benefits: List<String>
)

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val pointsCost: Int,
    val category: RewardCategory,
    val icon: ImageVector,
    val color: Color,
    val isAvailable: Boolean = true,
    val expiryDate: String? = null,
    val termsAndConditions: String? = null
)

data class PointsTransaction(
    val id: String,
    val description: String,
    val points: Int,
    val type: TransactionType,
    val date: String,
    val relatedActivity: String? = null
)

data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val pointsReward: Int,
    val progress: Int,
    val target: Int,
    val deadline: String,
    val isCompleted: Boolean = false,
    val icon: ImageVector,
    val color: Color
)

data class Partner(
    val id: String,
    val name: String,
    val description: String,
    val logo: String,
    val pointsMultiplier: Float,
    val category: PartnerCategory,
    val specialOffer: String? = null
)

enum class MembershipLevel {
    BRONZE, SILVER, GOLD, PLATINUM, DIAMOND
}

enum class RewardCategory {
    CASHBACK, VOUCHERS, EXPERIENCES, CHARITY, TRAVEL, SHOPPING
}

enum class TransactionType {
    EARNED, REDEEMED, BONUS, EXPIRED
}

enum class PartnerCategory {
    RETAIL, DINING, TRAVEL, ENTERTAINMENT, SERVICES
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardsProgramScreen(
    onBackClick: () -> Unit,
    onRewardClick: (String) -> Unit,
    onRedeemClick: (String) -> Unit,
    onChallengeClick: (String) -> Unit,
    onPartnerClick: (String) -> Unit,
    onViewHistoryClick: () -> Unit
) {
    val rewardProgram = remember { getDummyRewardProgram() }
    val availableRewards = remember { getDummyAvailableRewards() }
    val pointsHistory = remember { getDummyPointsHistory() }
    val activeChallenges = remember { getDummyActiveChallenges() }
    val partners = remember { getDummyPartners() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Rewards", "Challenges", "Partners")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rewards Program") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewHistoryClick) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { /* Rewards settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
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
                0 -> RewardsOverviewContent(
                    program = rewardProgram,
                    recentTransactions = pointsHistory.take(5),
                    featuredRewards = availableRewards.take(3),
                    onRewardClick = onRewardClick,
                    onViewAllClick = { selectedTab = 1 }
                )
                1 -> RewardsContent(
                    rewards = availableRewards,
                    currentPoints = rewardProgram.currentPoints,
                    onRewardClick = onRewardClick,
                    onRedeemClick = onRedeemClick
                )
                2 -> ChallengesContent(
                    challenges = activeChallenges,
                    onChallengeClick = onChallengeClick
                )
                3 -> PartnersContent(
                    partners = partners,
                    onPartnerClick = onPartnerClick
                )
            }
        }
    }
}

@Composable
fun RewardsOverviewContent(
    program: RewardProgram,
    recentTransactions: List<PointsTransaction>,
    featuredRewards: List<Reward>,
    onRewardClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Program Overview
        item {
            RewardsProgramCard(program = program)
        }

        // Points Balance
        item {
            PointsBalanceCard(
                currentPoints = program.currentPoints,
                totalEarned = program.totalEarned
            )
        }

        // Membership Progress
        item {
            MembershipProgressCard(
                currentLevel = program.membershipLevel,
                currentPoints = program.currentPoints,
                nextLevelPoints = program.nextLevelPoints
            )
        }

        // Quick Actions
        item {
            RewardsQuickActionsSection(
                onEarnPointsClick = { /* Earn points guide */ },
                onRedeemRewardsClick = onViewAllClick,
                onViewHistoryClick = { /* View full history */ }
            )
        }

        // Featured Rewards
        if (featuredRewards.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Rewards",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = onViewAllClick) {
                        Text("View All")
                    }
                }
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(featuredRewards) { reward ->
                        FeaturedRewardCard(
                            reward = reward,
                            onClick = { onRewardClick(reward.id) }
                        )
                    }
                }
            }
        }

        // Recent Activity
        if (recentTransactions.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(recentTransactions) { transaction ->
                PointsTransactionCard(transaction = transaction)
            }
        }

        // Earning Tips
        item {
            EarningTipsCard()
        }
    }
}

@Composable
fun RewardsContent(
    rewards: List<Reward>,
    currentPoints: Int,
    onRewardClick: (String) -> Unit,
    onRedeemClick: (String) -> Unit
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
                    text = "Available Rewards",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "${currentPoints} points",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Filter by category
        val rewardsByCategory = rewards.groupBy { it.category }
        rewardsByCategory.forEach { (category, categoryRewards) ->
            item {
                Text(
                    text = getRewardCategoryName(category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(categoryRewards) { reward ->
                RewardCard(
                    reward = reward,
                    canAfford = currentPoints >= reward.pointsCost,
                    onClick = { onRewardClick(reward.id) },
                    onRedeemClick = { onRedeemClick(reward.id) }
                )
            }
        }
    }
}

@Composable
fun ChallengesContent(
    challenges: List<Challenge>,
    onChallengeClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Active Challenges",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Complete challenges to earn bonus points",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (challenges.isNotEmpty()) {
            items(challenges) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = { onChallengeClick(challenge.id) }
                )
            }
        } else {
            item {
                EmptyChallengesState()
            }
        }

        // Challenge Tips
        item {
            ChallengeTipsCard()
        }
    }
}

@Composable
fun PartnersContent(
    partners: List<Partner>,
    onPartnerClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Partner Merchants",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Earn extra points when you shop with our partners",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Group by category
        val partnersByCategory = partners.groupBy { it.category }
        partnersByCategory.forEach { (category, categoryPartners) ->
            item {
                Text(
                    text = getPartnerCategoryName(category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            items(categoryPartners) { partner ->
                PartnerCard(
                    partner = partner,
                    onClick = { onPartnerClick(partner.id) }
                )
            }
        }
    }
}

@Composable
fun RewardsProgramCard(
    program: RewardProgram
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = program.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = program.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                MembershipLevelChip(level = program.membershipLevel)
            }
        }
    }
}

@Composable
fun PointsBalanceCard(
    currentPoints: Int,
    totalEarned: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Points Balance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PointsStatItem(
                    title = "Available",
                    value = currentPoints.toString(),
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color.Green
                )
                PointsStatItem(
                    title = "Total Earned",
                    value = totalEarned.toString(),
                    icon = Icons.Default.TrendingUp,
                    color = Color.Blue
                )
                PointsStatItem(
                    title = "Redeemed",
                    value = (totalEarned - currentPoints).toString(),
                    icon = Icons.Default.Redeem,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun PointsStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MembershipProgressCard(
    currentLevel: MembershipLevel,
    currentPoints: Int,
    nextLevelPoints: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = getMembershipLevelColor(currentLevel).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Membership Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Level: ${getMembershipLevelName(currentLevel)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (nextLevelPoints != null) {
                    Text(
                        text = "${nextLevelPoints - currentPoints} points to next level",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (nextLevelPoints != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = currentPoints.toFloat() / nextLevelPoints.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = getMembershipLevelColor(currentLevel)
                )
            }
        }
    }
}

@Composable
fun RewardsQuickActionsSection(
    onEarnPointsClick: () -> Unit,
    onRedeemRewardsClick: () -> Unit,
    onViewHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                RewardsQuickActionButton(
                    icon = Icons.Default.Add,
                    label = "Earn Points",
                    onClick = onEarnPointsClick
                )
                RewardsQuickActionButton(
                    icon = Icons.Default.Redeem,
                    label = "Redeem",
                    onClick = onRedeemRewardsClick
                )
                RewardsQuickActionButton(
                    icon = Icons.Default.History,
                    label = "History",
                    onClick = onViewHistoryClick
                )
            }
        }
    }
}

@Composable
fun RewardsQuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun FeaturedRewardCard(
    reward: Reward,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = reward.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = reward.icon,
                contentDescription = null,
                tint = reward.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${reward.pointsCost} points",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = reward.color
            )
        }
    }
}

@Composable
fun RewardCard(
    reward: Reward,
    canAfford: Boolean,
    onClick: () -> Unit,
    onRedeemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) {
                reward.color.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            }
        )
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
                    .background(reward.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = reward.icon,
                    contentDescription = null,
                    tint = reward.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (canAfford) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                reward.expiryDate?.let { expiry ->
                    Text(
                        text = "Expires: $expiry",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${reward.pointsCost}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = reward.color
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onRedeemClick,
                    enabled = canAfford && reward.isAvailable,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = if (canAfford) "Redeem" else "Not enough points",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PointsTransactionCard(
    transaction: PointsTransaction
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (transaction.type) {
                    TransactionType.EARNED -> Icons.Default.Add
                    TransactionType.REDEEMED -> Icons.Default.Remove
                    TransactionType.BONUS -> Icons.Default.Star
                    TransactionType.EXPIRED -> Icons.Default.Schedule
                },
                contentDescription = null,
                tint = when (transaction.type) {
                    TransactionType.EARNED -> Color.Green
                    TransactionType.REDEEMED -> Color.Red
                    TransactionType.BONUS -> Color(0xFFFF9800)
                    TransactionType.EXPIRED -> Color.Gray
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
                    text = transaction.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                transaction.relatedActivity?.let { activity ->
                    Text(
                        text = activity,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "${if (transaction.type == TransactionType.REDEEMED) "-" else "+"}${transaction.points}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = when (transaction.type) {
                    TransactionType.EARNED -> Color.Green
                    TransactionType.REDEEMED -> Color.Red
                    TransactionType.BONUS -> Color(0xFFFF9800)
                    TransactionType.EXPIRED -> Color.Gray
                }
            )
        }
    }
}

@Composable
fun ChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) {
                Color.Green.copy(alpha = 0.1f)
            } else {
                challenge.color.copy(alpha = 0.1f)
            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = null,
                        tint = challenge.color,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = challenge.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = challenge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (challenge.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progress: ${challenge.progress}/${challenge.target}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Reward: ${challenge.pointsReward} points",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = challenge.color
                    )
                }
                
                Text(
                    text = "Deadline: ${challenge.deadline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!challenge.isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = challenge.progress.toFloat() / challenge.target.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = challenge.color
                )
            }
        }
    }
}

@Composable
fun PartnerCard(
    partner: Partner,
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = partner.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = partner.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = partner.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                partner.specialOffer?.let { offer ->
                    Text(
                        text = offer,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF9800)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${partner.pointsMultiplier}x",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "points",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EarningTipsCard() {
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
                text = "Earning Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val tips = listOf(
                "Use your Monzo card for everyday purchases",
                "Complete monthly challenges for bonus points",
                "Shop with partner merchants for extra rewards",
                "Refer friends to earn referral bonuses"
            )
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun ChallengeTipsCard() {
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
                text = "Challenge Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val tips = listOf(
                "Check for new challenges weekly",
                "Focus on challenges that match your spending habits",
                "Set reminders for challenge deadlines",
                "Combine multiple challenges for maximum points"
            )
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun MembershipLevelChip(level: MembershipLevel) {
    val (color, text) = when (level) {
        MembershipLevel.BRONZE -> Color(0xFFCD7F32) to "Bronze"
        MembershipLevel.SILVER -> Color(0xFFC0C0C0) to "Silver"
        MembershipLevel.GOLD -> Color(0xFFFFD700) to "Gold"
        MembershipLevel.PLATINUM -> Color(0xFFE5E4E2) to "Platinum"
        MembershipLevel.DIAMOND -> Color(0xFFB9F2FF) to "Diamond"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
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
fun EmptyChallengesState() {
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
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Challenges",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "New challenges will appear here. Check back regularly for new opportunities to earn bonus points.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getMembershipLevelName(level: MembershipLevel): String {
    return when (level) {
        MembershipLevel.BRONZE -> "Bronze"
        MembershipLevel.SILVER -> "Silver"
        MembershipLevel.GOLD -> "Gold"
        MembershipLevel.PLATINUM -> "Platinum"
        MembershipLevel.DIAMOND -> "Diamond"
    }
}

fun getMembershipLevelColor(level: MembershipLevel): Color {
    return when (level) {
        MembershipLevel.BRONZE -> Color(0xFFCD7F32)
        MembershipLevel.SILVER -> Color(0xFFC0C0C0)
        MembershipLevel.GOLD -> Color(0xFFFFD700)
        MembershipLevel.PLATINUM -> Color(0xFFE5E4E2)
        MembershipLevel.DIAMOND -> Color(0xFFB9F2FF)
    }
}

fun getRewardCategoryName(category: RewardCategory): String {
    return when (category) {
        RewardCategory.CASHBACK -> "Cashback"
        RewardCategory.VOUCHERS -> "Vouchers & Gift Cards"
        RewardCategory.EXPERIENCES -> "Experiences"
        RewardCategory.CHARITY -> "Charity Donations"
        RewardCategory.TRAVEL -> "Travel & Hotels"
        RewardCategory.SHOPPING -> "Shopping"
    }
}

fun getPartnerCategoryName(category: PartnerCategory): String {
    return when (category) {
        PartnerCategory.RETAIL -> "Retail & Shopping"
        PartnerCategory.DINING -> "Dining & Food"
        PartnerCategory.TRAVEL -> "Travel & Transport"
        PartnerCategory.ENTERTAINMENT -> "Entertainment"
        PartnerCategory.SERVICES -> "Services"
    }
}

fun getDummyRewardProgram(): RewardProgram {
    return RewardProgram(
        id = "monzo_rewards",
        name = "Monzo Rewards",
        description = "Earn points on every purchase and redeem for amazing rewards",
        currentPoints = 2450,
        totalEarned = 5680,
        membershipLevel = MembershipLevel.GOLD,
        nextLevelPoints = 7500,
        benefits = listOf(
            "Earn 1 point per £1 spent",
            "Bonus points on partner purchases",
            "Exclusive member rewards",
            "Priority customer support"
        )
    )
}

fun getDummyAvailableRewards(): List<Reward> {
    return listOf(
        Reward(
            id = "cashback_10",
            title = "£10 Cashback",
            description = "Direct cashback to your account",
            pointsCost = 1000,
            category = RewardCategory.CASHBACK,
            icon = Icons.Default.AttachMoney,
            color = Color.Green
        ),
        Reward(
            id = "amazon_25",
            title = "£25 Amazon Voucher",
            description = "Amazon gift card for online shopping",
            pointsCost = 2500,
            category = RewardCategory.VOUCHERS,
            icon = Icons.Default.CardGiftcard,
            color = Color(0xFFFF9800)
        ),
        Reward(
            id = "cinema_tickets",
            title = "Cinema Tickets for 2",
            description = "Two tickets to any Vue cinema",
            pointsCost = 1800,
            category = RewardCategory.EXPERIENCES,
            icon = Icons.Default.Movie,
            color = Color(0xFF9C27B0),
            expiryDate = "31 Jan 2024"
        ),
        Reward(
            id = "charity_donation",
            title = "£20 Charity Donation",
            description = "Donate to your chosen charity",
            pointsCost = 2000,
            category = RewardCategory.CHARITY,
            icon = Icons.Default.Favorite,
            color = Color.Red
        )
    )
}

fun getDummyPointsHistory(): List<PointsTransaction> {
    return listOf(
        PointsTransaction(
            id = "txn1",
            description = "Purchase at Tesco",
            points = 25,
            type = TransactionType.EARNED,
            date = "Today",
            relatedActivity = "Grocery shopping"
        ),
        PointsTransaction(
            id = "txn2",
            description = "Monthly Challenge Bonus",
            points = 500,
            type = TransactionType.BONUS,
            date = "Yesterday",
            relatedActivity = "Spend £500 in a month"
        ),
        PointsTransaction(
            id = "txn3",
            description = "Redeemed £10 Cashback",
            points = 1000,
            type = TransactionType.REDEEMED,
            date = "2 days ago"
        ),
        PointsTransaction(
            id = "txn4",
            description = "Purchase at Starbucks",
            points = 15,
            type = TransactionType.EARNED,
            date = "3 days ago",
            relatedActivity = "Coffee purchase"
        )
    )
}

fun getDummyActiveChallenges(): List<Challenge> {
    return listOf(
        Challenge(
            id = "challenge1",
            title = "Spend £200 This Month",
            description = "Make purchases totaling £200 or more",
            pointsReward = 200,
            progress = 150,
            target = 200,
            deadline = "31 Jan 2024",
            isCompleted = false,
            icon = Icons.Default.ShoppingCart,
            color = Color.Blue
        ),
        Challenge(
            id = "challenge2",
            title = "Use 5 Different Merchants",
            description = "Shop at 5 different places this week",
            pointsReward = 100,
            progress = 5,
            target = 5,
            deadline = "Sunday",
            isCompleted = true,
            icon = Icons.Default.Store,
            color = Color.Green
        ),
        Challenge(
            id = "challenge3",
            title = "Refer a Friend",
            description = "Invite a friend to join Monzo",
            pointsReward = 1000,
            progress = 0,
            target = 1,
            deadline = "28 Feb 2024",
            isCompleted = false,
            icon = Icons.Default.PersonAdd,
            color = Color(0xFF9C27B0)
        )
    )
}

fun getDummyPartners(): List<Partner> {
    return listOf(
        Partner(
            id = "tesco",
            name = "Tesco",
            description = "UK's leading supermarket chain",
            logo = "",
            pointsMultiplier = 2.0f,
            category = PartnerCategory.RETAIL,
            specialOffer = "Double points this week!"
        ),
        Partner(
            id = "starbucks",
            name = "Starbucks",
            description = "Coffee and beverages",
            logo = "",
            pointsMultiplier = 3.0f,
            category = PartnerCategory.DINING
        ),
        Partner(
            id = "uber",
            name = "Uber",
            description = "Ride sharing and food delivery",
            logo = "",
            pointsMultiplier = 2.5f,
            category = PartnerCategory.TRAVEL
        ),
        Partner(
            id = "netflix",
            name = "Netflix",
            description = "Streaming entertainment",
            logo = "",
            pointsMultiplier = 1.5f,
            category = PartnerCategory.ENTERTAINMENT
        )
    )
}
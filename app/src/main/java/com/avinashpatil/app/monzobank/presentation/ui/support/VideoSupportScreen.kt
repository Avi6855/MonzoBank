package com.avinashpatil.app.monzobank.presentation.ui.support

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

data class VideoSession(
    val id: String,
    val title: String,
    val description: String,
    val agentName: String,
    val agentSpecialization: String,
    val scheduledTime: String,
    val duration: String,
    val status: VideoSessionStatus,
    val meetingLink: String,
    val category: String,
    val priority: SessionPriority
)

data class AvailableTimeSlot(
    val id: String,
    val date: String,
    val time: String,
    val agentName: String,
    val agentId: String,
    val isAvailable: Boolean
)

data class VideoAgent(
    val id: String,
    val name: String,
    val title: String,
    val specialization: String,
    val rating: Float,
    val totalSessions: Int,
    val languages: List<String>,
    val availability: String,
    val avatar: String
)

data class VideoCallFeature(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isAvailable: Boolean
)

data class SessionFeedback(
    val sessionId: String,
    val rating: Int,
    val comment: String,
    val agentRating: Int,
    val wouldRecommend: Boolean
)

enum class VideoSessionStatus {
    SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, RESCHEDULED
}

enum class SessionPriority {
    LOW, NORMAL, HIGH, URGENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSupportScreen(
    onBackClick: () -> Unit,
    onScheduleSessionClick: () -> Unit,
    onJoinSessionClick: (String) -> Unit,
    onRescheduleClick: (String) -> Unit,
    onCancelSessionClick: (String) -> Unit
) {
    val upcomingSessions = remember { getDummyUpcomingSessions() }
    val availableAgents = remember { getDummyVideoAgents() }
    val availableSlots = remember { getDummyAvailableSlots() }
    val callFeatures = remember { getDummyCallFeatures() }
    val pastSessions = remember { getDummyPastSessions() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Schedule", "Upcoming", "Past Sessions", "Agents")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Support") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Video settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.Default.Help, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = onScheduleSessionClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = "Schedule Session")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Video Support Status
            VideoSupportStatusBanner()

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
                0 -> ScheduleSessionContent(
                    availableSlots = availableSlots,
                    callFeatures = callFeatures,
                    onScheduleClick = onScheduleSessionClick
                )
                1 -> UpcomingSessionsContent(
                    sessions = upcomingSessions,
                    onJoinClick = onJoinSessionClick,
                    onRescheduleClick = onRescheduleClick,
                    onCancelClick = onCancelSessionClick
                )
                2 -> PastSessionsContent(
                    sessions = pastSessions,
                    onSessionClick = { /* View session details */ }
                )
                3 -> VideoAgentsContent(
                    agents = availableAgents,
                    onAgentClick = { /* View agent profile */ }
                )
            }
        }
    }
}

@Composable
fun ScheduleSessionContent(
    availableSlots: List<AvailableTimeSlot>,
    callFeatures: List<VideoCallFeature>,
    onScheduleClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        item {
            VideoSupportWelcomeCard()
        }

        // Session Types
        item {
            SessionTypesSection(
                onGeneralSupportClick = onScheduleClick,
                onTechnicalSupportClick = onScheduleClick,
                onAccountReviewClick = onScheduleClick,
                onFinancialAdviceClick = onScheduleClick
            )
        }

        // Available Time Slots
        item {
            Text(
                text = "Available Time Slots",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Group slots by date
        val slotsByDate = availableSlots.groupBy { it.date }
        slotsByDate.forEach { (date, slots) ->
            item {
                Text(
                    text = date,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(slots) { slot ->
                        TimeSlotCard(
                            slot = slot,
                            onClick = { if (slot.isAvailable) onScheduleClick() }
                        )
                    }
                }
            }
        }

        // Video Call Features
        item {
            Text(
                text = "Video Call Features",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(callFeatures) { feature ->
            VideoCallFeatureCard(feature = feature)
        }

        // Preparation Tips
        item {
            VideoCallPreparationCard()
        }
    }
}

@Composable
fun UpcomingSessionsContent(
    sessions: List<VideoSession>,
    onJoinClick: (String) -> Unit,
    onRescheduleClick: (String) -> Unit,
    onCancelClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Upcoming Video Sessions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (sessions.isNotEmpty()) {
            items(sessions) { session ->
                UpcomingSessionCard(
                    session = session,
                    onJoinClick = { onJoinClick(session.id) },
                    onRescheduleClick = { onRescheduleClick(session.id) },
                    onCancelClick = { onCancelClick(session.id) }
                )
            }
        } else {
            item {
                EmptyUpcomingSessionsState()
            }
        }

        // Quick Actions
        item {
            QuickVideoActions(
                onScheduleNewClick = { /* Schedule new session */ },
                onViewPastClick = { /* View past sessions */ },
                onContactSupportClick = { /* Contact support */ }
            )
        }
    }
}

@Composable
fun PastSessionsContent(
    sessions: List<VideoSession>,
    onSessionClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Past Video Sessions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (sessions.isNotEmpty()) {
            items(sessions) { session ->
                PastSessionCard(
                    session = session,
                    onClick = { onSessionClick(session.id) }
                )
            }
        } else {
            item {
                EmptyPastSessionsState()
            }
        }
    }
}

@Composable
fun VideoAgentsContent(
    agents: List<VideoAgent>,
    onAgentClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Video Support Agents",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Meet our expert support team available for video consultations",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(agents) { agent ->
            VideoAgentCard(
                agent = agent,
                onClick = { onAgentClick(agent.id) }
            )
        }
    }
}

@Composable
fun VideoSupportStatusBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Blue.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = Color.Blue,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "Video support is available",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Blue
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "HD Quality • Secure",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Blue
            )
        }
    }
}

@Composable
fun VideoSupportWelcomeCard() {
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
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Video Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Get face-to-face help from our expert support team",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun SessionTypesSection(
    onGeneralSupportClick: () -> Unit,
    onTechnicalSupportClick: () -> Unit,
    onAccountReviewClick: () -> Unit,
    onFinancialAdviceClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Session Types",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SessionTypeItem(
                    icon = Icons.Default.Support,
                    title = "General Support",
                    description = "Account questions, general banking help",
                    duration = "15-30 min",
                    onClick = onGeneralSupportClick
                )
                
                SessionTypeItem(
                    icon = Icons.Default.Build,
                    title = "Technical Support",
                    description = "App issues, online banking problems",
                    duration = "20-45 min",
                    onClick = onTechnicalSupportClick
                )
                
                SessionTypeItem(
                    icon = Icons.Default.AccountBalance,
                    title = "Account Review",
                    description = "Account setup, product recommendations",
                    duration = "30-60 min",
                    onClick = onAccountReviewClick
                )
                
                SessionTypeItem(
                    icon = Icons.Default.TrendingUp,
                    title = "Financial Advice",
                    description = "Investment guidance, financial planning",
                    duration = "45-90 min",
                    onClick = onFinancialAdviceClick
                )
            }
        }
    }
}

@Composable
fun SessionTypeItem(
    icon: ImageVector,
    title: String,
    description: String,
    duration: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TimeSlotCard(
    slot: AvailableTimeSlot,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable(enabled = slot.isAvailable) { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isAvailable) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = slot.time,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (slot.isAvailable) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
            )
            
            Text(
                text = slot.agentName,
                style = MaterialTheme.typography.bodySmall,
                color = if (slot.isAvailable) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                }
            )
            
            if (!slot.isAvailable) {
                Text(
                    text = "Booked",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun VideoCallFeatureCard(
    feature: VideoCallFeature
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (feature.isAvailable) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = null,
                tint = if (feature.isAvailable) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (feature.isAvailable) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    }
                )
                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (feature.isAvailable) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun UpcomingSessionCard(
    session: VideoSession,
    onJoinClick: () -> Unit,
    onRescheduleClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (session.status) {
                VideoSessionStatus.SCHEDULED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                VideoSessionStatus.IN_PROGRESS -> Color.Green.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = session.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                VideoSessionStatusChip(status = session.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Agent: ${session.agentName}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = session.agentSpecialization,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = session.scheduledTime,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Duration: ${session.duration}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (session.status == VideoSessionStatus.SCHEDULED) {
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join")
                    }
                    
                    OutlinedButton(
                        onClick = onRescheduleClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reschedule")
                    }
                    
                    OutlinedButton(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun PastSessionCard(
    session: VideoSession,
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
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Agent: ${session.agentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${session.scheduledTime} • ${session.duration}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                VideoSessionStatusChip(status = session.status)
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VideoAgentCard(
    agent: VideoAgent,
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
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = agent.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = agent.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = agent.specialization,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFF9800),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${agent.rating} (${agent.totalSessions} sessions)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "Languages: ${agent.languages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = agent.availability,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Green
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VideoCallPreparationCard() {
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
                text = "Preparation Tips",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val tips = listOf(
                "Ensure stable internet connection",
                "Test your camera and microphone",
                "Have your account details ready",
                "Find a quiet, well-lit space",
                "Prepare your questions in advance"
            )
            
            tips.forEach { tip ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
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
fun QuickVideoActions(
    onScheduleNewClick: () -> Unit,
    onViewPastClick: () -> Unit,
    onContactSupportClick: () -> Unit
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
                QuickActionItem(
                    icon = Icons.Default.Add,
                    label = "Schedule New",
                    onClick = onScheduleNewClick
                )
                QuickActionItem(
                    icon = Icons.Default.History,
                    label = "View Past",
                    onClick = onViewPastClick
                )
                QuickActionItem(
                    icon = Icons.Default.Support,
                    label = "Contact Support",
                    onClick = onContactSupportClick
                )
            }
        }
    }
}

@Composable
fun QuickActionItem(
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
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
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
fun VideoSessionStatusChip(status: VideoSessionStatus) {
    val (color, text) = when (status) {
        VideoSessionStatus.SCHEDULED -> Color.Blue to "Scheduled"
        VideoSessionStatus.IN_PROGRESS -> Color.Green to "In Progress"
        VideoSessionStatus.COMPLETED -> Color.Gray to "Completed"
        VideoSessionStatus.CANCELLED -> Color.Red to "Cancelled"
        VideoSessionStatus.RESCHEDULED -> Color(0xFFFF9800) to "Rescheduled"
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
fun EmptyUpcomingSessionsState() {
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
                imageVector = Icons.Default.VideoCall,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Upcoming Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Schedule a video call with our support team to get personalized help.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyPastSessionsState() {
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
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Past Sessions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your completed video support sessions will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummyUpcomingSessions(): List<VideoSession> {
    return listOf(
        VideoSession(
            id = "VS001",
            title = "Account Setup Consultation",
            description = "Help with setting up new account features",
            agentName = "Sarah Johnson",
            agentSpecialization = "Account Specialist",
            scheduledTime = "Today, 2:00 PM",
            duration = "30 min",
            status = VideoSessionStatus.SCHEDULED,
            meetingLink = "https://meet.monzo.com/vs001",
            category = "Account",
            priority = SessionPriority.NORMAL
        ),
        VideoSession(
            id = "VS002",
            title = "Investment Planning Session",
            description = "Discuss investment options and portfolio",
            agentName = "Mike Chen",
            agentSpecialization = "Investment Advisor",
            scheduledTime = "Tomorrow, 10:00 AM",
            duration = "60 min",
            status = VideoSessionStatus.SCHEDULED,
            meetingLink = "https://meet.monzo.com/vs002",
            category = "Investments",
            priority = SessionPriority.HIGH
        )
    )
}

fun getDummyPastSessions(): List<VideoSession> {
    return listOf(
        VideoSession(
            id = "VS003",
            title = "Technical Support Session",
            description = "Resolved app login issues",
            agentName = "Emma Wilson",
            agentSpecialization = "Technical Support",
            scheduledTime = "Last week",
            duration = "25 min",
            status = VideoSessionStatus.COMPLETED,
            meetingLink = "",
            category = "Technical",
            priority = SessionPriority.NORMAL
        )
    )
}

fun getDummyVideoAgents(): List<VideoAgent> {
    return listOf(
        VideoAgent(
            id = "agent1",
            name = "Sarah Johnson",
            title = "Senior Support Specialist",
            specialization = "Account Management & General Support",
            rating = 4.9f,
            totalSessions = 245,
            languages = listOf("English", "Spanish"),
            availability = "Available now",
            avatar = ""
        ),
        VideoAgent(
            id = "agent2",
            name = "Mike Chen",
            title = "Investment Advisor",
            specialization = "Investment Planning & Financial Advice",
            rating = 4.8f,
            totalSessions = 189,
            languages = listOf("English", "Mandarin"),
            availability = "Available in 15 min",
            avatar = ""
        ),
        VideoAgent(
            id = "agent3",
            name = "Emma Wilson",
            title = "Technical Support Lead",
            specialization = "Technical Issues & App Support",
            rating = 4.9f,
            totalSessions = 312,
            languages = listOf("English", "French"),
            availability = "Available tomorrow",
            avatar = ""
        )
    )
}

fun getDummyAvailableSlots(): List<AvailableTimeSlot> {
    return listOf(
        AvailableTimeSlot("slot1", "Today", "2:00 PM", "Sarah Johnson", "agent1", true),
        AvailableTimeSlot("slot2", "Today", "3:30 PM", "Mike Chen", "agent2", false),
        AvailableTimeSlot("slot3", "Today", "4:00 PM", "Emma Wilson", "agent3", true),
        AvailableTimeSlot("slot4", "Tomorrow", "9:00 AM", "Sarah Johnson", "agent1", true),
        AvailableTimeSlot("slot5", "Tomorrow", "10:30 AM", "Mike Chen", "agent2", true),
        AvailableTimeSlot("slot6", "Tomorrow", "2:00 PM", "Emma Wilson", "agent3", false)
    )
}

fun getDummyCallFeatures(): List<VideoCallFeature> {
    return listOf(
        VideoCallFeature(
            id = "hd_video",
            name = "HD Video Quality",
            description = "Crystal clear 1080p video calls",
            icon = Icons.Default.Videocam,
            isAvailable = true
        ),
        VideoCallFeature(
            id = "screen_share",
            name = "Screen Sharing",
            description = "Share your screen for better support",
            icon = Icons.Default.ScreenShare,
            isAvailable = true
        ),
        VideoCallFeature(
            id = "recording",
            name = "Session Recording",
            description = "Record sessions for your reference",
            icon = Icons.Default.VideoLibrary,
            isAvailable = true
        ),
        VideoCallFeature(
            id = "chat",
            name = "In-Call Chat",
            description = "Text chat during video calls",
            icon = Icons.Default.Chat,
            isAvailable = true
        ),
        VideoCallFeature(
            id = "file_share",
            name = "File Sharing",
            description = "Share documents during the call",
            icon = Icons.Default.AttachFile,
            isAvailable = false
        )
    )
}
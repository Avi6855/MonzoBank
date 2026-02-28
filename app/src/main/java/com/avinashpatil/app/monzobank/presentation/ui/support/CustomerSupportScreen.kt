package com.avinashpatil.app.monzobank.presentation.ui.support

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class ChatMessage(
    val id: String,
    val content: String,
    val timestamp: String,
    val isFromUser: Boolean,
    val messageType: MessageType = MessageType.TEXT,
    val attachments: List<String> = emptyList()
)

data class SupportAgent(
    val id: String,
    val name: String,
    val avatar: String,
    val status: AgentStatus,
    val specialization: String,
    val rating: Float,
    val responseTime: String
)

data class SupportSession(
    val id: String,
    val agentId: String?,
    val startTime: String,
    val status: SessionStatus,
    val category: String,
    val priority: Priority,
    val messages: List<ChatMessage>
)

data class QuickReply(
    val id: String,
    val text: String,
    val category: String
)

data class SupportTopic(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val estimatedTime: String
)

enum class MessageType {
    TEXT, IMAGE, FILE, SYSTEM
}

enum class AgentStatus {
    ONLINE, BUSY, OFFLINE
}

enum class SessionStatus {
    WAITING, ACTIVE, ENDED, TRANSFERRED
}

enum class Priority {
    LOW, NORMAL, HIGH, URGENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSupportScreen(
    onBackClick: () -> Unit,
    onStartChatClick: (String) -> Unit,
    onCallSupportClick: () -> Unit,
    onEmailSupportClick: () -> Unit
) {
    val supportTopics = remember { getDummySupportTopics() }
    val availableAgents = remember { getDummyAvailableAgents() }
    val quickReplies = remember { getDummyQuickReplies() }
    val activeSessions = remember { getDummyActiveSessions() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Start Chat", "Active Chats", "Call Support", "Email")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Support") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Support history */ }) {
                        Icon(Icons.Default.History, contentDescription = "History")
                    }
                    IconButton(onClick = { /* Support settings */ }) {
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
            // Support Status Banner
            SupportStatusBanner()

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
                0 -> StartChatContent(
                    topics = supportTopics,
                    agents = availableAgents,
                    quickReplies = quickReplies,
                    onTopicClick = onStartChatClick,
                    onAgentClick = { agentId -> onStartChatClick(agentId) }
                )
                1 -> ActiveChatsContent(
                    sessions = activeSessions,
                    onSessionClick = { /* Open chat */ }
                )
                2 -> CallSupportContent(
                    onCallClick = onCallSupportClick
                )
                3 -> EmailSupportContent(
                    onEmailClick = onEmailSupportClick
                )
            }
        }
    }
}

@Composable
fun StartChatContent(
    topics: List<SupportTopic>,
    agents: List<SupportAgent>,
    quickReplies: List<QuickReply>,
    onTopicClick: (String) -> Unit,
    onAgentClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        item {
            ChatWelcomeCard()
        }

        // Quick Start Options
        item {
            QuickStartSection(
                onGeneralChatClick = { onTopicClick("general") },
                onUrgentIssueClick = { onTopicClick("urgent") },
                onTechnicalSupportClick = { onTopicClick("technical") }
            )
        }

        // Support Topics
        item {
            Text(
                text = "What can we help you with?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        items(topics) { topic ->
            SupportTopicCard(
                topic = topic,
                onClick = { onTopicClick(topic.id) }
            )
        }

        // Available Agents
        if (agents.isNotEmpty()) {
            item {
                Text(
                    text = "Available Agents",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(agents) { agent ->
                        AgentCard(
                            agent = agent,
                            onClick = { onAgentClick(agent.id) }
                        )
                    }
                }
            }
        }

        // Quick Replies
        item {
            Text(
                text = "Quick Questions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            QuickRepliesSection(
                quickReplies = quickReplies,
                onQuickReplyClick = { reply -> onTopicClick(reply.id) }
            )
        }

        // Chat Guidelines
        item {
            ChatGuidelinesCard()
        }
    }
}

@Composable
fun ActiveChatsContent(
    sessions: List<SupportSession>,
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
                text = "Active Support Sessions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (sessions.isNotEmpty()) {
            items(sessions) { session ->
                SupportSessionCard(
                    session = session,
                    onClick = { onSessionClick(session.id) }
                )
            }
        } else {
            item {
                EmptyActiveChatsState()
            }
        }
    }
}

@Composable
fun CallSupportContent(
    onCallClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Phone Support",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Emergency Support
        item {
            EmergencyCallCard(
                onEmergencyCallClick = onCallClick
            )
        }

        // General Support Numbers
        item {
            GeneralSupportNumbers(
                onCallClick = onCallClick
            )
        }

        // International Support
        item {
            InternationalSupportCard()
        }

        // Call Guidelines
        item {
            CallGuidelinesCard()
        }
    }
}

@Composable
fun EmailSupportContent(
    onEmailClick: () -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var selectedPriority by remember { mutableStateOf(Priority.NORMAL) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Email Support",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Send us an email and we'll get back to you within 4 hours",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Email Form
        item {
            EmailSupportForm(
                subject = subject,
                message = message,
                selectedCategory = selectedCategory,
                selectedPriority = selectedPriority,
                onSubjectChange = { subject = it },
                onMessageChange = { message = it },
                onCategoryChange = { selectedCategory = it },
                onPriorityChange = { selectedPriority = it },
                onSendClick = onEmailClick
            )
        }

        // Email Templates
        item {
            EmailTemplatesSection(
                onTemplateClick = { template ->
                    subject = template.first
                    message = template.second
                }
            )
        }
    }
}

@Composable
fun SupportStatusBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Green.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "All support services are operational",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Green
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Response time: < 2 min",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Green
            )
        }
    }
}

@Composable
fun ChatWelcomeCard() {
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
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Need Help?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Our support team is ready to assist you 24/7",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun QuickStartSection(
    onGeneralChatClick: () -> Unit,
    onUrgentIssueClick: () -> Unit,
    onTechnicalSupportClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Quick Start",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStartButton(
                    icon = Icons.Default.Chat,
                    label = "General Chat",
                    color = Color.Blue,
                    onClick = onGeneralChatClick
                )
                QuickStartButton(
                    icon = Icons.Default.PriorityHigh,
                    label = "Urgent Issue",
                    color = Color.Red,
                    onClick = onUrgentIssueClick
                )
                QuickStartButton(
                    icon = Icons.Default.Build,
                    label = "Technical",
                    color = Color(0xFFFF9800),
                    onClick = onTechnicalSupportClick
                )
            }
        }
    }
}

@Composable
fun QuickStartButton(
    icon: ImageVector,
    label: String,
    color: Color,
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
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
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
fun SupportTopicCard(
    topic: SupportTopic,
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
                imageVector = topic.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = topic.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Est. time: ${topic.estimatedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AgentCard(
    agent: SupportAgent,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
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
                
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            when (agent.status) {
                                AgentStatus.ONLINE -> Color.Green
                                AgentStatus.BUSY -> Color(0xFFFF9800)
                                AgentStatus.OFFLINE -> Color.Gray
                            }
                        )
                        .align(Alignment.BottomEnd)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = agent.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
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
                    text = agent.rating.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "Response: ${agent.responseTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuickRepliesSection(
    quickReplies: List<QuickReply>,
    onQuickReplyClick: (QuickReply) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(quickReplies) { reply ->
            Surface(
                onClick = { onQuickReplyClick(reply) },
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = reply.text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SupportSessionCard(
    session: SupportSession,
    onClick: () -> Unit
) {
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
                        text = "Session #${session.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = session.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    SessionStatusChip(status = session.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    PriorityChip(priority = session.priority)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (session.messages.isNotEmpty()) {
                val lastMessage = session.messages.last()
                Text(
                    text = "Last message: ${lastMessage.content}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Started: ${session.startTime}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmergencyCallCard(
    onEmergencyCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Emergency,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Emergency Support",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text(
                    text = "For lost/stolen cards, fraud, or urgent account issues",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Available 24/7 • Free call",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
            
            Button(
                onClick = onEmergencyCallClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red
                )
            ) {
                Text("Call Now")
            }
        }
    }
}

@Composable
fun GeneralSupportNumbers(
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "General Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SupportNumberItem(
                title = "Customer Service",
                number = "0800 123 4567",
                availability = "Mon-Fri 8AM-8PM, Sat-Sun 9AM-5PM",
                onCallClick = onCallClick
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SupportNumberItem(
                title = "Technical Support",
                number = "0800 765 4321",
                availability = "24/7",
                onCallClick = onCallClick
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SupportNumberItem(
                title = "Business Banking",
                number = "0800 999 8888",
                availability = "Mon-Fri 8AM-6PM",
                onCallClick = onCallClick
            )
        }
    }
}

@Composable
fun SupportNumberItem(
    title: String,
    number: String,
    availability: String,
    onCallClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = number,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = availability,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onCallClick) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Call",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun InternationalSupportCard() {
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
                text = "International Support",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "If you're traveling abroad and need support, use our international numbers or contact us through the app.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "International: +44 20 1234 5678",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun EmailSupportForm(
    subject: String,
    message: String,
    selectedCategory: String,
    selectedPriority: Priority,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onSendClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Selection
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            val categories = listOf("General", "Account", "Payments", "Cards", "Technical", "Complaints")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        onClick = { onCategoryChange(category) },
                        label = { Text(category) },
                        selected = selectedCategory == category
                    )
                }
            }
            
            // Priority Selection
            Text(
                text = "Priority",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.values().forEach { priority ->
                    FilterChip(
                        onClick = { onPriorityChange(priority) },
                        label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        selected = selectedPriority == priority
                    )
                }
            }
            
            // Subject Field
            OutlinedTextField(
                value = subject,
                onValueChange = onSubjectChange,
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Message Field
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                label = { Text("Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Send Button
            Button(
                onClick = onSendClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = subject.isNotBlank() && message.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Email")
            }
        }
    }
}

@Composable
fun EmailTemplatesSection(
    onTemplateClick: (Pair<String, String>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Email Templates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val templates = listOf(
                "Account Access Issue" to "I'm having trouble accessing my account. Please help me resolve this issue.",
                "Transaction Dispute" to "I would like to dispute a transaction on my account. Transaction details: [Please provide details]",
                "Card Replacement" to "I need to request a replacement card. My current card is [lost/stolen/damaged].",
                "General Inquiry" to "I have a general question about my account. Please provide information about: [Your question]"
            )
            
            templates.forEach { template ->
                Surface(
                    onClick = { onTemplateClick(template) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
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
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = template.first,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
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
    }
}

@Composable
fun ChatGuidelinesCard() {
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
                text = "Chat Guidelines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val guidelines = listOf(
                "Be clear and specific about your issue",
                "Have your account details ready",
                "Our agents may ask for verification",
                "Chat sessions are recorded for quality purposes"
            )
            
            guidelines.forEach { guideline ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = guideline,
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
fun CallGuidelinesCard() {
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
                text = "Before You Call",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val guidelines = listOf(
                "Have your account number ready",
                "Prepare details about your issue",
                "Calls may be recorded for training",
                "Average wait time: 2-5 minutes"
            )
            
            guidelines.forEach { guideline ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = guideline,
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
fun SessionStatusChip(status: SessionStatus) {
    val (color, text) = when (status) {
        SessionStatus.WAITING -> Color(0xFFFF9800) to "Waiting"
        SessionStatus.ACTIVE -> Color.Green to "Active"
        SessionStatus.ENDED -> Color.Gray to "Ended"
        SessionStatus.TRANSFERRED -> Color.Blue to "Transferred"
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
fun PriorityChip(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.LOW -> Color.Green to "Low"
        Priority.NORMAL -> Color.Blue to "Normal"
        Priority.HIGH -> Color(0xFFFF9800) to "High"
        Priority.URGENT -> Color.Red to "Urgent"
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
fun EmptyActiveChatsState() {
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
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Chats",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You don't have any active support sessions. Start a new chat to get help.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummySupportTopics(): List<SupportTopic> {
    return listOf(
        SupportTopic(
            id = "account",
            title = "Account Issues",
            description = "Login problems, account settings, profile updates",
            icon = Icons.Default.AccountCircle,
            estimatedTime = "5-10 min"
        ),
        SupportTopic(
            id = "payments",
            title = "Payments & Transfers",
            description = "Transaction issues, failed payments, transfer problems",
            icon = Icons.Default.Payment,
            estimatedTime = "3-8 min"
        ),
        SupportTopic(
            id = "cards",
            title = "Card Support",
            description = "Lost/stolen cards, PIN issues, card activation",
            icon = Icons.Default.CreditCard,
            estimatedTime = "2-5 min"
        ),
        SupportTopic(
            id = "technical",
            title = "Technical Support",
            description = "App issues, connectivity problems, feature help",
            icon = Icons.Default.Build,
            estimatedTime = "10-15 min"
        )
    )
}

fun getDummyAvailableAgents(): List<SupportAgent> {
    return listOf(
        SupportAgent(
            id = "agent1",
            name = "Sarah Johnson",
            avatar = "",
            status = AgentStatus.ONLINE,
            specialization = "General Support",
            rating = 4.9f,
            responseTime = "< 1 min"
        ),
        SupportAgent(
            id = "agent2",
            name = "Mike Chen",
            avatar = "",
            status = AgentStatus.ONLINE,
            specialization = "Technical Issues",
            rating = 4.8f,
            responseTime = "< 2 min"
        ),
        SupportAgent(
            id = "agent3",
            name = "Emma Wilson",
            avatar = "",
            status = AgentStatus.BUSY,
            specialization = "Account Security",
            rating = 4.9f,
            responseTime = "< 5 min"
        )
    )
}

fun getDummyQuickReplies(): List<QuickReply> {
    return listOf(
        QuickReply("qr1", "Reset my password", "Account"),
        QuickReply("qr2", "Card not working", "Cards"),
        QuickReply("qr3", "Transaction missing", "Payments"),
        QuickReply("qr4", "App won't load", "Technical"),
        QuickReply("qr5", "Update my details", "Account")
    )
}

fun getDummyActiveSessions(): List<SupportSession> {
    return listOf(
        SupportSession(
            id = "SS001",
            agentId = "agent1",
            startTime = "2 hours ago",
            status = SessionStatus.ACTIVE,
            category = "Account Issues",
            priority = Priority.NORMAL,
            messages = listOf(
                ChatMessage(
                    id = "msg1",
                    content = "Hi, I need help with my account",
                    timestamp = "2 hours ago",
                    isFromUser = true
                ),
                ChatMessage(
                    id = "msg2",
                    content = "Hello! I'd be happy to help you with your account. What specific issue are you experiencing?",
                    timestamp = "2 hours ago",
                    isFromUser = false
                )
            )
        )
    )
}
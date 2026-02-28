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

data class SupportCategory(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val itemCount: Int
)

data class SupportArticle(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val readTime: String,
    val isPopular: Boolean,
    val tags: List<String>
)

data class SupportContact(
    val id: String,
    val type: ContactType,
    val title: String,
    val description: String,
    val availability: String,
    val responseTime: String,
    val icon: ImageVector
)

data class FAQ(
    val id: String,
    val question: String,
    val answer: String,
    val category: String,
    val isExpanded: Boolean = false
)

data class SupportTicket(
    val id: String,
    val title: String,
    val description: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    val createdDate: String,
    val lastUpdate: String,
    val category: String
)

enum class ContactType {
    CHAT, PHONE, EMAIL, VIDEO_CALL, BRANCH
}

enum class TicketStatus {
    OPEN, IN_PROGRESS, WAITING_FOR_RESPONSE, RESOLVED, CLOSED
}

enum class TicketPriority {
    LOW, MEDIUM, HIGH, URGENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onArticleClick: (String) -> Unit,
    onContactClick: (ContactType) -> Unit,
    onTicketClick: (String) -> Unit,
    onCreateTicketClick: () -> Unit
) {
    val supportCategories = remember { getDummySupportCategories() }
    val popularArticles = remember { getDummyPopularArticles() }
    val supportContacts = remember { getDummySupportContacts() }
    val faqs = remember { getDummyFAQs() }
    val recentTickets = remember { getDummyRecentTickets() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Help Center", "Contact Us", "My Tickets", "FAQ")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search support */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Support settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = onCreateTicketClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Ticket")
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
                0 -> HelpCenterContent(
                    categories = supportCategories,
                    popularArticles = popularArticles,
                    onCategoryClick = onCategoryClick,
                    onArticleClick = onArticleClick
                )
                1 -> ContactUsContent(
                    contacts = supportContacts,
                    onContactClick = onContactClick
                )
                2 -> MyTicketsContent(
                    tickets = recentTickets,
                    onTicketClick = onTicketClick
                )
                3 -> FAQContent(
                    faqs = faqs
                )
            }
        }
    }
}

@Composable
fun HelpCenterContent(
    categories: List<SupportCategory>,
    popularArticles: List<SupportArticle>,
    onCategoryClick: (String) -> Unit,
    onArticleClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        item {
            SupportWelcomeCard()
        }

        // Quick Actions
        item {
            QuickSupportActions(
                onChatClick = { /* Start chat */ },
                onCallClick = { /* Start call */ },
                onEmailClick = { /* Send email */ },
                onVideoClick = { /* Start video call */ }
            )
        }

        // Support Categories
        item {
            Text(
                text = "Browse by Category",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    SupportCategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category.id) }
                    )
                }
            }
        }

        // Popular Articles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Popular Articles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = { /* View all articles */ }) {
                    Text("View All")
                }
            }
        }

        items(popularArticles) { article ->
            SupportArticleCard(
                article = article,
                onClick = { onArticleClick(article.id) }
            )
        }

        // Support Statistics
        item {
            SupportStatisticsCard()
        }
    }
}

@Composable
fun ContactUsContent(
    contacts: List<SupportContact>,
    onContactClick: (ContactType) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Get in Touch",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Choose the best way to reach us. Our support team is here to help.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Emergency Contact
        item {
            EmergencyContactCard()
        }

        // Contact Options
        items(contacts) { contact ->
            SupportContactCard(
                contact = contact,
                onClick = { onContactClick(contact.type) }
            )
        }

        // Branch Locator
        item {
            BranchLocatorCard(
                onFindBranchClick = { /* Find branch */ }
            )
        }

        // Feedback Section
        item {
            FeedbackCard(
                onFeedbackClick = { /* Submit feedback */ }
            )
        }
    }
}

@Composable
fun MyTicketsContent(
    tickets: List<SupportTicket>,
    onTicketClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "My Support Tickets",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (tickets.isNotEmpty()) {
            // Filter by status
            val openTickets = tickets.filter { it.status in listOf(TicketStatus.OPEN, TicketStatus.IN_PROGRESS) }
            val closedTickets = tickets.filter { it.status in listOf(TicketStatus.RESOLVED, TicketStatus.CLOSED) }

            if (openTickets.isNotEmpty()) {
                item {
                    Text(
                        text = "Open Tickets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(openTickets) { ticket ->
                    SupportTicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) }
                    )
                }
            }

            if (closedTickets.isNotEmpty()) {
                item {
                    Text(
                        text = "Resolved Tickets",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(closedTickets) { ticket ->
                    SupportTicketCard(
                        ticket = ticket,
                        onClick = { onTicketClick(ticket.id) }
                    )
                }
            }
        } else {
            item {
                EmptyTicketsState()
            }
        }
    }
}

@Composable
fun FAQContent(
    faqs: List<FAQ>
) {
    var expandedFAQs by remember { mutableStateOf(setOf<String>()) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Frequently Asked Questions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Find quick answers to common questions",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Group FAQs by category
        val faqsByCategory = faqs.groupBy { it.category }
        faqsByCategory.forEach { (category, categoryFAQs) ->
            item {
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            
            items(categoryFAQs) { faq ->
                FAQCard(
                    faq = faq,
                    isExpanded = expandedFAQs.contains(faq.id),
                    onToggleExpanded = {
                        expandedFAQs = if (expandedFAQs.contains(faq.id)) {
                            expandedFAQs - faq.id
                        } else {
                            expandedFAQs + faq.id
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SupportWelcomeCard() {
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
                    text = "How can we help you?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Our support team is available 24/7 to assist you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun QuickSupportActions(
    onChatClick: () -> Unit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onVideoClick: () -> Unit
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
                QuickActionButton(
                    icon = Icons.Default.Chat,
                    label = "Live Chat",
                    onClick = onChatClick
                )
                QuickActionButton(
                    icon = Icons.Default.Phone,
                    label = "Call Us",
                    onClick = onCallClick
                )
                QuickActionButton(
                    icon = Icons.Default.Email,
                    label = "Email",
                    onClick = onEmailClick
                )
                QuickActionButton(
                    icon = Icons.Default.VideoCall,
                    label = "Video Call",
                    onClick = onVideoClick
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
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
fun SupportCategoryCard(
    category: SupportCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${category.itemCount} articles",
                style = MaterialTheme.typography.bodySmall,
                color = category.color
            )
        }
    }
}

@Composable
fun SupportArticleCard(
    article: SupportArticle,
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
                imageVector = Icons.Default.Article,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (article.isPopular) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Popular",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFFF9800),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "${article.readTime} • ${article.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun SupportContactCard(
    contact: SupportContact,
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
                Icon(
                    imageVector = contact.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = contact.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Available: ${contact.availability}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Response time: ${contact.responseTime}",
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
fun SupportTicketCard(
    ticket: SupportTicket,
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
                        text = ticket.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Ticket #${ticket.id}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    TicketStatusChip(status = ticket.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    TicketPriorityChip(priority = ticket.priority)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Created: ${ticket.createdDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Updated: ${ticket.lastUpdate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FAQCard(
    faq: FAQ,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpanded() }
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
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmergencyContactCard() {
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
                    text = "For urgent issues like lost/stolen cards or suspicious activity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Available 24/7 • Immediate response",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
            
            Button(
                onClick = { /* Emergency call */ },
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
fun BranchLocatorCard(
    onFindBranchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFindBranchClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Find a Branch or ATM",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Locate the nearest branch or ATM near you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun FeedbackCard(
    onFeedbackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFeedbackClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Feedback,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Share Your Feedback",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = "Help us improve our services",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun SupportStatisticsCard() {
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
                text = "Support Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    title = "Avg Response",
                    value = "< 2 min",
                    icon = Icons.Default.Speed
                )
                StatItem(
                    title = "Resolution Rate",
                    value = "98%",
                    icon = Icons.Default.CheckCircle
                )
                StatItem(
                    title = "Satisfaction",
                    value = "4.9/5",
                    icon = Icons.Default.Star
                )
            }
        }
    }
}

@Composable
fun StatItem(
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
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TicketStatusChip(status: TicketStatus) {
    val (color, text) = when (status) {
        TicketStatus.OPEN -> Color.Blue to "Open"
        TicketStatus.IN_PROGRESS -> Color(0xFFFF9800) to "In Progress"
        TicketStatus.WAITING_FOR_RESPONSE -> Color.Yellow to "Waiting"
        TicketStatus.RESOLVED -> Color.Green to "Resolved"
        TicketStatus.CLOSED -> Color.Gray to "Closed"
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
fun TicketPriorityChip(priority: TicketPriority) {
    val (color, text) = when (priority) {
        TicketPriority.LOW -> Color.Green to "Low"
        TicketPriority.MEDIUM -> Color(0xFFFF9800) to "Medium"
        TicketPriority.HIGH -> Color.Red to "High"
        TicketPriority.URGENT -> Color.Red to "Urgent"
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
fun EmptyTicketsState() {
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
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Support Tickets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You haven't created any support tickets yet. If you need help, feel free to create a new ticket.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getDummySupportCategories(): List<SupportCategory> {
    return listOf(
        SupportCategory(
            id = "account",
            title = "Account",
            description = "Account management",
            icon = Icons.Default.AccountCircle,
            color = Color.Blue,
            itemCount = 15
        ),
        SupportCategory(
            id = "payments",
            title = "Payments",
            description = "Transfers & payments",
            icon = Icons.Default.Payment,
            color = Color.Green,
            itemCount = 22
        ),
        SupportCategory(
            id = "cards",
            title = "Cards",
            description = "Card issues",
            icon = Icons.Default.CreditCard,
            color = Color(0xFFFF9800),
            itemCount = 18
        ),
        SupportCategory(
            id = "security",
            title = "Security",
            description = "Security & fraud",
            icon = Icons.Default.Security,
            color = Color.Red,
            itemCount = 12
        )
    )
}

fun getDummyPopularArticles(): List<SupportArticle> {
    return listOf(
        SupportArticle(
            id = "art1",
            title = "How to reset your PIN",
            summary = "Step-by-step guide to reset your card PIN",
            category = "Cards",
            readTime = "2 min read",
            isPopular = true,
            tags = listOf("PIN", "Cards", "Security")
        ),
        SupportArticle(
            id = "art2",
            title = "Setting up direct debits",
            summary = "Learn how to set up and manage direct debits",
            category = "Payments",
            readTime = "3 min read",
            isPopular = true,
            tags = listOf("Direct Debit", "Payments")
        ),
        SupportArticle(
            id = "art3",
            title = "Understanding your statement",
            summary = "How to read and understand your bank statement",
            category = "Account",
            readTime = "4 min read",
            isPopular = false,
            tags = listOf("Statement", "Account")
        )
    )
}

fun getDummySupportContacts(): List<SupportContact> {
    return listOf(
        SupportContact(
            id = "chat",
            type = ContactType.CHAT,
            title = "Live Chat",
            description = "Chat with our support team in real-time",
            availability = "24/7",
            responseTime = "< 2 minutes",
            icon = Icons.Default.Chat
        ),
        SupportContact(
            id = "phone",
            type = ContactType.PHONE,
            title = "Phone Support",
            description = "Speak directly with a support agent",
            availability = "Mon-Fri 8AM-8PM",
            responseTime = "Immediate",
            icon = Icons.Default.Phone
        ),
        SupportContact(
            id = "email",
            type = ContactType.EMAIL,
            title = "Email Support",
            description = "Send us an email with your query",
            availability = "24/7",
            responseTime = "< 4 hours",
            icon = Icons.Default.Email
        ),
        SupportContact(
            id = "video",
            type = ContactType.VIDEO_CALL,
            title = "Video Call",
            description = "Schedule a video call with our experts",
            availability = "Mon-Fri 9AM-5PM",
            responseTime = "Same day",
            icon = Icons.Default.VideoCall
        )
    )
}

fun getDummyFAQs(): List<FAQ> {
    return listOf(
        FAQ(
            id = "faq1",
            question = "How do I reset my password?",
            answer = "You can reset your password by clicking 'Forgot Password' on the login screen and following the instructions sent to your email.",
            category = "Account"
        ),
        FAQ(
            id = "faq2",
            question = "What should I do if my card is lost or stolen?",
            answer = "Immediately contact our emergency support line at any time to block your card. You can also freeze your card instantly through the app.",
            category = "Cards"
        ),
        FAQ(
            id = "faq3",
            question = "How long do transfers take?",
            answer = "Internal transfers are instant. External transfers typically take 1-2 business days depending on the receiving bank.",
            category = "Payments"
        ),
        FAQ(
            id = "faq4",
            question = "Is my money safe with Monzo?",
            answer = "Yes, your money is protected by the Financial Services Compensation Scheme (FSCS) up to £85,000. We also use advanced security measures to protect your account.",
            category = "Security"
        )
    )
}

fun getDummyRecentTickets(): List<SupportTicket> {
    return listOf(
        SupportTicket(
            id = "TKT001",
            title = "Unable to make international transfer",
            description = "Getting error when trying to send money abroad",
            status = TicketStatus.IN_PROGRESS,
            priority = TicketPriority.HIGH,
            createdDate = "2 days ago",
            lastUpdate = "1 hour ago",
            category = "Payments"
        ),
        SupportTicket(
            id = "TKT002",
            title = "Card declined at ATM",
            description = "My card was declined at multiple ATMs despite having sufficient balance",
            status = TicketStatus.RESOLVED,
            priority = TicketPriority.MEDIUM,
            createdDate = "1 week ago",
            lastUpdate = "3 days ago",
            category = "Cards"
        )
    )
}
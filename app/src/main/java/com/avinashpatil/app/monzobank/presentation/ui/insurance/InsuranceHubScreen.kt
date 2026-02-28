package com.avinashpatil.app.monzobank.presentation.ui.insurance

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

data class InsuranceCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val description: String,
    val productCount: Int,
    val color: Color
)

data class InsuranceArticle(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val readTime: String,
    val imageUrl: String? = null
)

data class InsuranceTip(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceHubScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onArticleClick: (String) -> Unit,
    onCalculatorClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    val categories = remember { getDummyInsuranceCategories() }
    val articles = remember { getDummyInsuranceArticles() }
    val tips = remember { getDummyInsuranceTips() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance Hub") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            item {
                WelcomeSection()
            }

            // Quick Actions
            item {
                QuickActionsSection(
                    onCalculatorClick = onCalculatorClick,
                    onCompareClick = onCompareClick
                )
            }

            // Insurance Categories
            item {
                InsuranceCategoriesSection(
                    categories = categories,
                    onCategoryClick = onCategoryClick
                )
            }

            // Insurance Tips
            item {
                InsuranceTipsSection(tips = tips)
            }

            // Educational Articles
            item {
                EducationalArticlesSection(
                    articles = articles,
                    onArticleClick = onArticleClick
                )
            }

            // Contact Support
            item {
                ContactSupportSection()
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Welcome to Insurance Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Protect what matters most to you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Discover comprehensive insurance solutions tailored to your needs. From life and health to auto and home insurance, we've got you covered.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun QuickActionsSection(
    onCalculatorClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                title = "Premium Calculator",
                description = "Calculate your insurance premiums",
                icon = Icons.Default.Calculate,
                onClick = onCalculatorClick,
                modifier = Modifier.weight(1f)
            )
            
            QuickActionCard(
                title = "Compare Plans",
                description = "Compare different insurance plans",
                icon = Icons.Default.Compare,
                onClick = onCompareClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    description: String,
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
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun InsuranceCategoriesSection(
    categories: List<InsuranceCategory>,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Insurance Categories",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* View all */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(categories) { category ->
                InsuranceCategoryCard(
                    category = category,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

@Composable
fun InsuranceCategoryCard(
    category: InsuranceCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = category.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(category.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = category.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${category.productCount} products",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InsuranceTipsSection(tips: List<InsuranceTip>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Insurance Tips",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        tips.take(3).forEach { tip ->
            InsuranceTipCard(tip = tip)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InsuranceTipCard(tip: InsuranceTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tip.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EducationalArticlesSection(
    articles: List<InsuranceArticle>,
    onArticleClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Educational Articles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = { /* View all */ }) {
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        articles.take(3).forEach { article ->
            InsuranceArticleCard(
                article = article,
                onClick = { onArticleClick(article.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InsuranceArticleCard(
    article: InsuranceArticle,
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
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = article.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                Text(
                    text = article.readTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ContactSupportSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Need Help?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Our insurance experts are here to help you find the right coverage for your needs.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { /* Chat support */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat")
                }
                
                OutlinedButton(
                    onClick = { /* Call support */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Call")
                }
            }
        }
    }
}

fun getDummyInsuranceCategories(): List<InsuranceCategory> {
    return listOf(
        InsuranceCategory(
            id = "life",
            name = "Life Insurance",
            icon = Icons.Default.Favorite,
            description = "Protect your family's financial future",
            productCount = 8,
            color = Color(0xFFE91E63)
        ),
        InsuranceCategory(
            id = "health",
            name = "Health Insurance",
            icon = Icons.Default.LocalHospital,
            description = "Comprehensive health coverage",
            productCount = 12,
            color = Color(0xFF4CAF50)
        ),
        InsuranceCategory(
            id = "auto",
            name = "Auto Insurance",
            icon = Icons.Default.DirectionsCar,
            description = "Protect your vehicle and yourself",
            productCount = 6,
            color = Color(0xFF2196F3)
        ),
        InsuranceCategory(
            id = "home",
            name = "Home Insurance",
            icon = Icons.Default.Home,
            description = "Secure your home and belongings",
            productCount = 10,
            color = Color(0xFFFF9800)
        ),
        InsuranceCategory(
            id = "travel",
            name = "Travel Insurance",
            icon = Icons.Default.Flight,
            description = "Travel with peace of mind",
            productCount = 5,
            color = Color(0xFF9C27B0)
        ),
        InsuranceCategory(
            id = "disability",
            name = "Disability Insurance",
            icon = Icons.Default.Accessible,
            description = "Income protection coverage",
            productCount = 4,
            color = Color(0xFF607D8B)
        )
    )
}

fun getDummyInsuranceArticles(): List<InsuranceArticle> {
    return listOf(
        InsuranceArticle(
            id = "art1",
            title = "Understanding Life Insurance: Term vs Whole Life",
            summary = "Learn the key differences between term and whole life insurance to make an informed decision for your family's future.",
            category = "Life Insurance",
            readTime = "5 min read"
        ),
        InsuranceArticle(
            id = "art2",
            title = "Health Insurance Basics: What You Need to Know",
            summary = "A comprehensive guide to understanding health insurance coverage, deductibles, and how to choose the right plan.",
            category = "Health Insurance",
            readTime = "7 min read"
        ),
        InsuranceArticle(
            id = "art3",
            title = "Auto Insurance: How to Lower Your Premiums",
            summary = "Discover practical tips and strategies to reduce your auto insurance costs without compromising on coverage.",
            category = "Auto Insurance",
            readTime = "4 min read"
        ),
        InsuranceArticle(
            id = "art4",
            title = "Home Insurance Claims: A Step-by-Step Guide",
            summary = "Learn how to file a home insurance claim effectively and what to expect during the claims process.",
            category = "Home Insurance",
            readTime = "6 min read"
        )
    )
}

fun getDummyInsuranceTips(): List<InsuranceTip> {
    return listOf(
        InsuranceTip(
            id = "tip1",
            title = "Review Your Coverage Annually",
            description = "Life changes, and so should your insurance coverage. Review your policies yearly to ensure adequate protection.",
            icon = Icons.Default.Schedule
        ),
        InsuranceTip(
            id = "tip2",
            title = "Bundle Your Policies",
            description = "Save money by bundling multiple insurance policies with the same provider for potential discounts.",
            icon = Icons.Default.Savings
        ),
        InsuranceTip(
            id = "tip3",
            title = "Maintain Good Credit",
            description = "A good credit score can help you get better rates on many types of insurance policies.",
            icon = Icons.Default.CreditScore
        ),
        InsuranceTip(
            id = "tip4",
            title = "Document Your Belongings",
            description = "Keep an inventory of your valuable items with photos and receipts for easier claims processing.",
            icon = Icons.Default.Inventory
        ),
        InsuranceTip(
            id = "tip5",
            title = "Understand Your Deductibles",
            description = "Higher deductibles typically mean lower premiums, but make sure you can afford the out-of-pocket costs.",
            icon = Icons.Default.Info
        )
    )
}
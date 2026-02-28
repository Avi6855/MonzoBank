package com.avinashpatil.app.monzobank.presentation.ui.loans

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

data class CreditScore(
    val score: Int,
    val rating: CreditRating,
    val lastUpdated: String,
    val factors: List<CreditFactor>
)

data class CreditFactor(
    val name: String,
    val impact: CreditImpact,
    val description: String,
    val suggestion: String
)

enum class CreditRating {
    POOR, FAIR, GOOD, VERY_GOOD, EXCELLENT
}

enum class CreditImpact {
    POSITIVE, NEGATIVE, NEUTRAL
}

data class CreditHistoryItem(
    val date: String,
    val score: Int,
    val change: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditScoreScreen(
    onBackClick: () -> Unit,
    onImproveTipsClick: () -> Unit
) {
    val creditScore = remember { getDummyCreditScore() }
    val creditHistory = remember { getDummyCreditHistory() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Factors", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Credit Score") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Refresh credit score */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
                0 -> CreditScoreOverviewContent(
                    creditScore = creditScore,
                    onImproveTipsClick = onImproveTipsClick
                )
                1 -> CreditFactorsContent(factors = creditScore.factors)
                2 -> CreditHistoryContent(history = creditHistory)
            }
        }
    }
}

@Composable
fun CreditScoreOverviewContent(
    creditScore: CreditScore,
    onImproveTipsClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Credit Score Gauge
        item {
            CreditScoreGauge(
                score = creditScore.score,
                rating = creditScore.rating,
                lastUpdated = creditScore.lastUpdated
            )
        }

        // Score Breakdown
        item {
            CreditScoreBreakdown(rating = creditScore.rating)
        }

        // Quick Actions
        item {
            CreditScoreActions(onImproveTipsClick = onImproveTipsClick)
        }

        // Key Factors Preview
        item {
            Text(
                text = "Key Factors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(creditScore.factors.take(3)) { factor ->
            CreditFactorCard(factor = factor)
        }
    }
}

@Composable
fun CreditFactorsContent(factors: List<CreditFactor>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Credit Factors",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "These factors affect your credit score",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(factors) { factor ->
            CreditFactorCard(factor = factor, showDetails = true)
        }
    }
}

@Composable
fun CreditHistoryContent(history: List<CreditHistoryItem>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Credit History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(history) { item ->
            CreditHistoryCard(item = item)
        }
    }
}

@Composable
fun CreditScoreGauge(
    score: Int,
    rating: CreditRating,
    lastUpdated: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CreditScoreCircularGauge(
                    score = score,
                    maxScore = 850
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = score.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = getCreditRatingText(rating),
                        style = MaterialTheme.typography.titleMedium,
                        color = getCreditRatingColor(rating)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Last updated: $lastUpdated",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CreditScoreCircularGauge(
    score: Int,
    maxScore: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = score.toFloat() / maxScore,
        animationSpec = tween(durationMillis = 1000)
    )
    
    Canvas(
        modifier = Modifier.size(180.dp)
    ) {
        val strokeWidth = 20.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)
        
        // Background arc
        drawArc(
            color = Color.Gray.copy(alpha = 0.3f),
            startAngle = 135f,
            sweepAngle = 270f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Progress arc
        val sweepAngle = 270f * animatedProgress
        val progressColor = when {
            score < 580 -> Color.Red
            score < 670 -> Color(0xFFFF9800)
            score < 740 -> Color.Yellow
            score < 800 -> Color.Green
            else -> Color(0xFF4CAF50)
        }
        
        drawArc(
            color = progressColor,
            startAngle = 135f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun CreditScoreBreakdown(rating: CreditRating) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Credit Score Ranges",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            val ranges = listOf(
                "Poor" to "300-579" to Color.Red,
                "Fair" to "580-669" to Color(0xFFFF9800),
                "Good" to "670-739" to Color.Yellow,
                "Very Good" to "740-799" to Color.Green,
                "Excellent" to "800-850" to Color(0xFF4CAF50)
            )
            
            ranges.forEach { (labelRange, color) ->
                val (label, range) = labelRange
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (getCreditRatingText(rating) == label) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Text(
                        text = range,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun CreditScoreActions(onImproveTipsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Improve Score",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onImproveTipsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Tips")
                }
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Monitor Credit",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Monitor credit */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Setup Alerts")
                }
            }
        }
    }
}

@Composable
fun CreditFactorCard(
    factor: CreditFactor,
    showDetails: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
                    text = factor.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                CreditImpactChip(impact = factor.impact)
            }
            
            if (showDetails) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = factor.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (factor.suggestion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = factor.suggestion,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreditHistoryCard(item: CreditHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = item.date,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Score: ${item.score}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (item.change != 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.change > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (item.change > 0) Color.Green else Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (item.change > 0) "+" else ""}${item.change}",
                        style = MaterialTheme.typography.titleSmall,
                        color = if (item.change > 0) Color.Green else Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CreditImpactChip(impact: CreditImpact) {
    val (color, text, icon) = when (impact) {
        CreditImpact.POSITIVE -> Triple(Color.Green, "Positive", Icons.Default.TrendingUp)
        CreditImpact.NEGATIVE -> Triple(Color.Red, "Negative", Icons.Default.TrendingDown)
        CreditImpact.NEUTRAL -> Triple(Color.Gray, "Neutral", Icons.Default.Remove)
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = color
            )
        }
    }
}

fun getCreditRatingText(rating: CreditRating): String {
    return when (rating) {
        CreditRating.POOR -> "Poor"
        CreditRating.FAIR -> "Fair"
        CreditRating.GOOD -> "Good"
        CreditRating.VERY_GOOD -> "Very Good"
        CreditRating.EXCELLENT -> "Excellent"
    }
}

fun getCreditRatingColor(rating: CreditRating): Color {
    return when (rating) {
        CreditRating.POOR -> Color.Red
        CreditRating.FAIR -> Color(0xFFFF9800)
        CreditRating.GOOD -> Color.Yellow
        CreditRating.VERY_GOOD -> Color.Green
        CreditRating.EXCELLENT -> Color(0xFF4CAF50)
    }
}

fun getDummyCreditScore(): CreditScore {
    return CreditScore(
        score = 742,
        rating = CreditRating.VERY_GOOD,
        lastUpdated = "September 15, 2024",
        factors = listOf(
            CreditFactor(
                name = "Payment History",
                impact = CreditImpact.POSITIVE,
                description = "You have a strong history of making payments on time",
                suggestion = "Continue making all payments on time to maintain your excellent payment history"
            ),
            CreditFactor(
                name = "Credit Utilization",
                impact = CreditImpact.POSITIVE,
                description = "Your credit utilization is 15%, which is below the recommended 30%",
                suggestion = "Keep your credit utilization below 30% for optimal credit health"
            ),
            CreditFactor(
                name = "Length of Credit History",
                impact = CreditImpact.POSITIVE,
                description = "You have a good credit history length of 8 years",
                suggestion = "Keep older accounts open to maintain a longer credit history"
            ),
            CreditFactor(
                name = "Credit Mix",
                impact = CreditImpact.NEUTRAL,
                description = "You have a moderate mix of credit types",
                suggestion = "Consider diversifying your credit types with different loan products"
            ),
            CreditFactor(
                name = "New Credit Inquiries",
                impact = CreditImpact.NEGATIVE,
                description = "You have 3 hard inquiries in the past 12 months",
                suggestion = "Limit new credit applications to avoid multiple hard inquiries"
            )
        )
    )
}

fun getDummyCreditHistory(): List<CreditHistoryItem> {
    return listOf(
        CreditHistoryItem("September 2024", 742, 5),
        CreditHistoryItem("August 2024", 737, -3),
        CreditHistoryItem("July 2024", 740, 8),
        CreditHistoryItem("June 2024", 732, 2),
        CreditHistoryItem("May 2024", 730, 0),
        CreditHistoryItem("April 2024", 730, -5),
        CreditHistoryItem("March 2024", 735, 7),
        CreditHistoryItem("February 2024", 728, 3)
    )
}
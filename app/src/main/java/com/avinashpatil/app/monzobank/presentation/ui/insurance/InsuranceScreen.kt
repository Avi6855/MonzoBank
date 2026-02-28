package com.avinashpatil.app.monzobank.presentation.ui.insurance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class InsurancePolicy(
    val id: String,
    val type: InsuranceType,
    val provider: String,
    val policyNumber: String,
    val premium: Double,
    val coverage: Double,
    val status: PolicyStatus,
    val renewalDate: String,
    val description: String
)

data class InsuranceProduct(
    val id: String,
    val name: String,
    val type: InsuranceType,
    val provider: String,
    val monthlyPremium: Double,
    val coverage: Double,
    val features: List<String>,
    val rating: Float
)

data class Claim(
    val id: String,
    val policyId: String,
    val type: String,
    val amount: Double,
    val status: ClaimStatus,
    val dateSubmitted: String,
    val description: String
)

enum class InsuranceType {
    LIFE, HEALTH, AUTO, HOME, TRAVEL, DISABILITY
}

enum class PolicyStatus {
    ACTIVE, EXPIRED, PENDING, CANCELLED
}

enum class ClaimStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PAID
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceScreen(
    onBackClick: () -> Unit,
    onPolicyClick: (String) -> Unit,
    onBuyInsuranceClick: () -> Unit,
    onClaimClick: (String) -> Unit
) {
    val policies = remember { getDummyPolicies() }
    val claims = remember { getDummyClaims() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Policies", "Claims", "Buy Insurance")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onBuyInsuranceClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buy Insurance")
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
                0 -> MyPoliciesContent(
                    policies = policies,
                    onPolicyClick = onPolicyClick
                )
                1 -> ClaimsContent(
                    claims = claims,
                    onClaimClick = onClaimClick
                )
                2 -> BuyInsuranceContent(
                    onBuyClick = onBuyInsuranceClick
                )
            }
        }
    }
}

@Composable
fun MyPoliciesContent(
    policies: List<InsurancePolicy>,
    onPolicyClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Summary Card
        item {
            InsuranceSummaryCard(policies = policies)
        }

        // Policies Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Policies (${policies.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { /* View all */ }) {
                    Text("View All")
                }
            }
        }

        // Policies List
        if (policies.isEmpty()) {
            item {
                EmptyPoliciesState()
            }
        } else {
            items(policies) { policy ->
                PolicyCard(
                    policy = policy,
                    onClick = { onPolicyClick(policy.id) }
                )
            }
        }
    }
}

@Composable
fun ClaimsContent(
    claims: List<Claim>,
    onClaimClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Recent Claims",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (claims.isEmpty()) {
            item {
                EmptyClaimsState()
            }
        } else {
            items(claims) { claim ->
                ClaimCard(
                    claim = claim,
                    onClick = { onClaimClick(claim.id) }
                )
            }
        }
    }
}

@Composable
fun BuyInsuranceContent(
    onBuyClick: () -> Unit
) {
    val products = remember { getDummyInsuranceProducts() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Available Insurance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Protect what matters most to you",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(products) { product ->
            InsuranceProductCard(
                product = product,
                onBuyClick = onBuyClick
            )
        }
    }
}

@Composable
fun InsuranceSummaryCard(policies: List<InsurancePolicy>) {
    val totalCoverage = policies.sumOf { it.coverage }
    val totalPremium = policies.sumOf { it.premium }
    val activePolicies = policies.count { it.status == PolicyStatus.ACTIVE }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Insurance Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    title = "Total Coverage",
                    value = "£${String.format("%,.0f", totalCoverage)}",
                    icon = Icons.Default.Security
                )
                SummaryItem(
                    title = "Monthly Premium",
                    value = "£${String.format("%.2f", totalPremium)}",
                    icon = Icons.Default.Payment
                )
                SummaryItem(
                    title = "Active Policies",
                    value = activePolicies.toString(),
                    icon = Icons.Default.Policy
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
fun PolicyCard(
    policy: InsurancePolicy,
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getInsuranceTypeIcon(policy.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = getInsuranceTypeName(policy.type),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = policy.provider,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                PolicyStatusChip(status = policy.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Coverage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%,.0f", policy.coverage)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Premium",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "£${String.format("%.2f", policy.premium)}/month",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Renewal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = policy.renewalDate,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ClaimCard(
    claim: Claim,
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
                        text = claim.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Claim #${claim.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ClaimStatusChip(status = claim.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = claim.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount: £${String.format("%,.2f", claim.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Submitted: ${claim.dateSubmitted}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun InsuranceProductCard(
    product: InsuranceProduct,
    onBuyClick: () -> Unit
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
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getInsuranceTypeIcon(product.type),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = product.provider,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", product.rating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Features
            product.features.take(3).forEach { feature ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "From £${String.format("%.2f", product.monthlyPremium)}/month",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Up to £${String.format("%,.0f", product.coverage)} coverage",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Button(
                    onClick = onBuyClick
                ) {
                    Text("Get Quote")
                }
            }
        }
    }
}

@Composable
fun PolicyStatusChip(status: PolicyStatus) {
    val (color, text) = when (status) {
        PolicyStatus.ACTIVE -> Color.Green to "Active"
        PolicyStatus.EXPIRED -> Color.Red to "Expired"
        PolicyStatus.PENDING -> Color(0xFFFF9800) to "Pending"
        PolicyStatus.CANCELLED -> Color.Gray to "Cancelled"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
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
fun ClaimStatusChip(status: ClaimStatus) {
    val (color, text) = when (status) {
        ClaimStatus.SUBMITTED -> Color.Blue to "Submitted"
        ClaimStatus.UNDER_REVIEW -> Color(0xFFFF9800) to "Under Review"
        ClaimStatus.APPROVED -> Color.Green to "Approved"
        ClaimStatus.REJECTED -> Color.Red to "Rejected"
        ClaimStatus.PAID -> Color(0xFF4CAF50) to "Paid"
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
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
fun EmptyPoliciesState() {
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
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Insurance Policies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Protect what matters most to you. Browse our insurance products to get started.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EmptyClaimsState() {
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
                imageVector = Icons.Default.Assignment,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Claims",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You haven't submitted any insurance claims yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun getInsuranceTypeIcon(type: InsuranceType): ImageVector {
    return when (type) {
        InsuranceType.LIFE -> Icons.Default.Favorite
        InsuranceType.HEALTH -> Icons.Default.LocalHospital
        InsuranceType.AUTO -> Icons.Default.DirectionsCar
        InsuranceType.HOME -> Icons.Default.Home
        InsuranceType.TRAVEL -> Icons.Default.Flight
        InsuranceType.DISABILITY -> Icons.Default.Accessible
    }
}

fun getInsuranceTypeName(type: InsuranceType): String {
    return when (type) {
        InsuranceType.LIFE -> "Life Insurance"
        InsuranceType.HEALTH -> "Health Insurance"
        InsuranceType.AUTO -> "Auto Insurance"
        InsuranceType.HOME -> "Home Insurance"
        InsuranceType.TRAVEL -> "Travel Insurance"
        InsuranceType.DISABILITY -> "Disability Insurance"
    }
}

fun getDummyPolicies(): List<InsurancePolicy> {
    return listOf(
        InsurancePolicy(
            id = "POL001",
            type = InsuranceType.LIFE,
            provider = "Monzo Life",
            policyNumber = "ML-2024-001",
            premium = 45.99,
            coverage = 250000.0,
            status = PolicyStatus.ACTIVE,
            renewalDate = "Dec 15, 2024",
            description = "Term life insurance with comprehensive coverage"
        ),
        InsurancePolicy(
            id = "POL002",
            type = InsuranceType.HEALTH,
            provider = "Monzo Health",
            policyNumber = "MH-2024-002",
            premium = 89.99,
            coverage = 50000.0,
            status = PolicyStatus.ACTIVE,
            renewalDate = "Jan 20, 2025",
            description = "Comprehensive health insurance with dental coverage"
        ),
        InsurancePolicy(
            id = "POL003",
            type = InsuranceType.AUTO,
            provider = "Monzo Auto",
            policyNumber = "MA-2024-003",
            premium = 125.50,
            coverage = 100000.0,
            status = PolicyStatus.ACTIVE,
            renewalDate = "Mar 10, 2025",
            description = "Full coverage auto insurance with roadside assistance"
        )
    )
}

fun getDummyClaims(): List<Claim> {
    return listOf(
        Claim(
            id = "CLM001",
            policyId = "POL002",
            type = "Medical Expense",
            amount = 1250.00,
            status = ClaimStatus.APPROVED,
            dateSubmitted = "Sep 15, 2024",
            description = "Emergency room visit and treatment"
        ),
        Claim(
            id = "CLM002",
            policyId = "POL003",
            type = "Auto Accident",
            amount = 3500.00,
            status = ClaimStatus.UNDER_REVIEW,
            dateSubmitted = "Oct 2, 2024",
            description = "Minor collision damage repair"
        )
    )
}

fun getDummyInsuranceProducts(): List<InsuranceProduct> {
    return listOf(
        InsuranceProduct(
            id = "PROD001",
            name = "Essential Life Cover",
            type = InsuranceType.LIFE,
            provider = "Monzo Life",
            monthlyPremium = 25.99,
            coverage = 100000.0,
            features = listOf(
                "24/7 customer support",
                "No medical exam required",
                "Instant approval",
                "Flexible payment options"
            ),
            rating = 4.8f
        ),
        InsuranceProduct(
            id = "PROD002",
            name = "Premium Health Plan",
            type = InsuranceType.HEALTH,
            provider = "Monzo Health",
            monthlyPremium = 65.99,
            coverage = 25000.0,
            features = listOf(
                "Comprehensive medical coverage",
                "Dental and vision included",
                "Prescription drug coverage",
                "Mental health support"
            ),
            rating = 4.6f
        ),
        InsuranceProduct(
            id = "PROD003",
            name = "Smart Home Protection",
            type = InsuranceType.HOME,
            provider = "Monzo Home",
            monthlyPremium = 35.99,
            coverage = 300000.0,
            features = listOf(
                "Contents and buildings cover",
                "Emergency home repairs",
                "Temporary accommodation",
                "Personal liability protection"
            ),
            rating = 4.7f
        ),
        InsuranceProduct(
            id = "PROD004",
            name = "Travel Essentials",
            type = InsuranceType.TRAVEL,
            provider = "Monzo Travel",
            monthlyPremium = 12.99,
            coverage = 10000.0,
            features = listOf(
                "Worldwide coverage",
                "Trip cancellation protection",
                "Medical emergency coverage",
                "Lost luggage compensation"
            ),
            rating = 4.5f
        )
    )
}
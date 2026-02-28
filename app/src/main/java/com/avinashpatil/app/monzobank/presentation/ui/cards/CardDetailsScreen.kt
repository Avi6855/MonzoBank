package com.avinashpatil.app.monzobank.presentation.ui.cards

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avinashpatil.app.monzobank.data.model.Card
import com.avinashpatil.app.monzobank.data.model.Transaction
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    cardId: String,
    onBackClick: () -> Unit,
    onFreezeClick: () -> Unit = {},
    onControlsClick: () -> Unit = {},
    onPinClick: () -> Unit = {},
    onReplaceClick: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {}
) {
    // Mock card data
    val card = remember {
        Card(
            id = cardId,
            accountId = "acc_1",
            cardNumber = "**** **** **** 1234",
            cardholderName = "John Doe",
            expiryDate = "12/26",
            cardType = "debit",
            provider = "Mastercard",
            isActive = true,
            isFrozen = false,
            spendingLimit = 1000.0,
            currentSpending = 245.67
        )
    }

    // Mock recent transactions for this card
    val recentTransactions = remember {
        listOf(
            Transaction(
                id = "1",
                accountId = "acc_1",
                amount = -45.50,
                description = "Tesco Express",
                category = "Groceries",
                date = Date(),
                merchant = "Tesco",
                type = "debit"
            ),
            Transaction(
                id = "2",
                accountId = "acc_1",
                amount = -12.99,
                description = "Netflix Subscription",
                category = "Entertainment",
                date = Date(System.currentTimeMillis() - 86400000),
                merchant = "Netflix",
                type = "debit"
            ),
            Transaction(
                id = "3",
                accountId = "acc_1",
                amount = -89.99,
                description = "Amazon Purchase",
                category = "Shopping",
                date = Date(System.currentTimeMillis() - 172800000),
                merchant = "Amazon",
                type = "debit"
            )
        )
    }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Card Details") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { /* More options */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card Visual
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFF8B5CF6)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "Monzo",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = card.provider,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Text(
                                text = card.cardNumber,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "CARDHOLDER",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = card.cardholderName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        text = "EXPIRES",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = card.expiryDate,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Card Status
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (card.isFrozen) 
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (card.isFrozen) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (card.isFrozen) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (card.isFrozen) "Card Frozen" else "Card Active",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (card.isFrozen) 
                                    "Your card is temporarily frozen" 
                                else 
                                    "Your card is ready to use",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // Spending Limit
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Spending Limit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val progress = (card.currentSpending / card.spendingLimit).toFloat()
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth(),
                            color = if (progress > 0.8f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Spent: ${currencyFormatter.format(card.currentSpending)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Limit: ${currencyFormatter.format(card.spendingLimit)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Card Controls",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            CardActionButton(
                                icon = if (card.isFrozen) Icons.Default.PlayArrow else Icons.Default.Pause,
                                label = if (card.isFrozen) "Unfreeze" else "Freeze",
                                onClick = onFreezeClick
                            )
                            CardActionButton(
                                icon = Icons.Default.Settings,
                                label = "Controls",
                                onClick = onControlsClick
                            )
                            CardActionButton(
                                icon = Icons.Default.Lock,
                                label = "PIN",
                                onClick = onPinClick
                            )
                            CardActionButton(
                                icon = Icons.Default.Refresh,
                                label = "Replace",
                                onClick = onReplaceClick
                            )
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(recentTransactions) { transaction ->
                CardTransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
            }

            // View All Transactions
            item {
                OutlinedButton(
                    onClick = { /* Navigate to all card transactions */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View All Card Transactions")
                }
            }
        }
    }
}

@Composable
fun CardActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CardTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.UK)
    val dateFormatter = SimpleDateFormat("MMM dd, HH:mm", Locale.UK)
    
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
            // Transaction Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Transaction Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${transaction.category} • ${dateFormatter.format(transaction.date)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Amount
            Text(
                text = currencyFormatter.format(Math.abs(transaction.amount)),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
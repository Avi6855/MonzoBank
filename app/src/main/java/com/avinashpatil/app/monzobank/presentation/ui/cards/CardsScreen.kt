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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avinashpatil.app.monzobank.domain.model.Card
import com.avinashpatil.app.monzobank.domain.model.CardType
import com.avinashpatil.app.monzobank.domain.model.DeliveryStatus
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralPrimary
import com.avinashpatil.app.monzobank.presentation.theme.MonzoCoralSecondary
import com.avinashpatil.app.monzobank.presentation.viewmodel.CardViewModel
import com.avinashpatil.app.monzobank.presentation.viewmodel.CardUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCardDetails: (String) -> Unit,
    cardViewModel: CardViewModel? = null
) {
    // Mock data for demonstration
    val cardUiState = CardUiState(cards = emptyList(), isLoading = false)
    val selectedCard: Card? = null
    
    var showCreateCardDialog by remember { mutableStateOf(false) }
    
    // Mock user ID - in real app this would come from authentication
    val userId = "user123"
    
    // LaunchedEffect(Unit) {
    //     cardViewModel?.loadCards(userId)
    // }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Cards",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showCreateCardDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Card",
                        tint = MonzoCoralPrimary
                    )
                }
            }
        )
        
        if (cardUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MonzoCoralPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cards Summary
                item {
                    CardsSummary(
                        physicalCards = emptyList(), // cardViewModel?.getPhysicalCards() ?: emptyList(),
                        virtualCards = emptyList() // cardViewModel?.getVirtualCards() ?: emptyList()
                    )
                }
                
                // Physical Cards Section
                val physicalCards = emptyList<Card>() // cardViewModel?.getPhysicalCards() ?: emptyList()
                if (physicalCards.isNotEmpty()) {
                    item {
                        Text(
                            text = "Physical Cards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(physicalCards) { card ->
                        CardItem(
                            card = card,
                            onClick = { 
                                // cardViewModel?.selectCard(card)
                                onNavigateToCardDetails(card.id) 
                            },
                            onFreezeToggle = {
                                // if (card.isFrozen) {
                                //     cardViewModel?.unfreezeCard(card.id)
                                // } else {
                                //     cardViewModel?.freezeCard(card.id)
                                // }
                            }
                        )
                    }
                }
                
                // Virtual Cards Section
                val virtualCards = emptyList<Card>() // cardViewModel?.getVirtualCards() ?: emptyList()
                if (virtualCards.isNotEmpty()) {
                    item {
                        Text(
                            text = "Virtual Cards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )
                    }
                    
                    items(virtualCards) { card ->
                        CardItem(
                            card = card,
                            onClick = { 
                                // cardViewModel?.selectCard(card)
                                onNavigateToCardDetails(card.id) 
                            },
                            onFreezeToggle = {
                                // if (card.isFrozen) {
                                //     cardViewModel?.unfreezeCard(card.id)
                                // } else {
                                //     cardViewModel?.freezeCard(card.id)
                                // }
                            }
                        )
                    }
                }
                
                // Empty state if no cards
                if (cardUiState.cards.isEmpty()) {
                    item {
                        EmptyCardsState(
                            onCreateCardClick = { showCreateCardDialog = true }
                        )
                    }
                }
            }
        }
    }
    
    // Create Card Dialog
    if (showCreateCardDialog) {
        CreateCardDialog(
            onDismiss = { showCreateCardDialog = false },
            onCreateCard = { cardType, cardName ->
                // For demo purposes, using a mock account ID
                // cardViewModel?.createCard(
                //     userId = userId,
                //     accountId = "account123",
                //     cardType = cardType,
                //     cardName = cardName
                // )
                showCreateCardDialog = false
            },
            isLoading = cardUiState.isCreatingCard
        )
    }
    
    // Error handling
    cardUiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or handle error
            // cardViewModel?.clearError()
        }
    }
}

@Composable
fun CardsSummary(
    physicalCards: List<Card>,
    virtualCards: List<Card>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = physicalCards.size.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MonzoCoralPrimary
                )
                Text(
                    text = "Physical Cards",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            VerticalDivider(
                modifier = Modifier.height(48.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = virtualCards.size.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MonzoCoralSecondary
                )
                Text(
                    text = "Virtual Cards",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    onClick: () -> Unit,
    onFreezeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Card Visual
            CardVisual(
                card = card,
                modifier = Modifier.padding(16.dp)
            )
            
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            
            // Card Controls
            CardControls(
                card = card,
                onFreezeToggle = onFreezeToggle,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun CardVisual(
    card: Card,
    modifier: Modifier = Modifier
) {
    val cardGradient = if (card.cardType == CardType.DEBIT) {
        Brush.linearGradient(
            colors = listOf(
                MonzoCoralPrimary,
                MonzoCoralSecondary
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFF6366F1),
                Color(0xFF8B5CF6)
            )
        )
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = card.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    
                    Text(
                        text = card.cardType.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                // Card Status
                Surface(
                    color = if (card.isFrozen) 
                        Color.Red.copy(alpha = 0.2f) 
                    else 
                        Color.Green.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (card.isFrozen) "Frozen" else "Active",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Card Number
            Text(
                text = card.maskedCardNumber,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                letterSpacing = 2.sp
            )
            
            // Card Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "EXPIRES",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    
                    Text(
                        text = SimpleDateFormat("MM/yy", Locale.getDefault()).format(card.expiryDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
                
                // Mastercard logo placeholder
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MC",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CardControls(
    card: Card,
    onFreezeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // Delivery Status (for physical cards)
        if (card.cardType == CardType.DEBIT && card.deliveryStatus != DeliveryStatus.DELIVERED) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    tint = MonzoCoralPrimary,
                    modifier = Modifier.size(16.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Status: ${card.statusText}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Tracking info would be shown here for physical cards
                if (card.deliveryStatus == DeliveryStatus.DISPATCHED) {
                    Text(
                        text = " • Tracking available",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Quick Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Freeze/Unfreeze Button
            OutlinedButton(
                onClick = onFreezeToggle,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (card.isFrozen) Color(0xFF4CAF50) else Color(0xFFFF5722)
                )
            ) {
                Icon(
                    imageVector = if (card.isFrozen) Icons.Default.PlayArrow else Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (card.isFrozen) "Unfreeze" else "Freeze")
            }
            
            // Settings Button
            OutlinedButton(
                onClick = { /* Navigate to card settings */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MonzoCoralPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Settings")
            }
        }
        
        // Card Limits Info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Daily Limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "£${String.format("%.0f", card.dailyLimit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Column {
                Text(
                    text = "Monthly Limit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "£${String.format("%.0f", card.monthlyLimit)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Column {
                Text(
                    text = "Features",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row {
                    if (card.contactlessEnabled) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Contactless",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4CAF50)
                        )
                    }
                    if (card.internationalEnabled) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "International",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF4CAF50)
                        )
                    }
                    // Online payments are enabled by default for all cards
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Online",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCardsState(
    onCreateCardClick: () -> Unit
) {
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
                imageVector = Icons.Default.AccountBox,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No cards yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Order your first Monzo card to start spending",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            
            Button(
                onClick = onCreateCardClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Order Card")
            }
        }
    }
}

@Composable
fun CreateCardDialog(
    onDismiss: () -> Unit,
    onCreateCard: (CardType, String?) -> Unit,
    isLoading: Boolean
) {
    var selectedCardType by remember { mutableStateOf(CardType.DEBIT) }
    var cardName by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Order New Card",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Choose card type:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                CardType.values().forEach { cardType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCardType = cardType }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCardType == cardType,
                            onClick = { selectedCardType = cardType },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MonzoCoralPrimary
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = cardType.name.lowercase().replaceFirstChar { it.uppercase() } + " Card",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = if (cardType == CardType.DEBIT) {
                                    "Physical debit card delivered to your address"
                                } else if (cardType == CardType.VIRTUAL) {
                                    "Instant virtual card for online payments"
                                } else {
                                    "Card for your banking needs"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                OutlinedTextField(
                    value = cardName,
                    onValueChange = { cardName = it },
                    label = { Text("Card Name (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MonzoCoralPrimary,
                        focusedLabelColor = MonzoCoralPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateCard(
                        selectedCardType,
                        cardName.takeIf { it.isNotBlank() }
                    )
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonzoCoralPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Order Card")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}
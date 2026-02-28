package com.avinashpatil.app.monzobank.presentation.ui.pots

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

data class Pot(
    val id: String,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val color: Color,
    val emoji: String
)

// Main Pots Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotsScreen(
    onBackClick: () -> Unit,
    onPotClick: (Pot) -> Unit,
    onCreatePotClick: () -> Unit
) {
    val pots = remember {
        listOf(
            Pot("1", "Holiday Fund", 2000.0, 850.0, Color.Blue, "🏖️"),
            Pot("2", "Emergency Fund", 5000.0, 3200.0, Color.Red, "🚨"),
            Pot("3", "New Car", 15000.0, 7500.0, Color.Green, "🚗")
        )
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Pots & Savings") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = onCreatePotClick) {
                    Icon(Icons.Default.Add, contentDescription = "Create Pot")
                }
            }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Total Saved",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "£${String.format("%.2f", pots.sumOf { it.currentAmount })}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Across ${pots.size} pots",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            items(pots) { pot ->
                PotCard(
                    pot = pot,
                    onClick = { onPotClick(pot) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotCard(
    pot: Pot,
    onClick: () -> Unit
) {
    val progress = (pot.currentAmount / pot.targetAmount).toFloat()
    
    Card(
        onClick = onClick,
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
                    Text(
                        pot.emoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            pot.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "£${String.format("%.2f")} of £${String.format("%.2f", pot.targetAmount)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Pot Details Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotDetailsScreen(
    pot: Pot,
    onBackClick: () -> Unit,
    onAddMoney: () -> Unit,
    onWithdraw: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(pot.name) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        pot.emoji,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "£${String.format("%.2f", pot.currentAmount)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "of £${String.format("%.2f", pot.targetAmount)} target",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LinearProgressIndicator(
                        progress = (pot.currentAmount / pot.targetAmount).toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAddMoney,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Money")
                }
                
                OutlinedButton(
                    onClick = onWithdraw,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Withdraw")
                }
            }
        }
    }
}

// Create Pot Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePotScreen(
    onBackClick: () -> Unit,
    onPotCreated: () -> Unit
) {
    var potName by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("💰") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val emojiOptions = listOf("💰", "🏖️", "🚗", "🏠", "🎓", "🚨", "🎁", "✈️")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Create New Pot") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = potName,
                onValueChange = { potName = it },
                label = { Text("Pot Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target Amount (£)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Choose an emoji",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                emojiOptions.forEach { emoji ->
                    FilterChip(
                        onClick = { selectedEmoji = emoji },
                        label = { Text(emoji, style = MaterialTheme.typography.headlineSmall) },
                        selected = selectedEmoji == emoji
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        delay(1500)
                        isLoading = false
                        onPotCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && potName.isNotBlank() && targetAmount.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                } else {
                    Text("Create Pot")
                }
            }
        }
    }
}
package com.avinashpatil.app.monzobank.presentation.ui.investments

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Investment(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val holdings: Double,
    val totalValue: Double,
    val type: InvestmentType
)

enum class InvestmentType {
    STOCK, CRYPTO, FUND, BOND
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentsScreen(
    onBackClick: () -> Unit,
    onInvestmentClick: (Investment) -> Unit,
    onStocksClick: () -> Unit,
    onCryptoClick: () -> Unit
) {
    val investments = remember { getDummyInvestments() }
    val totalPortfolioValue = investments.sumOf { it.totalValue }
    val totalChange = investments.sumOf { it.change * it.holdings }
    val totalChangePercent = if (totalPortfolioValue > 0) (totalChange / (totalPortfolioValue - totalChange)) * 100 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investments") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Portfolio Overview
            item {
                PortfolioOverviewCard(
                    totalValue = totalPortfolioValue,
                    totalChange = totalChange,
                    totalChangePercent = totalChangePercent
                )
            }

            // Quick Actions
            item {
                InvestmentQuickActions(
                    onStocksClick = onStocksClick,
                    onCryptoClick = onCryptoClick
                )
            }

            // Holdings
            item {
                Text(
                    text = "Your Holdings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(investments) { investment ->
                InvestmentCard(
                    investment = investment,
                    onClick = { onInvestmentClick(investment) }
                )
            }
        }
    }
}

@Composable
fun PortfolioOverviewCard(
    totalValue: Double,
    totalChange: Double,
    totalChangePercent: Double
) {
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
                text = "Portfolio Value",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "£${String.format("%.2f", totalValue)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (totalChange >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (totalChange >= 0) Color.Green else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (totalChange >= 0) "+" else ""}£${String.format("%.2f", totalChange)} (${String.format("%.2f", totalChangePercent)}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (totalChange >= 0) Color.Green else Color.Red
                )
            }
        }
    }
}

@Composable
fun InvestmentQuickActions(
    onStocksClick: () -> Unit,
    onCryptoClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onStocksClick() },
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
                    text = "Stocks",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onCryptoClick() },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyBitcoin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Crypto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun InvestmentCard(
    investment: Investment,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = investment.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = investment.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${investment.holdings} shares",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%.2f", investment.totalValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (investment.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (investment.change >= 0) Color.Green else Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format("%.2f", investment.changePercent)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (investment.change >= 0) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

fun getDummyInvestments(): List<Investment> {
    return listOf(
        Investment(
            id = "1",
            name = "Apple Inc.",
            symbol = "AAPL",
            currentPrice = 175.43,
            change = 2.15,
            changePercent = 1.24,
            holdings = 10.0,
            totalValue = 1754.30,
            type = InvestmentType.STOCK
        ),
        Investment(
            id = "2",
            name = "Microsoft Corporation",
            symbol = "MSFT",
            currentPrice = 378.85,
            change = -1.25,
            changePercent = -0.33,
            holdings = 5.0,
            totalValue = 1894.25,
            type = InvestmentType.STOCK
        ),
        Investment(
            id = "3",
            name = "Bitcoin",
            symbol = "BTC",
            currentPrice = 43250.00,
            change = 1250.00,
            changePercent = 2.98,
            holdings = 0.5,
            totalValue = 21625.00,
            type = InvestmentType.CRYPTO
        ),
        Investment(
            id = "4",
            name = "Ethereum",
            symbol = "ETH",
            currentPrice = 2650.00,
            change = -85.00,
            changePercent = -3.11,
            holdings = 2.0,
            totalValue = 5300.00,
            type = InvestmentType.CRYPTO
        ),
        Investment(
            id = "5",
            name = "Vanguard S&P 500 ETF",
            symbol = "VOO",
            currentPrice = 425.67,
            change = 3.21,
            changePercent = 0.76,
            holdings = 25.0,
            totalValue = 10641.75,
            type = InvestmentType.FUND
        )
    )
}
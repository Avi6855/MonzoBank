package com.avinashpatil.app.monzobank.presentation.ui.investments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Cryptocurrency(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val change24h: Double,
    val changePercent24h: Double,
    val marketCap: String,
    val volume24h: String,
    val circulatingSupply: String,
    val holdings: Double = 0.0,
    val rank: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoScreen(
    onBackClick: () -> Unit,
    onCryptoClick: (Cryptocurrency) -> Unit,
    onBuyCrypto: (Cryptocurrency) -> Unit
) {
    val cryptos = remember { getDummyCryptocurrencies() }
    val portfolio = remember { getDummyCryptoPortfolio() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Market", "Portfolio", "News")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cryptocurrency") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search functionality */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
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
                0 -> CryptoMarketContent(
                    cryptos = cryptos,
                    onCryptoClick = onCryptoClick,
                    onBuyCrypto = onBuyCrypto
                )
                1 -> CryptoPortfolioContent(
                    portfolio = portfolio,
                    onCryptoClick = onCryptoClick
                )
                2 -> CryptoNewsContent()
            }
        }
    }
}

@Composable
fun CryptoMarketContent(
    cryptos: List<Cryptocurrency>,
    onCryptoClick: (Cryptocurrency) -> Unit,
    onBuyCrypto: (Cryptocurrency) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CryptoMarketOverview()
        }

        item {
            Text(
                text = "Top Cryptocurrencies",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(cryptos) { crypto ->
            CryptoCard(
                crypto = crypto,
                onClick = { onCryptoClick(crypto) },
                onBuyClick = { onBuyCrypto(crypto) },
                showBuyButton = true
            )
        }
    }
}

@Composable
fun CryptoPortfolioContent(
    portfolio: List<Cryptocurrency>,
    onCryptoClick: (Cryptocurrency) -> Unit
) {
    if (portfolio.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.CurrencyBitcoin,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No crypto holdings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Start investing in cryptocurrency",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                val totalValue = portfolio.sumOf { it.currentPrice * it.holdings }
                val totalChange = portfolio.sumOf { (it.change24h * it.holdings) }
                
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
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "£${String.format("%.2f", totalValue)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
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
                                text = "${if (totalChange >= 0) "+" else ""}£${String.format("%.2f", totalChange)} (24h)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (totalChange >= 0) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }

            items(portfolio) { crypto ->
                CryptoHoldingCard(
                    crypto = crypto,
                    onClick = { onCryptoClick(crypto) }
                )
            }
        }
    }
}

@Composable
fun CryptoNewsContent() {
    val news = remember { getDummyCryptoNews() }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Crypto News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(news) { article ->
            CryptoNewsCard(article = article)
        }
    }
}

@Composable
fun CryptoMarketOverview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Market Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Market Cap",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£1.65T",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "24h Volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "£89.2B",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "BTC Dominance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "52.3%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoCard(
    crypto: Cryptocurrency,
    onClick: () -> Unit,
    onBuyClick: () -> Unit,
    showBuyButton: Boolean = false
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
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Crypto icon placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(getCryptoColor(crypto.symbol)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = crypto.symbol.take(2),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${crypto.rank}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = crypto.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = crypto.symbol,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%.2f", crypto.currentPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (crypto.change24h >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (crypto.change24h >= 0) Color.Green else Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format("%.2f", crypto.changePercent24h)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (crypto.change24h >= 0) Color.Green else Color.Red
                    )
                }
                if (showBuyButton) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onBuyClick,
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Buy",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CryptoHoldingCard(
    crypto: Cryptocurrency,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(getCryptoColor(crypto.symbol)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = crypto.symbol.take(2),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = crypto.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = crypto.symbol,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "£${String.format("%.2f", crypto.currentPrice * crypto.holdings)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${String.format("%.6f", crypto.holdings)} ${crypto.symbol}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun CryptoNewsCard(article: CryptoNewsArticle) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.source,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = article.timeAgo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class CryptoNewsArticle(
    val title: String,
    val summary: String,
    val source: String,
    val timeAgo: String
)

fun getCryptoColor(symbol: String): Color {
    return when (symbol) {
        "BTC" -> Color(0xFFF7931A)
        "ETH" -> Color(0xFF627EEA)
        "ADA" -> Color(0xFF0033AD)
        "DOT" -> Color(0xFFE6007A)
        "SOL" -> Color(0xFF9945FF)
        else -> Color(0xFF6C5CE7)
    }
}

fun getDummyCryptocurrencies(): List<Cryptocurrency> {
    return listOf(
        Cryptocurrency(
            id = "1",
            name = "Bitcoin",
            symbol = "BTC",
            currentPrice = 43250.00,
            change24h = 1250.00,
            changePercent24h = 2.98,
            marketCap = "847B",
            volume24h = "28.5B",
            circulatingSupply = "19.6M",
            holdings = 0.5,
            rank = 1
        ),
        Cryptocurrency(
            id = "2",
            name = "Ethereum",
            symbol = "ETH",
            currentPrice = 2650.00,
            change24h = -85.00,
            changePercent24h = -3.11,
            marketCap = "318B",
            volume24h = "15.2B",
            circulatingSupply = "120M",
            holdings = 2.0,
            rank = 2
        ),
        Cryptocurrency(
            id = "3",
            name = "Cardano",
            symbol = "ADA",
            currentPrice = 0.52,
            change24h = 0.03,
            changePercent24h = 6.12,
            marketCap = "18.2B",
            volume24h = "892M",
            circulatingSupply = "35B",
            rank = 3
        ),
        Cryptocurrency(
            id = "4",
            name = "Polkadot",
            symbol = "DOT",
            currentPrice = 7.85,
            change24h = -0.42,
            changePercent24h = -5.08,
            marketCap = "9.8B",
            volume24h = "456M",
            circulatingSupply = "1.2B",
            rank = 4
        ),
        Cryptocurrency(
            id = "5",
            name = "Solana",
            symbol = "SOL",
            currentPrice = 98.75,
            change24h = 4.23,
            changePercent24h = 4.48,
            marketCap = "42.1B",
            volume24h = "2.1B",
            circulatingSupply = "426M",
            rank = 5
        )
    )
}

fun getDummyCryptoPortfolio(): List<Cryptocurrency> {
    return getDummyCryptocurrencies().filter { it.holdings > 0 }
}

fun getDummyCryptoNews(): List<CryptoNewsArticle> {
    return listOf(
        CryptoNewsArticle(
            title = "Bitcoin Reaches New All-Time High",
            summary = "Bitcoin surpassed £43,000 for the first time, driven by institutional adoption and regulatory clarity.",
            source = "CryptoNews",
            timeAgo = "2 hours ago"
        ),
        CryptoNewsArticle(
            title = "Ethereum 2.0 Upgrade Shows Promise",
            summary = "The latest Ethereum upgrade demonstrates significant improvements in transaction speed and energy efficiency.",
            source = "BlockchainToday",
            timeAgo = "5 hours ago"
        ),
        CryptoNewsArticle(
            title = "Major Bank Announces Crypto Services",
            summary = "Leading financial institution announces comprehensive cryptocurrency trading and custody services.",
            source = "FinanceDaily",
            timeAgo = "1 day ago"
        )
    )
}
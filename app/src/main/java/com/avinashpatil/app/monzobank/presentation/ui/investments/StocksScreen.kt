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

data class Stock(
    val id: String,
    val name: String,
    val symbol: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val marketCap: String,
    val volume: String,
    val high52Week: Double,
    val low52Week: Double,
    val holdings: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksScreen(
    onBackClick: () -> Unit,
    onStockClick: (Stock) -> Unit,
    onBuyStock: (Stock) -> Unit
) {
    val stocks = remember { getDummyStocks() }
    val watchlist = remember { getDummyWatchlist() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Market", "Watchlist", "Holdings")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stocks") },
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
                0 -> MarketStocksContent(
                    stocks = stocks,
                    onStockClick = onStockClick,
                    onBuyStock = onBuyStock
                )
                1 -> WatchlistContent(
                    watchlist = watchlist,
                    onStockClick = onStockClick,
                    onBuyStock = onBuyStock
                )
                2 -> HoldingsContent(
                    holdings = stocks.filter { it.holdings > 0 },
                    onStockClick = onStockClick
                )
            }
        }
    }
}

@Composable
fun MarketStocksContent(
    stocks: List<Stock>,
    onStockClick: (Stock) -> Unit,
    onBuyStock: (Stock) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Popular Stocks",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        items(stocks) { stock ->
            StockCard(
                stock = stock,
                onClick = { onStockClick(stock) },
                onBuyClick = { onBuyStock(stock) },
                showBuyButton = true
            )
        }
    }
}

@Composable
fun WatchlistContent(
    watchlist: List<Stock>,
    onStockClick: (Stock) -> Unit,
    onBuyStock: (Stock) -> Unit
) {
    if (watchlist.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your watchlist is empty",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Add stocks to keep track of their performance",
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
            items(watchlist) { stock ->
                StockCard(
                    stock = stock,
                    onClick = { onStockClick(stock) },
                    onBuyClick = { onBuyStock(stock) },
                    showBuyButton = true
                )
            }
        }
    }
}

@Composable
fun HoldingsContent(
    holdings: List<Stock>,
    onStockClick: (Stock) -> Unit
) {
    if (holdings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No stock holdings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Start investing to see your holdings here",
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
                val totalValue = holdings.sumOf { it.currentPrice * it.holdings }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Total Holdings Value",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "£${String.format("%.2f", totalValue)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(holdings) { stock ->
                StockHoldingCard(
                    stock = stock,
                    onClick = { onStockClick(stock) }
                )
            }
        }
    }
}

@Composable
fun StockCard(
    stock: Stock,
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Vol: ${stock.volume}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "£${String.format("%.2f", stock.currentPrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (stock.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (stock.change >= 0) Color.Green else Color.Red,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (stock.change >= 0) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (stock.change >= 0) Color.Green else Color.Red
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
fun StockHoldingCard(
    stock: Stock,
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
                Column {
                    Text(
                        text = stock.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stock.symbol,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "£${String.format("%.2f", stock.currentPrice * stock.holdings)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${stock.holdings} shares",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current Price: £${String.format("%.2f", stock.currentPrice)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (stock.change >= 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (stock.change >= 0) Color.Green else Color.Red,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${String.format("%.2f", stock.changePercent)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (stock.change >= 0) Color.Green else Color.Red
                    )
                }
            }
        }
    }
}

fun getDummyStocks(): List<Stock> {
    return listOf(
        Stock(
            id = "1",
            name = "Apple Inc.",
            symbol = "AAPL",
            currentPrice = 175.43,
            change = 2.15,
            changePercent = 1.24,
            marketCap = "2.7T",
            volume = "45.2M",
            high52Week = 198.23,
            low52Week = 124.17,
            holdings = 10.0
        ),
        Stock(
            id = "2",
            name = "Microsoft Corporation",
            symbol = "MSFT",
            currentPrice = 378.85,
            change = -1.25,
            changePercent = -0.33,
            marketCap = "2.8T",
            volume = "23.1M",
            high52Week = 384.30,
            low52Week = 213.43,
            holdings = 5.0
        ),
        Stock(
            id = "3",
            name = "Amazon.com Inc.",
            symbol = "AMZN",
            currentPrice = 145.86,
            change = 3.42,
            changePercent = 2.40,
            marketCap = "1.5T",
            volume = "31.7M",
            high52Week = 170.00,
            low52Week = 81.43
        ),
        Stock(
            id = "4",
            name = "Tesla Inc.",
            symbol = "TSLA",
            currentPrice = 248.50,
            change = -5.23,
            changePercent = -2.06,
            marketCap = "789B",
            volume = "89.4M",
            high52Week = 299.29,
            low52Week = 101.81
        ),
        Stock(
            id = "5",
            name = "Alphabet Inc.",
            symbol = "GOOGL",
            currentPrice = 140.93,
            change = 1.87,
            changePercent = 1.34,
            marketCap = "1.8T",
            volume = "25.6M",
            high52Week = 151.55,
            low52Week = 83.34
        )
    )
}

fun getDummyWatchlist(): List<Stock> {
    return getDummyStocks().take(2)
}
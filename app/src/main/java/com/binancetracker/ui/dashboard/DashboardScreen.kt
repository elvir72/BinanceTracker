package com.binancetracker.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binancetracker.data.model.Ticker24h
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSymbolClick: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        containerColor = BinanceDark,
        topBar = {
            TopAppBar(
                title = { Text("Binance Futures", color = BinanceText, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BinanceSurface)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            SearchBar(
                query = state.searchQuery,
                onQueryChanged = viewModel::onSearchChanged,
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BinanceYellow)
                }
                return@Scaffold
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Favorites section
                if (state.favorites.isNotEmpty() && state.searchQuery.isBlank()) {
                    item {
                        SectionHeader("⭐ Favorites")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.favorites) { fav ->
                                val ticker = state.tickers[fav.symbol]
                                FavoriteChip(
                                    symbol = fav.symbol,
                                    ticker = ticker,
                                    onClick = {
                                        viewModel.onSymbolSelected(fav.symbol)
                                        onSymbolClick(fav.symbol)
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // Recent section
                if (state.recentSymbols.isNotEmpty() && state.searchQuery.isBlank()) {
                    item {
                        SectionHeader("🕐 Recent")
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.recentSymbols.take(10)) { recent ->
                                AssistChip(
                                    onClick = {
                                        viewModel.onSymbolSelected(recent.symbol)
                                        onSymbolClick(recent.symbol)
                                    },
                                    label = { Text(recent.symbol, fontSize = 12.sp, color = BinanceText) },
                                    colors = AssistChipDefaults.assistChipColors(containerColor = BinanceSurface2)
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        SectionHeader("All Perpetuals (${state.filteredSymbols.size})")
                    }
                }

                // Symbols list
                items(state.filteredSymbols) { symbol ->
                    val ticker = state.tickers[symbol]
                    SymbolRow(
                        symbol = symbol,
                        ticker = ticker,
                        onClick = {
                            viewModel.onSymbolSelected(symbol)
                            onSymbolClick(symbol)
                        }
                    )
                    HorizontalDivider(color = BinanceBorder.copy(alpha = 0.4f), thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChanged: (String) -> Unit, modifier: Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        placeholder = { Text("Search symbol (e.g. XVGUSDT)...", color = BinanceTextSec, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = BinanceTextSec) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BinanceYellow,
            unfocusedBorderColor = BinanceBorder,
            focusedTextColor = BinanceText,
            unfocusedTextColor = BinanceText,
            cursorColor = BinanceYellow,
            focusedContainerColor = BinanceSurface,
            unfocusedContainerColor = BinanceSurface
        )
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        color = BinanceTextSec,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SymbolRow(symbol: String, ticker: Ticker24h?, onClick: () -> Unit) {
    val pct = ticker?.priceChangePercent?.toDoubleOrNull() ?: 0.0
    val color = if (pct >= 0) BinanceGreen else BinanceRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(symbol, color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            ticker?.let {
                Text("Vol: ${Formatter.volume(it.quoteVolume)}", color = BinanceTextSec, fontSize = 11.sp)
            }
        }
        ticker?.let {
            Column(horizontalAlignment = Alignment.End) {
                Text(Formatter.price(it.lastPrice), color = BinanceText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text(Formatter.percent(pct), color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun FavoriteChip(symbol: String, ticker: Ticker24h?, onClick: () -> Unit) {
    val pct = ticker?.priceChangePercent?.toDoubleOrNull() ?: 0.0
    val color = if (pct >= 0) BinanceGreen else BinanceRed

    Card(
        modifier = Modifier.width(120.dp).clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface2)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(symbol, color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            ticker?.let {
                Text(Formatter.price(it.lastPrice), color = BinanceText, fontSize = 11.sp)
                Text(Formatter.percent(pct), color = color, fontSize = 11.sp)
            } ?: Text("--", color = BinanceTextSec, fontSize = 11.sp)
        }
    }
}

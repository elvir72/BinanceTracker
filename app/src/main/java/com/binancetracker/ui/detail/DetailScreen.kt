package com.binancetracker.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.binancetracker.ui.detail.components.*
import com.binancetracker.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    symbol: String,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BinanceDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(symbol, color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(6.dp).then(
                                    Modifier
                                ),
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(if (state.wsConnected) BinanceGreen else BinanceRed)
                                }
                            }
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (state.wsConnected) "Live" else "Offline",
                                color = if (state.wsConnected) BinanceGreen else BinanceRed,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BinanceText)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (state.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (state.isFavorite) BinanceYellow else BinanceTextSec
                        )
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = BinanceTextSec)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BinanceSurface)
            )
        }
    ) { padding ->
        if (state.isLoading && state.ticker == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BinanceYellow)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Price Header
                item {
                    PriceHeaderCard(
                        symbol = symbol,
                        ticker = state.ticker,
                        markPrice = state.markPrice,
                        openInterest = state.openInterest,
                        liveMarkPrice = state.liveMarkPrice,
                        liveTicker = state.liveTicker
                    )
                }

                // 2. Candlestick chart
                item {
                    CandlestickChartCard(
                        klines = state.klines,
                        selectedInterval = state.klineInterval,
                        onIntervalChange = { viewModel.changeInterval(it) }
                    )
                }

                // 3. Order Book
                item {
                    val bids = state.liveOrderBook?.bids ?: state.orderBook?.bids ?: emptyList()
                    val asks = state.liveOrderBook?.asks ?: state.orderBook?.asks ?: emptyList()
                    OrderBookCard(bids = bids, asks = asks)
                }

                // 4. Trade Tape
                item {
                    TradeTapeCard(
                        liveTrades = state.liveTrades,
                        restTrades = state.recentTrades
                    )
                }

                // 5. Liquidations
                item {
                    LiquidationsCard(liquidations = state.liveLiquidations)
                }

                // 6. Open Interest history
                item {
                    OpenInterestCard(oiHistory = state.oiHistory)
                }

                // 7. Long/Short Ratio
                item {
                    LongShortCard(ratios = state.longShortRatio)
                }

                // 8. Funding Rate
                item {
                    FundingRateCard(fundingHistory = state.fundingHistory)
                }

                // Bottom padding
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// Canvas helper for WS dot
@Composable
private fun Canvas(modifier: Modifier, onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    androidx.compose.foundation.Canvas(modifier = modifier, onDraw = onDraw)
}

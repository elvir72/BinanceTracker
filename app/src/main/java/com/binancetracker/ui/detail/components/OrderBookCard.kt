package com.binancetracker.ui.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun OrderBookCard(
    bids: List<List<String>>,
    asks: List<List<String>>,
    modifier: Modifier = Modifier
) {
    val topAsks = asks.take(10).reversed()
    val topBids = bids.take(10)

    val maxBidVol = topBids.maxOfOrNull { it.getOrNull(1)?.toDoubleOrNull() ?: 0.0 } ?: 1.0
    val maxAskVol = topAsks.maxOfOrNull { it.getOrNull(1)?.toDoubleOrNull() ?: 0.0 } ?: 1.0

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Order Book", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            // Header
            Row(Modifier.fillMaxWidth()) {
                Text("Price (USDT)", modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp)
                Text("Size", modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("Total", modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.End)
            }
            Spacer(Modifier.height(4.dp))

            // Asks (red - sell orders)
            topAsks.forEach { entry ->
                val price = entry.getOrNull(0) ?: return@forEach
                val size = entry.getOrNull(1) ?: return@forEach
                val sizeD = size.toDoubleOrNull() ?: 0.0
                val ratio = (sizeD / maxAskVol).coerceIn(0.0, 1.0)
                OrderBookRow(
                    price = Formatter.price(price),
                    size = Formatter.volume(size),
                    total = Formatter.volume((price.toDoubleOrNull() ?: 0.0) * sizeD),
                    barRatio = ratio.toFloat(),
                    barColor = BinanceRed.copy(alpha = 0.25f),
                    priceColor = BinanceRed
                )
            }

            // Spread
            if (bids.isNotEmpty() && asks.isNotEmpty()) {
                val bestBid = bids.firstOrNull()?.firstOrNull()?.toDoubleOrNull() ?: 0.0
                val bestAsk = asks.firstOrNull()?.firstOrNull()?.toDoubleOrNull() ?: 0.0
                val spread = bestAsk - bestBid
                val spreadPct = if (bestBid > 0) (spread / bestBid * 100) else 0.0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Spread: ${Formatter.price(spread)} (${String.format("%.4f", spreadPct)}%)",
                        color = BinanceTextSec,
                        fontSize = 10.sp
                    )
                }
            }

            // Bids (green - buy orders)
            topBids.forEach { entry ->
                val price = entry.getOrNull(0) ?: return@forEach
                val size = entry.getOrNull(1) ?: return@forEach
                val sizeD = size.toDoubleOrNull() ?: 0.0
                val ratio = (sizeD / maxBidVol).coerceIn(0.0, 1.0)
                OrderBookRow(
                    price = Formatter.price(price),
                    size = Formatter.volume(size),
                    total = Formatter.volume((price.toDoubleOrNull() ?: 0.0) * sizeD),
                    barRatio = ratio.toFloat(),
                    barColor = BinanceGreen.copy(alpha = 0.25f),
                    priceColor = BinanceGreen
                )
            }
        }
    }
}

@Composable
private fun OrderBookRow(
    price: String,
    size: String,
    total: String,
    barRatio: Float,
    barColor: Color,
    priceColor: Color
) {
    Box(modifier = Modifier.fillMaxWidth().height(20.dp)) {
        // Background bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(barRatio)
                .align(Alignment.CenterEnd)
                .background(barColor)
        )
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(price, modifier = Modifier.weight(1f), color = priceColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text(size, modifier = Modifier.weight(1f), color = BinanceText, fontSize = 11.sp, textAlign = TextAlign.Center)
            Text(total, modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.End)
        }
    }
}

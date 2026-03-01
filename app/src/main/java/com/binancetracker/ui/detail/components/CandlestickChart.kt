package com.binancetracker.ui.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.data.model.Kline
import com.binancetracker.ui.theme.*

val INTERVALS = listOf("1m", "5m", "15m", "1h", "4h", "1d", "1w")

@Composable
fun CandlestickChartCard(
    klines: List<Kline>,
    selectedInterval: String,
    onIntervalChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Chart", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            // Interval selector
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(INTERVALS) { interval ->
                    val selected = interval == selectedInterval
                    FilterChip(
                        selected = selected,
                        onClick = { onIntervalChange(interval) },
                        label = { Text(interval, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BinanceYellow,
                            selectedLabelColor = BinanceDark,
                            containerColor = BinanceSurface2,
                            labelColor = BinanceTextSec
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (klines.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = BinanceYellow, modifier = Modifier.size(24.dp))
                }
            } else {
                CandlestickCanvas(klines = klines, modifier = Modifier.fillMaxWidth().height(220.dp))
                Spacer(Modifier.height(4.dp))
                VolumeCanvas(klines = klines, modifier = Modifier.fillMaxWidth().height(50.dp))
            }
        }
    }
}

@Composable
private fun CandlestickCanvas(klines: List<Kline>, modifier: Modifier) {
    var visibleCount by remember { mutableStateOf(60) }
    val displayKlines = klines.takeLast(visibleCount.coerceAtLeast(10))

    val highs = displayKlines.map { it.high }
    val lows = displayKlines.map { it.low }
    val maxH = highs.maxOrNull() ?: 1.0
    val minL = lows.minOrNull() ?: 0.0
    val range = if (maxH - minL == 0.0) 1.0 else maxH - minL

    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectTransformGestures { _, _, zoom, _ ->
                visibleCount = (visibleCount / zoom).toInt().coerceIn(10, klines.size)
            }
        }
    ) {
        val w = size.width
        val h = size.height
        val n = displayKlines.size
        val candleW = (w / n) * 0.7f
        val gap = (w / n) * 0.3f

        displayKlines.forEachIndexed { i, kline ->
            val x = i * (w / n) + gap / 2
            val isBull = kline.close >= kline.open
            val color = if (isBull) BinanceGreen else BinanceRed

            val openY = h * (1 - (kline.open - minL) / range).toFloat()
            val closeY = h * (1 - (kline.close - minL) / range).toFloat()
            val highY = h * (1 - (kline.high - minL) / range).toFloat()
            val lowY = h * (1 - (kline.low - minL) / range).toFloat()

            // Wick
            drawLine(color, Offset(x + candleW / 2, highY), Offset(x + candleW / 2, lowY), strokeWidth = 1f)

            // Body
            val bodyTop = minOf(openY, closeY)
            val bodyH = maxOf(kotlin.math.abs(closeY - openY), 1f)
            drawRect(color, Offset(x, bodyTop), Size(candleW, bodyH))
        }

        // Price lines (high/low labels)
        drawLine(BinanceBorder, Offset(0f, 0f), Offset(w, 0f), strokeWidth = 0.5f)
        drawLine(BinanceBorder, Offset(0f, h), Offset(w, h), strokeWidth = 0.5f)
    }
}

@Composable
private fun VolumeCanvas(klines: List<Kline>, modifier: Modifier) {
    val displayKlines = klines.takeLast(60)
    val maxVol = displayKlines.maxOfOrNull { it.volume } ?: 1.0

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val n = displayKlines.size
        val barW = (w / n) * 0.7f
        val gap = (w / n) * 0.3f

        displayKlines.forEachIndexed { i, kline ->
            val x = i * (w / n) + gap / 2
            val isBull = kline.close >= kline.open
            val barH = (h * (kline.volume / maxVol)).toFloat().coerceAtLeast(1f)
            drawRect(
                color = if (isBull) BinanceGreen.copy(alpha = 0.5f) else BinanceRed.copy(alpha = 0.5f),
                topLeft = Offset(x, h - barH),
                size = Size(barW, barH)
            )
        }
    }
}

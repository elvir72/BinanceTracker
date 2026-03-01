package com.binancetracker.ui.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.data.model.LongShortRatioEntry
import com.binancetracker.data.model.OpenInterestHistEntry
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun OpenInterestCard(
    oiHistory: List<OpenInterestHistEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Open Interest", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            if (oiHistory.size >= 2) {
                MiniLineChart(
                    values = oiHistory.map { it.sumOpenInterest.toDoubleOrNull() ?: 0.0 },
                    color = BinanceYellow,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            // Latest 6 entries
            oiHistory.takeLast(6).reversed().forEach { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(Formatter.timeMDHM(entry.timestamp), color = BinanceTextSec, fontSize = 11.sp)
                    Column(horizontalAlignment = Alignment.End) {
                        Text(Formatter.volume(entry.sumOpenInterest), color = BinanceText, fontSize = 12.sp)
                        Text("$${Formatter.volume(entry.sumOpenInterestValue)}", color = BinanceTextSec, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LongShortCard(
    ratios: List<LongShortRatioEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Long/Short Ratio", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            val latest = ratios.lastOrNull()
            if (latest != null) {
                val longPct = (latest.longAccount.toDoubleOrNull() ?: 0.5) * 100
                val shortPct = 100 - longPct
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Long: ${String.format("%.1f", longPct)}%", color = BinanceGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Short: ${String.format("%.1f", shortPct)}%", color = BinanceRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(Modifier.height(6.dp))

                // Bar
                Row(Modifier.fillMaxWidth().height(8.dp)) {
                    Box(modifier = Modifier.weight(longPct.toFloat()).fillMaxHeight()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(BinanceGreen)
                        }
                    }
                    Box(modifier = Modifier.weight(shortPct.toFloat()).fillMaxHeight()) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawRect(BinanceRed)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            if (ratios.size >= 2) {
                MiniLineChart(
                    values = ratios.map { it.longShortRatio.toDoubleOrNull() ?: 1.0 },
                    color = BinanceYellow,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                )
            }
        }
    }
}

@Composable
fun MiniLineChart(values: List<Double>, color: androidx.compose.ui.graphics.Color, modifier: Modifier) {
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: 1.0
    val range = if (max - min == 0.0) 1.0 else max - min

    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = w * i / (values.size - 1)
            val y = h * (1 - ((v - min) / range)).toFloat().coerceIn(0f, 1f)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color, style = Stroke(width = 2f))
        // Dots at last point
        val lastX = w
        val lastY = h * (1 - ((values.last() - min) / range)).toFloat().coerceIn(0f, 1f)
        drawCircle(color, radius = 4f, center = Offset(lastX, lastY))
    }
}

package com.binancetracker.ui.detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.data.model.FundingRateEntry
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun FundingRateCard(
    fundingHistory: List<FundingRateEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Funding Rate History", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            // Mini chart
            if (fundingHistory.size >= 2) {
                FundingMiniChart(fundingHistory, modifier = Modifier.fillMaxWidth().height(60.dp))
                Spacer(Modifier.height(8.dp))
            }

            // List
            fundingHistory.take(8).forEach { entry ->
                val rate = entry.fundingRate.toDoubleOrNull() ?: 0.0
                val rateColor = if (rate >= 0) BinanceRed else BinanceGreen
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(Formatter.timeMDHM(entry.fundingTime), color = BinanceTextSec, fontSize = 11.sp)
                    Text(Formatter.fundingRate(entry.fundingRate), color = rateColor, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun FundingMiniChart(entries: List<FundingRateEntry>, modifier: Modifier) {
    val values = entries.map { it.fundingRate.toDoubleOrNull() ?: 0.0 }
    val min = values.minOrNull() ?: 0.0
    val max = values.maxOrNull() ?: 0.0
    val range = if (max - min == 0.0) 1.0 else max - min

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val zeroY = h * (1 - (0.0 - min) / range).toFloat().coerceIn(0f, 1f)

        // Zero line
        drawLine(BinanceTextSec.copy(alpha = 0.3f), Offset(0f, zeroY), Offset(w, zeroY), strokeWidth = 1f)

        // Line
        if (values.size >= 2) {
            val path = Path()
            values.forEachIndexed { i, v ->
                val x = w * i / (values.size - 1)
                val y = h * (1 - ((v - min) / range)).toFloat().coerceIn(0f, 1f)
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, BinanceYellow, style = Stroke(width = 2f))
        }
    }
}

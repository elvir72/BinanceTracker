package com.binancetracker.ui.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.data.model.WsLiquidation
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun LiquidationsCard(
    liquidations: List<WsLiquidation>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Liquidations", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.width(8.dp))
                if (liquidations.isNotEmpty()) {
                    Surface(
                        color = BinanceRed.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            "LIVE",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = BinanceRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            if (liquidations.isEmpty()) {
                Text("No liquidations yet...", color = BinanceTextSec, fontSize = 12.sp)
            } else {
                Row(Modifier.fillMaxWidth()) {
                    Text("Side", Modifier.weight(0.8f), color = BinanceTextSec, fontSize = 11.sp)
                    Text("Price", Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.Center)
                    Text("Qty", Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.Center)
                    Text("Time", Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.End)
                }
                Spacer(Modifier.height(4.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(liquidations) { liq ->
                        val isBuy = liq.side == "BUY"
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (isBuy) "BUY" else "SELL",
                                Modifier.weight(0.8f),
                                color = if (isBuy) BinanceGreen else BinanceRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(Formatter.price(liq.price), Modifier.weight(1f), color = BinanceText, fontSize = 11.sp, textAlign = TextAlign.Center)
                            Text(Formatter.volume(liq.qty), Modifier.weight(1f), color = BinanceText, fontSize = 11.sp, textAlign = TextAlign.Center)
                            Text(Formatter.timeHHMM(liq.time), Modifier.weight(1f), color = BinanceTextSec, fontSize = 10.sp, textAlign = TextAlign.End)
                        }
                    }
                }
            }
        }
    }
}

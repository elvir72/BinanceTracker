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
import com.binancetracker.data.model.TradeEntry
import com.binancetracker.data.model.WsAggTrade
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun TradeTapeCard(
    liveTrades: List<WsAggTrade>,
    restTrades: List<TradeEntry>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Recent Trades", color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Price", modifier = Modifier.weight(1.2f), color = BinanceTextSec, fontSize = 11.sp)
                Text("Size", modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.Center)
                Text("Time", modifier = Modifier.weight(1f), color = BinanceTextSec, fontSize = 11.sp, textAlign = TextAlign.End)
            }
            Spacer(Modifier.height(4.dp))

            if (liveTrades.isNotEmpty()) {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(liveTrades.take(30), key = { it.time }) { trade ->
                        val isBuy = !trade.isBuyerMaker
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                Formatter.price(trade.price),
                                modifier = Modifier.weight(1.2f),
                                color = if (isBuy) BinanceGreen else BinanceRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                Formatter.volume(trade.qty),
                                modifier = Modifier.weight(1f),
                                color = BinanceText,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                Formatter.timeHHMM(trade.time),
                                modifier = Modifier.weight(1f),
                                color = BinanceTextSec,
                                fontSize = 10.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(restTrades.take(30)) { trade ->
                        val isBuy = !trade.isBuyerMaker
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                Formatter.price(trade.price),
                                modifier = Modifier.weight(1.2f),
                                color = if (isBuy) BinanceGreen else BinanceRed,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                Formatter.volume(trade.qty),
                                modifier = Modifier.weight(1f),
                                color = BinanceText,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                Formatter.timeHHMM(trade.time),
                                modifier = Modifier.weight(1f),
                                color = BinanceTextSec,
                                fontSize = 10.sp,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

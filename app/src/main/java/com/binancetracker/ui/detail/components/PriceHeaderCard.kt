package com.binancetracker.ui.detail.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binancetracker.data.model.*
import com.binancetracker.ui.theme.*
import com.binancetracker.util.Formatter

@Composable
fun PriceHeaderCard(
    symbol: String,
    ticker: Ticker24h?,
    markPrice: MarkPriceData?,
    openInterest: OpenInterestData?,
    liveMarkPrice: WsMarkPrice?,
    liveTicker: WsTickerData?,
    modifier: Modifier = Modifier
) {
    val lastPrice = liveTicker?.lastPrice ?: ticker?.lastPrice ?: "0"
    val changePercent = liveTicker?.priceChangePercent ?: ticker?.priceChangePercent ?: "0"
    val changeD = changePercent.toDoubleOrNull() ?: 0.0
    val priceColor by animateColorAsState(
        if (changeD >= 0) BinanceGreen else BinanceRed,
        label = "priceColor"
    )

    val markPriceVal = liveMarkPrice?.markPrice ?: markPrice?.markPrice ?: "0"
    val indexPrice = liveMarkPrice?.indexPrice ?: markPrice?.indexPrice ?: "0"
    val fundingRate = liveMarkPrice?.fundingRate ?: markPrice?.lastFundingRate ?: "0"
    val nextFunding = liveMarkPrice?.nextFundingTime ?: markPrice?.nextFundingTime ?: 0L
    val fundingD = fundingRate.toDoubleOrNull() ?: 0.0
    val fundingColor = if (fundingD >= 0) BinanceRed else BinanceGreen // positive = longs pay

    val highPrice = liveTicker?.highPrice ?: ticker?.highPrice ?: "0"
    val lowPrice = liveTicker?.lowPrice ?: ticker?.lowPrice ?: "0"
    val volume = liveTicker?.volume ?: ticker?.volume ?: "0"
    val quoteVolume = liveTicker?.quoteVolume ?: ticker?.quoteVolume ?: "0"
    val oiValue = openInterest?.openInterest ?: "0"

    // Countdown timer for funding
    var countdown by remember { mutableStateOf("--:--:--") }
    LaunchedEffect(nextFunding) {
        while (true) {
            countdown = Formatter.countdown(nextFunding)
            kotlinx.coroutines.delay(1000)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BinanceSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Symbol + price row
            Row(verticalAlignment = Alignment.Bottom) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(symbol, color = BinanceText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Perpetual", color = BinanceTextSec, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        Formatter.price(lastPrice),
                        color = priceColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                    Text(
                        Formatter.percent(changeD),
                        color = priceColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = BinanceBorder, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            // Mark + Index + Funding row
            Row(Modifier.fillMaxWidth()) {
                StatItem("Mark Price", Formatter.price(markPriceVal), BinanceText, Modifier.weight(1f))
                StatItem("Index Price", Formatter.price(indexPrice), BinanceText, Modifier.weight(1f))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text("Funding", color = BinanceTextSec, fontSize = 10.sp)
                    Text(Formatter.fundingRate(fundingRate), color = fundingColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(countdown, color = BinanceTextSec, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            // 24h stats grid
            Row(Modifier.fillMaxWidth()) {
                StatItem("24h High", Formatter.price(highPrice), BinanceGreen, Modifier.weight(1f))
                StatItem("24h Low", Formatter.price(lowPrice), BinanceRed, Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                StatItem("Volume", Formatter.volume(volume), BinanceText, Modifier.weight(1f))
                StatItem("USDT Vol", Formatter.volume(quoteVolume), BinanceText, Modifier.weight(1f))
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                StatItem("Open Interest", Formatter.volume(oiValue), BinanceYellow, Modifier.weight(1f))
                StatItem("Trades 24h", ticker?.count?.toString() ?: "--", BinanceText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = BinanceTextSec, fontSize = 10.sp)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

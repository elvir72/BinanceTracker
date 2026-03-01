package com.binancetracker.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Binance-style dark palette
val BinanceYellow = Color(0xFFF0B90B)
val BinanceGreen = Color(0xFF0ECB81)
val BinanceRed = Color(0xFFF6465D)
val BinanceDark = Color(0xFF0B0E11)
val BinanceSurface = Color(0xFF1E2329)
val BinanceSurface2 = Color(0xFF2B3139)
val BinanceText = Color(0xFFEAECEF)
val BinanceTextSec = Color(0xFF848E9C)
val BinanceBorder = Color(0xFF2B3139)

private val DarkColorScheme = darkColorScheme(
    primary = BinanceYellow,
    onPrimary = BinanceDark,
    secondary = BinanceGreen,
    onSecondary = BinanceDark,
    background = BinanceDark,
    onBackground = BinanceText,
    surface = BinanceSurface,
    onSurface = BinanceText,
    surfaceVariant = BinanceSurface2,
    onSurfaceVariant = BinanceTextSec,
    error = BinanceRed,
    onError = Color.White,
    outline = BinanceBorder
)

@Composable
fun BinanceTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

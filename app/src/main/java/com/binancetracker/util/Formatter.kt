package com.binancetracker.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

object Formatter {
    private val df2 = DecimalFormat("#,##0.00")
    private val df4 = DecimalFormat("#,##0.0000")
    private val df6 = DecimalFormat("#,##0.000000")
    private val dfPct = DecimalFormat("+#,##0.00;-#,##0.00")
    private val dfVol = DecimalFormat("#,##0.00")

    fun price(value: String): String = price(value.toDoubleOrNull() ?: 0.0)
    fun price(value: Double): String {
        return when {
            value == 0.0 -> "0.0000"
            value >= 1000 -> df2.format(value)
            value >= 1 -> df4.format(value)
            else -> df6.format(value)
        }
    }

    fun percent(value: String): String = percent(value.toDoubleOrNull() ?: 0.0)
    fun percent(value: Double): String = "${dfPct.format(value)}%"

    fun volume(value: String): String = volume(value.toDoubleOrNull() ?: 0.0)
    fun volume(value: Double): String {
        return when {
            value >= 1_000_000_000 -> "${dfVol.format(value / 1_000_000_000)}B"
            value >= 1_000_000 -> "${dfVol.format(value / 1_000_000)}M"
            value >= 1_000 -> "${dfVol.format(value / 1_000)}K"
            else -> dfVol.format(value)
        }
    }

    fun fundingRate(value: String): String {
        val d = value.toDoubleOrNull() ?: 0.0
        return "${DecimalFormat("+0.0000;-0.0000").format(d * 100)}%"
    }

    fun timeHHMM(millis: Long): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun timeMDHM(millis: Long): String {
        val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        return sdf.format(Date(millis))
    }

    fun countdown(targetMillis: Long): String {
        val diff = targetMillis - System.currentTimeMillis()
        if (diff <= 0) return "00:00:00"
        val h = diff / 3_600_000
        val m = (diff % 3_600_000) / 60_000
        val s = (diff % 60_000) / 1000
        return "%02d:%02d:%02d".format(h, m, s)
    }
}

fun String.toDoubleOrZero() = toDoubleOrNull() ?: 0.0
fun Double.isPositive() = this > 0

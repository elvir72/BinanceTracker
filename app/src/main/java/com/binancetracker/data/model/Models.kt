package com.binancetracker.data.model

// WSZYSTKIE IMPORTY NA POCZĄTKU!
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.room.Entity
import androidx.room.PrimaryKey

// ── REST Models ──────────────────────────────────────────────

@JsonClass(generateAdapter = true)
data class Ticker24h(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "priceChange") val priceChange: String = "0",
    @Json(name = "priceChangePercent") val priceChangePercent: String = "0",
    @Json(name = "weightedAvgPrice") val weightedAvgPrice: String = "0",
    @Json(name = "lastPrice") val lastPrice: String = "0",
    @Json(name = "lastQty") val lastQty: String = "0",
    @Json(name = "openPrice") val openPrice: String = "0",
    @Json(name = "highPrice") val highPrice: String = "0",
    @Json(name = "lowPrice") val lowPrice: String = "0",
    @Json(name = "volume") val volume: String = "0",
    @Json(name = "quoteVolume") val quoteVolume: String = "0",
    @Json(name = "openTime") val openTime: Long = 0,
    @Json(name = "closeTime") val closeTime: Long = 0,
    @Json(name = "firstId") val firstId: Long = 0,
    @Json(name = "lastId") val lastId: Long = 0,
    @Json(name = "count") val count: Int = 0
)

@JsonClass(generateAdapter = true)
data class MarkPriceData(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "markPrice") val markPrice: String = "0",
    @Json(name = "indexPrice") val indexPrice: String = "0",
    @Json(name = "estimatedSettlePrice") val estimatedSettlePrice: String = "0",
    @Json(name = "lastFundingRate") val lastFundingRate: String = "0",
    @Json(name = "nextFundingTime") val nextFundingTime: Long = 0,
    @Json(name = "interestRate") val interestRate: String = "0",
    @Json(name = "time") val time: Long = 0
)

@JsonClass(generateAdapter = true)
data class OpenInterestData(
    @Json(name = "openInterest") val openInterest: String = "0",
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "time") val time: Long = 0
)

@JsonClass(generateAdapter = true)
data class FundingRateEntry(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "fundingTime") val fundingTime: Long = 0,
    @Json(name = "fundingRate") val fundingRate: String = "0",
    @Json(name = "markPrice") val markPrice: String? = null
)

@JsonClass(generateAdapter = true)
data class TradeEntry(
    @Json(name = "id") val id: Long = 0,
    @Json(name = "price") val price: String = "0",
    @Json(name = "qty") val qty: String = "0",
    @Json(name = "quoteQty") val quoteQty: String = "0",
    @Json(name = "time") val time: Long = 0,
    @Json(name = "isBuyerMaker") val isBuyerMaker: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OrderBookData(
    @Json(name = "lastUpdateId") val lastUpdateId: Long = 0,
    @Json(name = "T") val transactionTime: Long = 0,
    @Json(name = "E") val eventTime: Long = 0,
    @Json(name = "bids") val bids: List<List<String>> = emptyList(),
    @Json(name = "asks") val asks: List<List<String>> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ExchangeInfoResponse(
    @Json(name = "symbols") val symbols: List<FuturesSymbol> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FuturesSymbol(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "baseAsset") val baseAsset: String = "",
    @Json(name = "quoteAsset") val quoteAsset: String = "",
    @Json(name = "contractType") val contractType: String = "",
    @Json(name = "pricePrecision") val pricePrecision: Int = 0,
    @Json(name = "quantityPrecision") val quantityPrecision: Int = 0
)

@JsonClass(generateAdapter = true)
data class OpenInterestHistEntry(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "sumOpenInterest") val sumOpenInterest: String = "0",
    @Json(name = "sumOpenInterestValue") val sumOpenInterestValue: String = "0",
    @Json(name = "timestamp") val timestamp: Long = 0
)

@JsonClass(generateAdapter = true)
data class LongShortRatioEntry(
    @Json(name = "symbol") val symbol: String = "",
    @Json(name = "longShortRatio") val longShortRatio: String = "0",
    @Json(name = "longAccount") val longAccount: String = "0",
    @Json(name = "shortAccount") val shortAccount: String = "0",
    @Json(name = "timestamp") val timestamp: Long = 0
)

// ── WebSocket Models ─────────────────────────────────────────

data class WsTickerData(
    val symbol: String,
    val lastPrice: String,
    val priceChange: String,
    val priceChangePercent: String,
    val highPrice: String,
    val lowPrice: String,
    val volume: String,
    val quoteVolume: String
)

data class WsMarkPrice(
    val symbol: String,
    val markPrice: String,
    val indexPrice: String,
    val fundingRate: String,
    val nextFundingTime: Long
)

data class WsAggTrade(
    val symbol: String,
    val price: String,
    val qty: String,
    val time: Long,
    val isBuyerMaker: Boolean
)

data class WsLiquidation(
    val symbol: String,
    val side: String,
    val price: String,
    val qty: String,
    val time: Long
)

data class WsOrderBook(
    val bids: List<List<String>>,
    val asks: List<List<String>>
)

// ── Kline (Candle) ───────────────────────────────────────────

data class Kline(
    val openTime: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val closeTime: Long,
    val quoteVolume: Double,
    val trades: Int
)

// ── Room Entities ────────────────────────────────────────────

@Entity(tableName = "favorite_symbols")
data class FavoriteSymbol(
    @PrimaryKey val symbol: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_symbols")
data class RecentSymbol(
    @PrimaryKey val symbol: String,
    val visitedAt: Long = System.currentTimeMillis()
)

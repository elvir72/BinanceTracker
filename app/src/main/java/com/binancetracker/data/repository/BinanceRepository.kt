package com.binancetracker.data.repository

import com.binancetracker.data.api.BinanceApiService
import com.binancetracker.data.api.BinanceWebSocketManager
import com.binancetracker.data.db.SymbolDao
import com.binancetracker.data.model.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinanceRepository @Inject constructor(
    private val api: BinanceApiService,
    private val wsManager: BinanceWebSocketManager,
    private val symbolDao: SymbolDao
) {
    // ── REST ──────────────────────────────────────────────────

    suspend fun getTicker24h(symbol: String): Result<Ticker24h> = runCatching {
        api.getTicker24h(symbol)
    }

    suspend fun getAllTickers(): Result<List<Ticker24h>> = runCatching {
        api.getAllTickers()
    }

    suspend fun getMarkPrice(symbol: String): Result<MarkPriceData> = runCatching {
        api.getMarkPrice(symbol)
    }

    suspend fun getOrderBook(symbol: String, limit: Int = 20): Result<OrderBookData> = runCatching {
        api.getOrderBook(symbol, limit)
    }

    suspend fun getRecentTrades(symbol: String): Result<List<TradeEntry>> = runCatching {
        api.getRecentTrades(symbol)
    }

    suspend fun getKlines(symbol: String, interval: String, limit: Int = 100): Result<List<Kline>> = runCatching {
        api.getKlines(symbol, interval, limit).map { raw ->
            Kline(
                openTime = (raw[0] as Double).toLong(),
                open = (raw[1] as String).toDouble(),
                high = (raw[2] as String).toDouble(),
                low = (raw[3] as String).toDouble(),
                close = (raw[4] as String).toDouble(),
                volume = (raw[5] as String).toDouble(),
                closeTime = (raw[6] as Double).toLong(),
                quoteVolume = (raw[7] as String).toDouble(),
                trades = (raw[8] as Double).toInt()
            )
        }
    }

    suspend fun getOpenInterest(symbol: String): Result<OpenInterestData> = runCatching {
        api.getOpenInterest(symbol)
    }

    suspend fun getFundingRateHistory(symbol: String): Result<List<FundingRateEntry>> = runCatching {
        api.getFundingRateHistory(symbol)
    }

    suspend fun getOpenInterestHistory(symbol: String, period: String = "1h"): Result<List<OpenInterestHistEntry>> = runCatching {
        api.getOpenInterestHistory(symbol, period)
    }

    suspend fun getLongShortRatio(symbol: String, period: String = "1h"): Result<List<LongShortRatioEntry>> = runCatching {
        api.getLongShortRatio(symbol, period)
    }

    suspend fun getExchangeSymbols(): Result<List<FuturesSymbol>> = runCatching {
        api.getExchangeInfo().symbols.filter { it.status == "TRADING" }
    }

    // ── WebSocket ─────────────────────────────────────────────

    fun connectWebSocket(symbol: String) = wsManager.connect(symbol)
    fun disconnectWebSocket() = wsManager.disconnect()

    val wsTicker = wsManager.ticker
    val wsMarkPrice = wsManager.markPrice
    val wsAggTrades = wsManager.aggTrades
    val wsOrderBook = wsManager.orderBook
    val wsLiquidations = wsManager.liquidations

    // ── Room ──────────────────────────────────────────────────

    fun getFavorites(): Flow<List<FavoriteSymbol>> = symbolDao.getFavorites()
    suspend fun isFavorite(symbol: String): Boolean = symbolDao.isFavorite(symbol)
    suspend fun toggleFavorite(symbol: String) {
        if (symbolDao.isFavorite(symbol)) {
            symbolDao.removeFavorite(FavoriteSymbol(symbol))
        } else {
            symbolDao.addFavorite(FavoriteSymbol(symbol))
        }
    }

    fun getRecentSymbols(): Flow<List<RecentSymbol>> = symbolDao.getRecentSymbols()
    suspend fun addRecentSymbol(symbol: String) {
        symbolDao.addRecent(RecentSymbol(symbol, System.currentTimeMillis()))
        symbolDao.pruneRecent()
    }
}

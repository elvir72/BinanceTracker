package com.binancetracker.data.api

import com.binancetracker.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApiService {

    // Ticker 24h stats
    @GET("fapi/v1/ticker/24hr")
    suspend fun getTicker24h(@Query("symbol") symbol: String): Ticker24h

    // All tickers for dashboard
    @GET("fapi/v1/ticker/24hr")
    suspend fun getAllTickers(): List<Ticker24h>

    // Mark price + funding info
    @GET("fapi/v1/premiumIndex")
    suspend fun getMarkPrice(@Query("symbol") symbol: String): MarkPriceData

    // Order book depth
    @GET("fapi/v1/depth")
    suspend fun getOrderBook(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int = 20
    ): OrderBookData

    // Recent trades
    @GET("fapi/v1/trades")
    suspend fun getRecentTrades(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int = 50
    ): List<TradeEntry>

    // Klines (OHLCV)
    @GET("fapi/v1/klines")
    suspend fun getKlines(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("limit") limit: Int = 100
    ): List<List<Any>>

    // Open interest
    @GET("fapi/v1/openInterest")
    suspend fun getOpenInterest(@Query("symbol") symbol: String): OpenInterestData

    // Funding rate history
    @GET("fapi/v1/fundingRate")
    suspend fun getFundingRateHistory(
        @Query("symbol") symbol: String,
        @Query("limit") limit: Int = 10
    ): List<FundingRateEntry>

    // Open interest history (statistics endpoint)
    @GET("futures/data/openInterestHist")
    suspend fun getOpenInterestHistory(
        @Query("symbol") symbol: String,
        @Query("period") period: String = "1h",
        @Query("limit") limit: Int = 24
    ): List<OpenInterestHistEntry>

    // Long/Short ratio
    @GET("futures/data/globalLongShortAccountRatio")
    suspend fun getLongShortRatio(
        @Query("symbol") symbol: String,
        @Query("period") period: String = "1h",
        @Query("limit") limit: Int = 24
    ): List<LongShortRatioEntry>

    // Exchange info (all symbols)
    @GET("fapi/v1/exchangeInfo")
    suspend fun getExchangeInfo(): ExchangeInfoResponse
}

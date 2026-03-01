package com.binancetracker.data.api

import android.util.Log
import com.binancetracker.data.model.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinanceWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttp3Client
) {
    private val TAG = "BinanceWS"

    private val _ticker = MutableSharedFlow<WsTickerData>(replay = 1)
    val ticker: SharedFlow<WsTickerData> = _ticker

    private val _markPrice = MutableSharedFlow<WsMarkPrice>(replay = 1)
    val markPrice: SharedFlow<WsMarkPrice> = _markPrice

    private val _aggTrades = MutableSharedFlow<WsAggTrade>(replay = 0)
    val aggTrades: SharedFlow<WsAggTrade> = _aggTrades

    private val _orderBook = MutableSharedFlow<WsOrderBook>(replay = 1)
    val orderBook: SharedFlow<WsOrderBook> = _orderBook

    private val _liquidations = MutableSharedFlow<WsLiquidation>(replay = 0)
    val liquidations: SharedFlow<WsLiquidation> = _liquidations

    private var combinedSocket: WebSocket? = null
    private var currentSymbol: String = ""

    private val client = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    fun connect(symbol: String) {
        if (currentSymbol == symbol && combinedSocket != null) return
        disconnect()
        currentSymbol = symbol
        val sym = symbol.lowercase()

        // Combined stream
        val streams = listOf(
            "$sym@ticker",
            "$sym@markPrice@1s",
            "$sym@aggTrade",
            "$sym@depth20@500ms",
            "$sym@forceOrder"
        ).joinToString("/")

        val url = "wss://fstream.binance.com/stream?streams=$streams"
        Log.d(TAG, "Connecting to: $url")

        val request = Request.Builder().url(url).build()
        combinedSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val wrapper = JSONObject(text)
                    val streamName = wrapper.optString("stream")
                    val data = wrapper.optJSONObject("data") ?: return

                    when {
                        streamName.endsWith("@ticker") -> parseTicker(data)
                        streamName.endsWith("@markPrice@1s") -> parseMarkPrice(data)
                        streamName.endsWith("@aggTrade") -> parseAggTrade(data)
                        streamName.contains("@depth") -> parseDepth(data)
                        streamName.endsWith("@forceOrder") -> parseLiquidation(data)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Parse error: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WS failure: ${t.message}")
                // Reconnect after delay
                Thread.sleep(3000)
                connect(symbol)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WS closed: $reason")
            }
        })
    }

    fun disconnect() {
        combinedSocket?.close(1000, "User left")
        combinedSocket = null
        currentSymbol = ""
    }

    private fun parseTicker(d: JSONObject) {
        _ticker.tryEmit(
            WsTickerData(
                symbol = d.optString("s"),
                lastPrice = d.optString("c"),
                priceChange = d.optString("p"),
                priceChangePercent = d.optString("P"),
                highPrice = d.optString("h"),
                lowPrice = d.optString("l"),
                volume = d.optString("v"),
                quoteVolume = d.optString("q")
            )
        )
    }

    private fun parseMarkPrice(d: JSONObject) {
        _markPrice.tryEmit(
            WsMarkPrice(
                symbol = d.optString("s"),
                markPrice = d.optString("p"),
                indexPrice = d.optString("i"),
                fundingRate = d.optString("r"),
                nextFundingTime = d.optLong("T")
            )
        )
    }

    private fun parseAggTrade(d: JSONObject) {
        _aggTrades.tryEmit(
            WsAggTrade(
                symbol = d.optString("s"),
                price = d.optString("p"),
                qty = d.optString("q"),
                time = d.optLong("T"),
                isBuyerMaker = d.optBoolean("m")
            )
        )
    }

    private fun parseDepth(d: JSONObject) {
        val bids = mutableListOf<List<String>>()
        val asks = mutableListOf<List<String>>()
        val bidsArr = d.optJSONArray("b")
        val asksArr = d.optJSONArray("a")
        bidsArr?.let { for (i in 0 until it.length()) bids.add(listOf(it.getJSONArray(i).getString(0), it.getJSONArray(i).getString(1))) }
        asksArr?.let { for (i in 0 until it.length()) asks.add(listOf(it.getJSONArray(i).getString(0), it.getJSONArray(i).getString(1))) }
        _orderBook.tryEmit(WsOrderBook(bids, asks))
    }

    private fun parseLiquidation(d: JSONObject) {
        val o = d.optJSONObject("o") ?: return
        _liquidations.tryEmit(
            WsLiquidation(
                symbol = o.optString("s"),
                side = o.optString("S"),
                price = o.optString("ap"),
                qty = o.optString("q"),
                time = o.optLong("T")
            )
        )
    }
}

// Marker class for injection
class OkHttp3Client

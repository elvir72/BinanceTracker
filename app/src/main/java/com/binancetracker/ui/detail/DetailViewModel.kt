package com.binancetracker.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binancetracker.data.model.*
import com.binancetracker.data.repository.BinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DetailUiState(
    val symbol: String = "",
    val ticker: Ticker24h? = null,
    val markPrice: MarkPriceData? = null,
    val openInterest: OpenInterestData? = null,
    val orderBook: OrderBookData? = null,
    val recentTrades: List<TradeEntry> = emptyList(),
    val klines: List<Kline> = emptyList(),
    val klineInterval: String = "15m",
    val fundingHistory: List<FundingRateEntry> = emptyList(),
    val oiHistory: List<OpenInterestHistEntry> = emptyList(),
    val longShortRatio: List<LongShortRatioEntry> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Live WS overrides
    val liveTicker: WsTickerData? = null,
    val liveMarkPrice: WsMarkPrice? = null,
    val liveOrderBook: WsOrderBook? = null,
    val liveTrades: List<WsAggTrade> = emptyList(),
    val liveLiquidations: List<WsLiquidation> = emptyList(),
    val wsConnected: Boolean = false
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: BinanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    private val _uiState = MutableStateFlow(DetailUiState(symbol = symbol))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        loadAll()
        startWebSocket()
        startRestPolling()
    }

    private fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            coroutineScope {
                launch { loadTicker() }
                launch { loadMarkPrice() }
                launch { loadOpenInterest() }
                launch { loadOrderBook() }
                launch { loadTrades() }
                launch { loadKlines(uiState.value.klineInterval) }
                launch { loadFundingHistory() }
                launch { loadOiHistory() }
                launch { loadLongShort() }
                launch { checkFavorite() }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun startRestPolling() {
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(5_000)
                launch { loadTicker() }
                launch { loadMarkPrice() }
                launch { loadOpenInterest() }
                launch { loadOrderBook() }
                launch { loadTrades() }
            }
        }
    }

    private fun startWebSocket() {
        repository.connectWebSocket(symbol)
        _uiState.update { it.copy(wsConnected = true) }

        viewModelScope.launch {
            repository.wsTicker.collect { ws ->
                _uiState.update { it.copy(liveTicker = ws) }
            }
        }
        viewModelScope.launch {
            repository.wsMarkPrice.collect { ws ->
                _uiState.update { it.copy(liveMarkPrice = ws) }
            }
        }
        viewModelScope.launch {
            repository.wsOrderBook.collect { ws ->
                _uiState.update { it.copy(liveOrderBook = ws) }
            }
        }
        viewModelScope.launch {
            repository.wsAggTrades.collect { trade ->
                _uiState.update { state ->
                    val updated = (listOf(trade) + state.liveTrades).take(50)
                    state.copy(liveTrades = updated)
                }
            }
        }
        viewModelScope.launch {
            repository.wsLiquidations.collect { liq ->
                _uiState.update { state ->
                    val updated = (listOf(liq) + state.liveLiquidations).take(20)
                    state.copy(liveLiquidations = updated)
                }
            }
        }
    }

    fun changeInterval(interval: String) {
        _uiState.update { it.copy(klineInterval = interval) }
        viewModelScope.launch { loadKlines(interval) }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            repository.toggleFavorite(symbol)
            checkFavorite()
        }
    }

    fun refresh() = loadAll()

    private suspend fun loadTicker() {
        repository.getTicker24h(symbol).onSuccess { t ->
            _uiState.update { it.copy(ticker = t) }
        }
    }

    private suspend fun loadMarkPrice() {
        repository.getMarkPrice(symbol).onSuccess { m ->
            _uiState.update { it.copy(markPrice = m) }
        }
    }

    private suspend fun loadOpenInterest() {
        repository.getOpenInterest(symbol).onSuccess { oi ->
            _uiState.update { it.copy(openInterest = oi) }
        }
    }

    private suspend fun loadOrderBook() {
        repository.getOrderBook(symbol).onSuccess { ob ->
            _uiState.update { it.copy(orderBook = ob) }
        }
    }

    private suspend fun loadTrades() {
        repository.getRecentTrades(symbol).onSuccess { trades ->
            _uiState.update { it.copy(recentTrades = trades) }
        }
    }

    private suspend fun loadKlines(interval: String) {
        repository.getKlines(symbol, interval).onSuccess { k ->
            _uiState.update { it.copy(klines = k) }
        }
    }

    private suspend fun loadFundingHistory() {
        repository.getFundingRateHistory(symbol).onSuccess { f ->
            _uiState.update { it.copy(fundingHistory = f) }
        }
    }

    private suspend fun loadOiHistory() {
        repository.getOpenInterestHistory(symbol).onSuccess { oi ->
            _uiState.update { it.copy(oiHistory = oi) }
        }
    }

    private suspend fun loadLongShort() {
        repository.getLongShortRatio(symbol).onSuccess { ls ->
            _uiState.update { it.copy(longShortRatio = ls) }
        }
    }

    private suspend fun checkFavorite() {
        val fav = repository.isFavorite(symbol)
        _uiState.update { it.copy(isFavorite = fav) }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectWebSocket()
        refreshJob?.cancel()
    }
}

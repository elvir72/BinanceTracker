package com.binancetracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binancetracker.data.model.FavoriteSymbol
import com.binancetracker.data.model.RecentSymbol
import com.binancetracker.data.model.Ticker24h
import com.binancetracker.data.repository.BinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val searchQuery: String = "",
    val allSymbols: List<String> = emptyList(),
    val filteredSymbols: List<String> = emptyList(),
    val tickers: Map<String, Ticker24h> = emptyMap(),
    val favorites: List<FavoriteSymbol> = emptyList(),
    val recentSymbols: List<RecentSymbol> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: BinanceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    private var tickerJob: Job? = null

    init {
        loadSymbols()
        observeFavorites()
        observeRecent()
        startTickerPolling()
    }

    private fun loadSymbols() {
        viewModelScope.launch {
            repository.getExchangeSymbols().onSuccess { symbols ->
                val names = symbols.filter { it.contractType == "PERPETUAL" }
                    .map { it.symbol }
                    .sorted()
                _state.update { it.copy(allSymbols = names, filteredSymbols = names, isLoading = false) }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favs ->
                _state.update { it.copy(favorites = favs) }
            }
        }
    }

    private fun observeRecent() {
        viewModelScope.launch {
            repository.getRecentSymbols().collect { recents ->
                _state.update { it.copy(recentSymbols = recents) }
            }
        }
    }

    private fun startTickerPolling() {
        tickerJob = viewModelScope.launch {
            while (isActive) {
                repository.getAllTickers().onSuccess { list ->
                    val map = list.associateBy { it.symbol }
                    _state.update { it.copy(tickers = map) }
                }
                delay(5_000)
            }
        }
    }

    fun onSearchChanged(query: String) {
        _state.update { s ->
            val filtered = if (query.isBlank()) s.allSymbols
            else s.allSymbols.filter { it.contains(query.uppercase()) }
            s.copy(searchQuery = query, filteredSymbols = filtered)
        }
    }

    fun onSymbolSelected(symbol: String) {
        viewModelScope.launch {
            repository.addRecentSymbol(symbol)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tickerJob?.cancel()
    }
}

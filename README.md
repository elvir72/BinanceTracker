# 📱 Binance Tracker – Android App

Aplikacja do śledzenia danych Binance Futures w czasie rzeczywistym.

## 🔧 Wymagania

- Android Studio Ladybug (2024.2.1) lub nowszy
- JDK 17+
- Android SDK 35
- Min Android 8.0 (API 26)
- Połączenie internetowe (Binance API jest publiczne – nie wymaga klucza API)

---

## 🚀 Szybki start

1. **Otwórz projekt w Android Studio:**
   ```
   File → Open → wybierz folder BinanceTracker
   ```

2. **Poczekaj na sync Gradle** (pierwsze uruchomienie pobierze zależności ~500MB)

3. **Uruchom na urządzeniu lub emulatorze:**
   ```
   Run → Run 'app' (Shift+F10)
   ```

---

## 📂 Struktura projektu

```
app/src/main/java/com/binancetracker/
│
├── data/
│   ├── api/
│   │   ├── BinanceApiService.kt     # REST endpoints Binance Futures
│   │   └── BinanceWebSocketManager.kt # WS combined stream manager
│   ├── model/
│   │   └── Models.kt                # Wszystkie data classy
│   ├── db/
│   │   └── AppDatabase.kt           # Room DB (ulubione, ostatnie)
│   └── repository/
│       └── BinanceRepository.kt     # Agreguje REST + WS + DB
│
├── di/
│   └── AppModule.kt                 # Hilt DI
│
├── ui/
│   ├── theme/
│   │   └── Theme.kt                 # Binance dark theme
│   ├── dashboard/
│   │   ├── DashboardScreen.kt       # Lista par + wyszukiwarka
│   │   └── DashboardViewModel.kt
│   └── detail/
│       ├── DetailScreen.kt          # Ekran szczegółów pary
│       ├── DetailViewModel.kt
│       └── components/
│           ├── PriceHeaderCard.kt   # Cena + Mark/Index + Funding
│           ├── CandlestickChart.kt  # Wykres świecowy (Canvas)
│           ├── OrderBookCard.kt     # Order book z depth bars
│           ├── TradeTapeCard.kt     # Live tape transakcji
│           ├── LiquidationsCard.kt  # Likwidacje live
│           ├── FundingRateCard.kt   # Historia funding rate
│           └── OiLongShortCards.kt  # OI + Long/Short ratio
│
└── MainActivity.kt                  # Entry point + Navigation
```

---

## 📊 Dane wyświetlane (na ekranie szczegółów)

### 🔴 Header (Live via WebSocket)
- Last Price z animowanym kolorem
- Zmiana procentowa 24h
- Mark Price / Index Price
- Funding Rate (aktualny) + countdown do następnego
- High/Low 24h
- Wolumen (coin + USDT)
- Open Interest

### 🕯️ Wykres świecowy
- Timeframe: 1m, 5m, 15m, 1h, 4h, 1d, 1w
- Świece OHLC rysowane na Canvas
- Wolumen pod wykresem
- Pinch-to-zoom

### 📖 Order Book (Live)
- Top 10 bid/ask
- Depth bars wizualizujące wielkość
- Spread w punktach i %

### 💹 Trade Tape (Live WebSocket)
- Ostatnie 30 transakcji w czasie rzeczywistym
- Kolor: zielony = buy, czerwony = sell

### 💥 Liquidations (Live WebSocket)
- Likwidacje pozycji live

### 📈 Open Interest History
- Historia OI (1h/24h)
- Mini wykres + tabela

### ↕️ Long/Short Ratio
- Aktualna proporcja longs vs shorts
- Wizualny pasek + mini wykres historyczny

### 💸 Funding Rate History
- Ostatnie 8 płatności funding
- Mini wykres historyczny

---

## ⚙️ Technologie

| Warstwa | Technologia |
|---------|-------------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Network (REST) | Retrofit + OkHttp + Moshi |
| Network (WS) | OkHttp WebSocket |
| DB | Room |
| Async | Coroutines + Flow |
| Charts | Custom Canvas |

---

## 📡 API Endpoints używane

### REST (Binance Futures)
- `GET /fapi/v1/ticker/24hr` – Ticker 24h
- `GET /fapi/v1/premiumIndex` – Mark price + Funding
- `GET /fapi/v1/depth` – Order book
- `GET /fapi/v1/trades` – Ostatnie transakcje
- `GET /fapi/v1/klines` – OHLCV świece
- `GET /fapi/v1/openInterest` – Open Interest
- `GET /fapi/v1/fundingRate` – Historia funding
- `GET /futures/data/openInterestHist` – OI historia
- `GET /futures/data/globalLongShortAccountRatio` – Long/Short ratio
- `GET /fapi/v1/exchangeInfo` – Lista symboli

### WebSocket (Binance Futures Stream)
- `<symbol>@ticker` – Ticker live
- `<symbol>@markPrice@1s` – Mark price co sekundę
- `<symbol>@aggTrade` – Transakcje live
- `<symbol>@depth20@500ms` – Order book live
- `<symbol>@forceOrder` – Likwidacje live

---

## 🔮 Możliwe rozszerzenia

- [ ] Price alerts (powiadomienia push)
- [ ] Portfolio tracker
- [ ] TradingView webview integration
- [ ] Więcej wskaźników technicznych (RSI, MACD)
- [ ] Android Widget
- [ ] Eksport danych do CSV
- [ ] Multiple accounts / API key management

---

## ⚠️ Uwagi

- Aplikacja używa **publicznego API Binance** – nie wymaga klucza API
- WebSocket automatycznie reconnektuje przy utracie połączenia
- Dane REST odświeżane co 5 sekund jako backup dla WS
- Aplikacja działa tylko z **Futures Perpetual** (endpoint fapi)

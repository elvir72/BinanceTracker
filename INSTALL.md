# 🚀 Jak uzyskać gotowy APK przez GitHub Actions

## Krok 1 – Załóż konto GitHub (jeśli nie masz)
Wejdź na https://github.com/signup i zarejestruj się za darmo.

## Krok 2 – Stwórz nowe repozytorium
1. Kliknij **"+"** w prawym górnym rogu → **"New repository"**
2. Nazwa: `BinanceTracker`
3. Ustaw na **Public** (wymagane dla darmowych Actions)
4. NIE zaznaczaj "Add README"
5. Kliknij **"Create repository"**

## Krok 3 – Wgraj pliki projektu
Na stronie nowego repo kliknij **"uploading an existing file"**
Przeciągnij CAŁY rozpakowany folder BinanceTracker.
Kliknij **"Commit changes"**.

## Krok 4 – Uruchom build
1. Wejdź w zakładkę **Actions**
2. Kliknij workflow **"Build APK"**
3. Kliknij **"Run workflow"** → **"Run workflow"** (zielony przycisk)
4. Poczekaj ~5-10 minut

## Krok 5 – Pobierz APK
Po zakończeniu builda (zielony ptaszek ✅):
1. Kliknij w dany run
2. Przewiń na dół do sekcji **Artifacts**
3. Pobierz **BinanceTracker-debug**
4. Rozpakuj ZIP → masz `app-debug.apk`

## Krok 6 – Zainstaluj na telefonie
1. Wyślij APK na telefon (przez kabel, email, Drive, Telegram)
2. Na telefonie: **Ustawienia → Bezpieczeństwo → Nieznane źródła → Włącz**
   (lub przy instalacji telefon sam zapyta)
3. Otwórz plik APK → Zainstaluj

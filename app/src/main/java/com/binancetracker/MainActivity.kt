package com.binancetracker

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.binancetracker.ui.dashboard.DashboardScreen
import com.binancetracker.ui.detail.DetailScreen
import com.binancetracker.ui.theme.BinanceTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BinanceApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BinanceTrackerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                onSymbolClick = { symbol ->
                    navController.navigate("detail/$symbol")
                }
            )
        }
        composable(
            route = "detail/{symbol}",
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: return@composable
            DetailScreen(
                symbol = symbol,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

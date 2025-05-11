package com.willkopec.fetchexercise.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.willkopec.fetchexercise.data.datastore.DataStore
import com.willkopec.fetchexercise.ui.screens.CartScreen
import com.willkopec.fetchexercise.ui.screens.HomeScreen
import com.willkopec.fetchexercise.ui.screens.MainScreen
import com.willkopec.fetchexercise.ui.screens.SettingsScreen
import com.willkopec.fetchexercise.ui.viewmodels.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppContainer(
    viewModel: MainViewModel = koinViewModel(),
    dataStore: DataStore
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("list") {
            MainScreen(
                viewModel = viewModel,
                onBackPressed = { navController.popBackStack() },
                navController = navController
            )
        }
        composable("settings") {
            SettingsScreen(dataStore, onBackPressed = { navController.popBackStack() })
        }
        composable("mycart") {
            CartScreen(viewModel, onBackClick = { navController.popBackStack() })
        }
    }
}


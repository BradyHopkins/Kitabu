package com.example.kitabu.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kitabu.ui.screens.CatalogueScreen
import com.example.kitabu.ui.screens.RentalsScreen
import com.example.kitabu.viewmodel.KitabuViewModel
import com.example.kitabu.viewmodel.KitabuViewModelFactory

private object KitabuRoutes {
    const val CATALOGUE = "catalogue"
    const val RENTALS = "rentals"
}

@Composable
fun KitabuNavigation(
    viewModelFactory: KitabuViewModelFactory
) {
    val navController = rememberNavController()

    // Use one shared ViewModel across both screens.
    val kitabuViewModel: KitabuViewModel = viewModel(
        factory = viewModelFactory
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == KitabuRoutes.CATALOGUE,
                    onClick = {
                        navController.navigate(KitabuRoutes.CATALOGUE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("📚") },
                    label = { Text("Catalogue") }
                )

                NavigationBarItem(
                    selected = currentRoute == KitabuRoutes.RENTALS,
                    onClick = {
                        navController.navigate(KitabuRoutes.RENTALS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Text("🗓️") },
                    label = { Text("My Rentals") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = KitabuRoutes.CATALOGUE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(KitabuRoutes.CATALOGUE) {
                CatalogueScreen(
                    viewModel = kitabuViewModel
                )
            }

            composable(KitabuRoutes.RENTALS) {
                RentalsScreen(
                    viewModel = kitabuViewModel
                )
            }
        }
    }
}
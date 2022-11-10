package no.tepohi.projectepta

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.screens.DeparturesScreen
import no.tepohi.projectepta.ui.screens.SettingsScreen
import no.tepohi.projectepta.ui.screens.TravelScreen
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

/**
 * Determines items to be included in navbar
 */
sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen_route: String){

    object Departures : BottomNavItem("Departures", Icons.Filled.TableChart,"departures_route")
    object Travel: BottomNavItem("Travel", Icons.Filled.Map, "travel_route")
    object Settings: BottomNavItem("Settings", Icons.Filled.Settings,"settings_route")
}

/**
 * Creates navbar from bottomNavItem() and composable functions
 */
@Composable
fun CustomBottomNavigation(
    navController: NavController,
) {

    val items = listOf(
        BottomNavItem.Travel,
        BottomNavItem.Departures,
        BottomNavItem.Settings
    )

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.Red
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, fontSize = 9.sp) },
                selectedContentColor = MaterialTheme.colors.onPrimary,
                unselectedContentColor = MaterialTheme.colors.onPrimary.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    if (currentRoute != item.screen_route) {
                        navController.navigate(item.screen_route) {

                            navController.graph.startDestinationRoute?.let { screen_route ->
                                popUpTo(screen_route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * Creates navigation capabilities for BottomNavigation()
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {
    NavHost(navController, startDestination = BottomNavItem.Travel.screen_route) {
        composable(BottomNavItem.Travel.screen_route) {
            TravelScreen(
                enturViewModel = enturViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel,
            )
        }
        composable(BottomNavItem.Departures.screen_route) {
            DeparturesScreen(
                enturViewModel = enturViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel,
            )
        }
        composable(BottomNavItem.Settings.screen_route) {
            SettingsScreen(
                enturViewModel = enturViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel,
            )
        }
    }
}
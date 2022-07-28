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
import no.tepohi.projectepta.ui.viewmodels.MainActivityViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.screens.DeparturesScreen
import no.tepohi.projectepta.ui.screens.TravelScreen
import no.tepohi.projectepta.ui.screens.TripsScreen
import no.tepohi.projectepta.ui.screens.MapScreen

/**
 * Determines items to be included in navbar
 */
sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen_route: String){

    object Trips : BottomNavItem("Trips", Icons.Filled.Search,"journey_route")
    object Departures : BottomNavItem("Departures", Icons.Filled.TableChart,"departures_route")
    object Map: BottomNavItem("Map", Icons.Filled.Map,"map_route")
//    object Settings: BottomNavItem("Settings", Icons.Filled.Settings,"settings_route")
    object Map_New: BottomNavItem("Map_new", Icons.Filled.Map, "map_new_route")
}

/**
 * Creates navbar from bottomNavItem() and composable functions
 */
@Composable
fun CustomBottomNavigation(
    navController: NavController,
) {

    val items = listOf(
        BottomNavItem.Map_New,
        BottomNavItem.Departures,
//        BottomNavItem.Map,
//        BottomNavItem.Trips,
//        BottomNavItem.Settings
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
    mainActivityViewModel: MainActivityViewModel,
    searchViewModel: SearchViewModel,
) {
    NavHost(navController, startDestination = BottomNavItem.Departures.screen_route) {
        composable(BottomNavItem.Map_New.screen_route) {
            TravelScreen(
                mainActivityViewModel = mainActivityViewModel,
                searchViewModel = searchViewModel,
            )
        }
        composable(BottomNavItem.Trips.screen_route) {
            TripsScreen(
                navController = navController,
                viewModel = mainActivityViewModel
            )
        }
        composable(BottomNavItem.Departures.screen_route) {
            DeparturesScreen(
                mainActivityViewModel = mainActivityViewModel,
                searchViewModel = searchViewModel,
            )
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(mainActivityViewModel)
        }
    }
}
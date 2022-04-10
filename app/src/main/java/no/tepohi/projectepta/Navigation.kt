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
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.data.MainActivityViewModel
import no.tepohi.projectepta.ui.screens.DeparturesScreen
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
}

/**
 * Creates navbar from bottomNavItem() and composable functions
 */
@Composable
fun CustomBottomNavigation(
    navController: NavController,
) {

    val items = listOf(
        BottomNavItem.Trips,
        BottomNavItem.Departures,
        BottomNavItem.Map,
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
    viewModel: MainActivityViewModel,
//    stops: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>
) {
    NavHost(navController, startDestination = BottomNavItem.Trips.screen_route) {
        composable(BottomNavItem.Trips.screen_route) {
            TripsScreen(navController, viewModel)
        }
        composable(BottomNavItem.Departures.screen_route) {
            DeparturesScreen()
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen(viewModel)
        }
    }
}
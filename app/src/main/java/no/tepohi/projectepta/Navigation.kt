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
import no.tepohi.example.StopsQuery
import no.tepohi.projectepta.ui.screens.HomeScreen
import no.tepohi.projectepta.ui.screens.MapScreen

/**
 * Determines items to be included in navbar
 */
sealed class BottomNavItem(var title: String, var icon: ImageVector, var screen_route: String){

    object Home : BottomNavItem("Home", Icons.Filled.Search,"home_route")
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
        BottomNavItem.Home,
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
    stops: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>
) {
    NavHost(navController, startDestination = BottomNavItem.Map.screen_route) {
        composable(BottomNavItem.Home.screen_route) {
            HomeScreen(navController, stops)
        }
        composable(BottomNavItem.Map.screen_route) {
            MapScreen()
        }
    }
}
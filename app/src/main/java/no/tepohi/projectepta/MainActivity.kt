package no.tepohi.projectepta

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.data.MainActivityViewModel
import no.tepohi.projectepta.ui.theme.EptaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var stops: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>

        viewModel.loadStops().observe(this) {
            stops = it ?: emptyList()

            setContent {

                val navController = rememberNavController()
                EptaTheme {

                    Scaffold(
                        bottomBar = {
                            CustomBottomNavigation(
                                navController = navController,
                            )
                        }
                    ) { padding ->
                        Box(modifier = Modifier.padding(padding)) {
                            NavigationGraph(
                                navController = navController,
                                stops = stops
                            )
                        }
                    }
                }
            }

        }

    }


}
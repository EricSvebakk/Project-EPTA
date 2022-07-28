package no.tepohi.projectepta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import no.tepohi.projectepta.ui.viewmodels.MainActivityViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.theme.EptaTheme

class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        searchViewModel.mavm = mainActivityViewModel
        mainActivityViewModel.loadStops()

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
                            mainActivityViewModel = mainActivityViewModel,
                            searchViewModel = searchViewModel,
    //                                stops = stops,
                        )
                    }
                }
            }
        }

    }


}
package no.tepohi.projectepta

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.EptaTheme
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val enturViewModel: EnturViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enturViewModel.loadStops()

        setContent {

            val acp by settingsViewModel.appColorPalette.observeAsState()

            val navController = rememberNavController()
            EptaTheme(
                colorPalette = acp ?: Constants.THEME_SYSTEM
            ) {

                Scaffold(
                    bottomBar = {
                        CustomBottomNavigation(navController = navController)
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        NavigationGraph(
                            navController = navController,
                            enturViewModel = enturViewModel,
                            searchViewModel = searchViewModel,
                            settingsViewModel = settingsViewModel,
                        )
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

//        this.deleteSharedPreferences("EptaPreferences")

        val sp = this.getSharedPreferences("EptaPreferences", MODE_PRIVATE)

        val data = sp.getStringSet("favouriteStops", emptySet())!!.map {

            val temp = it.split(",")

            StopPlacesByBoundaryQuery.StopPlacesByBbox(
                temp[0],
                temp[1],
                temp[2].toDouble(),
                temp[3].toDouble()
            )
        }

        Log.d("SharedPref resume", data.toString())

        settingsViewModel.favouriteStops.postValue(data)
        settingsViewModel.appColorPalette.postValue(sp.getString("appColorPalette", Constants.THEME_SYSTEM))
    }

    override fun onPause() {
        super.onPause()

        val sp = this.getSharedPreferences("EptaPreferences", MODE_PRIVATE)
        val edit = sp.edit()


        val data = mutableSetOf<String>()
        settingsViewModel.favouriteStops.value?.forEach { stop ->
            data.add("${stop?.name},${stop?.id},${stop?.latitude},${stop?.longitude}")
        }

        Log.d("SharedPref pause", data.toString())

        edit.putStringSet("favouriteStops", data)
        edit.putString("appColorPalette", settingsViewModel.appColorPalette.value ?: Constants.THEME_SYSTEM)

        edit.apply()
    }


}
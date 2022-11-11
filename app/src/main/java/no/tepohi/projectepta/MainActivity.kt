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
import no.tepohi.projectepta.ui.sources.StopData
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.theme.EptaTheme
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.lang.NumberFormatException

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


        settingsViewModel.appColorPalette.postValue(sp.getString("appColorPalette", Constants.THEME_SYSTEM))

        val data = mutableListOf<StopData>()
        sp.getStringSet("favouriteStops", emptySet())?.forEach { stop ->

            val temp = stop.split(",")

            try {
                data.add(
                    StopData(
                        temp[0],
                        temp[1],
                        temp[2].toDouble(),
                        temp[3].toDouble()
                    )
                )
            }
            catch (err: NumberFormatException) {
                Log.e("sharedPref", temp.toString())
            }
        }

        settingsViewModel.favouriteStops.postValue(data)
    }

    override fun onPause() {
        super.onPause()

        val sp = this.getSharedPreferences("EptaPreferences", MODE_PRIVATE)
        val edit = sp.edit()

        val data = mutableSetOf<String>()
        settingsViewModel.favouriteStops.value?.forEach { stop ->
            data.add("${stop.name},${stop.id},${stop.latitude},${stop.longitude}")
        }

        edit.putStringSet("favouriteStops", data)
        edit.putString("appColorPalette", settingsViewModel.appColorPalette.value ?: Constants.THEME_SYSTEM)

        edit.apply()
    }


}
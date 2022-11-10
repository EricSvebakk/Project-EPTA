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
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.theme.EptaTheme
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

        val sp = this.getSharedPreferences("EptaPreferences", MODE_PRIVATE)

        settingsViewModel.appColorPalette.postValue(sp.getString("appColorPalette", Constants.THEME_SYSTEM))

//        appViewModel.mapSettingsShowPollution.postValue(sp.getBoolean("mapSettingsShowPollution", false))
//        appViewModel.mapSettingsShowAltitude.postValue(sp.getBoolean("mapSettingsShowAltitude", false))
//        appViewModel.mapSettingsShowTraffic.postValue(sp.getBoolean("mapSettingsShowTraffic", false))
//        appViewModel.mapSettingsShowReturn.postValue(sp.getBoolean("mapSettingsShowReturn", false))
//        appViewModel.mapSettingsDistance.postValue(sp.getFloat("mapSettingsDistance", 0f))
    }

    override fun onPause() {
        super.onPause()

        val sp = this.getSharedPreferences("EptaPreferences", MODE_PRIVATE)
        val edit = sp.edit()

        edit.putString("appColorPalette", settingsViewModel.appColorPalette.value ?: Constants.THEME_SYSTEM)

//        edit.putBoolean("mapSettingsShowPollution", appViewModel.mapSettingsShowPollution.value ?: false)
//        edit.putBoolean("mapSettingsShowAltitude", appViewModel.mapSettingsShowAltitude.value ?: false)
//        edit.putBoolean("mapSettingsShowTraffic", appViewModel.mapSettingsShowTraffic.value ?: false)
//        edit.putBoolean("mapSettingsShowReturn", appViewModel.mapSettingsShowReturn.value ?: false)
//        edit.putFloat("mapSettingsDistance", appViewModel.mapSettingsDistance.value ?: 0f)

        edit.apply()
    }


}
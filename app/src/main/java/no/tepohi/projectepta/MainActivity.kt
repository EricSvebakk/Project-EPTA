package no.tepohi.projectepta

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.EptaTheme
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel


//import java.util.jar.Manifest

class MainActivity : ComponentActivity() {

    private val enturViewModel: EnturViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    private var currentLocation: StopPlacesByBoundaryQuery.StopPlacesByBbox? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (hasPermission == PERMISSION_GRANTED) {
            Log.d("LOCATION tag", "ALREADY GRANTED")
            getCurrentLocation()
        }
        else {
            Log.d("LOCATION tag", "NOT GRANTED")

            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        Log.d("LOCATION tag", "fine location granted.")
                        getCurrentLocation()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        Log.d("LOCATION tag", "coarse location granted.")
                        getCurrentLocation()
                    }
                    else -> {
                        Log.d("LOCATION tag", "no location granted.")
                    }
                }
            }

            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }

        enturViewModel.loadStops()

        setContent {

            val favStops by settingsViewModel.favouriteStops.observeAsState()
            val currentPosition by searchViewModel.currentLocation.observeAsState()
            val allStops by enturViewModel.stopsData.observeAsState()


            if (favStops != null && currentLocation != currentPosition) {
                val lat = currentLocation?.latitude ?: 0.0
                val lon = currentLocation?.longitude ?: 0.0

                val result = StopPlacesByBoundaryQuery.StopPlacesByBbox(
                    "Your position",
                    "boobs",
                    lat,
                    lon,
                )

                val copyFavs = favStops!!.toMutableList()

                copyFavs.remove(currentPosition)
                copyFavs.add(result)
                settingsViewModel.favouriteStops.postValue(copyFavs)
                searchViewModel.currentLocation.postValue(currentLocation)

                if (allStops != null) {
                    val copyAll = allStops!!.toMutableList()
                    copyAll.remove(currentPosition)
                    copyAll.add(result)
                    enturViewModel.stopsData.postValue(copyAll)
                }

                Log.d("LOCATION add SUCCESS tag", "location added! ")
            }
            else {
                Log.d("LOCATION add ERROR tag", "unable to add position to map")
            }

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

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {

        Log.d("LOCATION tag", "GETTING POSITION.")

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(p0: Location) {}
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                100F,
                gpsLocationListener
            )
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).let { location ->

                val lat = location?.latitude ?: 0.0
                val lon = location?.longitude ?: 0.0

                Log.d("LOCATION result tag", "$lat $lon")

                currentLocation = StopPlacesByBoundaryQuery.StopPlacesByBbox(
                    "Your position",
                    "boobs",
                    lat,
                    lon,
                )

//                val stops = settingsViewModel.favouriteStops.value
//
//                if (stops != null) {
//                    val result = StopPlacesByBoundaryQuery.StopPlacesByBbox(
//                        "Your position",
//                        "boobs",
//                        lat,
//                        lon,
//                    )
//
//                    val copy = stops.toMutableList()
//
//                    copy.add(result)
//                    settingsViewModel.favouriteStops.postValue(copy)
//
//
//
//                    Log.d("LOCATION add SUCCESS tag", "unable to add position to map")
//                }
//                else {
//                    Log.d("LOCATION add ERROR tag", "unable to add position to map")
//                }


            }
        }

        else {
            Log.e("LOCATION ERROR", "GPS UNAVAILABLE")
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
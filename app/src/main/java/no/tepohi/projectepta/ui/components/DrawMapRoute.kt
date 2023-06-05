package no.tepohi.projectepta.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.projectepta.R
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

@Composable
fun DrawMapRoute(
    cameraPosition: CameraPositionState,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel
) {

    val favouriteStops by settingsViewModel.favouriteStops.observeAsState()
    val showPopUp by settingsViewModel.showPopUpFavouriteStop.observeAsState()

    val polylines by searchViewModel.polylines.observeAsState()
    val polyBounds by searchViewModel.polyBounds.observeAsState()

    val context = LocalContext.current

    // update camera position to polyline bounds
    LaunchedEffect(
        key1 = polylines
    ) {

        if (polylines != null) {

            val polylineBounds = LatLngBounds.builder()
            val polyPoints = polylines?.map { it.points }

            polyPoints?.forEach { list ->
                list.forEach { coordinate ->
                    polylineBounds.include(coordinate)
                }
            }

            val bounds = polylineBounds.build()

            CoroutineScope(Dispatchers.Main).launch {
                cameraPosition.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 800, 1400, 0)
                )
            }

            searchViewModel.polyBounds.postValue(bounds)
        }
    }

    if (polylines != null) {

        polylines!!.forEach { line ->
            CustomMapMarker(
                context = context,
                position = line.points[0],
                title = line.text,
                iconResourceId = line.startPointIconId
            )
            Polyline(
                points = line.points,
                color = line.color,
                zIndex = 400f,
                visible = true
            )
        }

        val end = polylines!!.last().points.size-1
        CustomMapMarker(
            context = context,
            position = polylines!!.last().points[end],
            title = polylines!!.last().text,
            iconResourceId = R.drawable.icon_map_end_36
        )

        if (polyBounds != null) {
            Polyline(
                points = listOf(
                    polyBounds!!.southwest,
                    LatLng(polyBounds!!.southwest.latitude, polyBounds!!.northeast.longitude),
                    polyBounds!!.northeast,
                    LatLng(polyBounds!!.northeast.latitude, polyBounds!!.southwest.longitude),
                    polyBounds!!.southwest,
                ),
                color = testColor
            )
        }
    }

    favouriteStops?.forEach { stop ->
        CustomMapMarker(
            context = context,
            position = LatLng(stop?.latitude ?: 0.0, stop?.longitude ?: 0.0),
            title = stop?.name ?: "",
            iconResourceId = R.drawable.icon_map_end_36,
            onInfoWindowClick = {
                settingsViewModel.showPopUpFavouriteStop.postValue(!showPopUp!!)
                searchViewModel.searchTempText.postValue(stop?.name)
            }
        )
    }


}
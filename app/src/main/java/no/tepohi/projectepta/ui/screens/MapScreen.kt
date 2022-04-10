package no.tepohi.projectepta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.House
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import no.tepohi.projectepta.ui.components.CustomMapMarker
import no.tepohi.projectepta.ui.data.MainActivityViewModel
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.R


@Composable
fun MapScreen(viewModel: MainActivityViewModel) {

    val data by viewModel.selectedTripData.observeAsState()

    val context = LocalContext.current
    val displayMetrics = context.resources.displayMetrics
    val dpHeight = displayMetrics.heightPixels / displayMetrics.density
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density

    val boundsBuilder = LatLngBounds.builder()
    val points = mutableListOf<LatLng>()

    var flag = true
    var start = LatLng(0.0, 0.0)
    var end = LatLng(0.0, 0.0)

    data?.legs?.let { legs ->

        legs.forEach { leg ->
            points.addAll(
                decode(leg!!.pointsOnLink?.points ?: "")
            )

            if (flag) start = points[0]; flag = false
            end = points[points.lastIndex]
        }

        points.forEach { boundsBuilder.include(it) }

        val bounds = boundsBuilder.build()
        val centre = LatLng(bounds.center.latitude, bounds.center.longitude)
        val zoom = getBoundsZoomLevel(bounds, dpWidth, dpHeight)

        val darkMode = isSystemInDarkTheme()
        val mapProperties = MapProperties(
            latLngBoundsForCameraTarget = bounds,
            minZoomPreference = 10f,
            mapStyleOptions = if (darkMode) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
        )
        val cameraPosition = CameraPositionState(
            position = CameraPosition.fromLatLngZoom(centre, zoom)
        )
        val uiSettings = MapUiSettings(
            compassEnabled = false,
            zoomControlsEnabled = false,
        )

        Scaffold(
            bottomBar = { }
        ) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
                    .wrapContentSize(Alignment.Center),
                properties = mapProperties,
                cameraPositionState = cameraPosition,
                uiSettings = uiSettings,
            ) {
                Polyline(
                    points = points,
                    color = Color.Red,
                    width = 5f
                )
                CustomMapMarker(
                    context = context,
                    position = start,
                    title = "start",
                    iconResourceId = R.drawable.walk_icon
                )
                CustomMapMarker(
                    context = context,
                    position = end,
                    title = "end",
                    iconResourceId = R.drawable.walk_icon
                )
            }
        }


    }


}

private fun decode(polyline: String): List<LatLng> {
    val coordinateChunks = mutableListOf<MutableList<Int>>()
    coordinateChunks.add(mutableListOf())

    for (char in polyline.toCharArray()) {
        // convert each character to decimal from ascii
        var value = char.code - 63

        // values that have a chunk following have an extra 1 on the left
        val isLastOfChunk = (value and 0x20) == 0
        value = value and (0x1F)

        coordinateChunks.last().add(value)

        if (isLastOfChunk)
            coordinateChunks.add(mutableListOf())
    }

    coordinateChunks.removeAt(coordinateChunks.lastIndex)

    val coordinates = mutableListOf<Double>()

    for (coordinateChunk in coordinateChunks) {
        var coordinate = coordinateChunk.mapIndexed { i, chunk -> chunk shl (i * 5) }.reduce { i, j -> i or j }

        // there is a 1 on the right if the coordinate is negative
        if (coordinate and 0x1 > 0)
            coordinate = (coordinate).inv()

        coordinate = coordinate shr 1
        coordinates.add((coordinate).toDouble() / 100000.0)
    }

    val points = mutableListOf<LatLng>()
    var prevLon = 0.0
    var prevLat = 0.0

    for(i in 0 until coordinates.size-1 step 2) {
        if(coordinates[i] == 0.0 && coordinates[i+1] == 0.0)
            continue

        prevLon += coordinates[i + 1]
        prevLat += coordinates[i]

        points.add(LatLng(prevLat, prevLon))
    }
    return points
}



private const val LN2 = 0.6931471805599453
private const val ZOOM_MAX = 21
private val WORLD_DP_HEIGHT = 256.dp.value
private val WORLD_DP_WIDTH = 256.dp.value


fun getBoundsZoomLevel(bounds: LatLngBounds, mapWidthPx: Float, mapHeightPx: Float): Float {
    val ne = bounds.northeast
    val sw = bounds.southwest

    val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI
    val lngDiff = ne.longitude - sw.longitude
    val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360

//    val latZoom = zoom(mapHeightPx, WORLD_PX_HEIGHT, latFraction)
//    val lngZoom = zoom(mapWidthPx, WORLD_PX_WIDTH, lngFraction)
    val latZoom: Double = zoom(mapHeightPx, WORLD_DP_HEIGHT, latFraction)
    val lngZoom: Double = zoom(mapWidthPx, WORLD_DP_WIDTH, lngFraction);

    val result = Math.min(latZoom.toInt(), lngZoom.toInt())

    return Math.min(result, ZOOM_MAX).toFloat()
}

private fun latRad(lat: Double): Double {
    val sin = Math.sin(lat * Math.PI / 180)
    val radX2 = Math.log((1 + sin) / (1 - sin)) / 2
    return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2
}

private fun zoom(mapPx: Float, worldPx: Float, fraction: Double): Double {
    return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2)
}
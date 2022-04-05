package no.tepohi.projectepta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import no.tepohi.projectepta.ui.theme.Constants

@Composable
fun MapScreen() {

//    val darkMode = isSystemInDarkTheme()
//
//    val mapProperties = MapProperties(
//        latLngBoundsForCameraTarget = LatLngBounds(
//            LatLng(59.809, 10.456),
//            LatLng(60.136, 10.954)
//        ),
//        minZoomPreference = 10f,
//        mapStyleOptions = if (darkMode) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
//    )
//    val cameraPosition = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(LatLng(59.93, 10.74), 12f)
//    }
//    val uiSettings = MapUiSettings(
//        compassEnabled = false,
//        zoomControlsEnabled = false,
//    )
//    Scaffold(
//        bottomBar = { }
//    ) {
//        GoogleMap(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background)
//                .wrapContentSize(Alignment.Center),
//            properties = mapProperties,
//            cameraPositionState = cameraPosition,
//            uiSettings = uiSettings,
//        )
//    }

}
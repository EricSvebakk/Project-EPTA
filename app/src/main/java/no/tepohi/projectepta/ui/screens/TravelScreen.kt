package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.components.CustomResultCard
import no.tepohi.projectepta.ui.viewmodels.MainActivityViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor

@Composable
fun TravelScreen(
    mainActivityViewModel: MainActivityViewModel,
    searchViewModel: SearchViewModel
) {

    val mapProperties = MapProperties(
        latLngBoundsForCameraTarget = LatLngBounds(
            Constants. MAP_BOUNDS_SW,
            Constants.MAP_BOUNDS_NE
        ),
        minZoomPreference = 10f,
        mapStyleOptions = if (isSystemInDarkTheme()) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
    )

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.93, 10.74), 12f)
    }

    val uiSettings = MapUiSettings(
        rotationGesturesEnabled = false,
        tiltGesturesEnabled = false,
        compassEnabled = true,
        zoomControlsEnabled = true,
        zoomGesturesEnabled = true,
        scrollGesturesEnabled = true,
    )

    Box {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center),
            properties = mapProperties,
            cameraPositionState = cameraPosition,
            uiSettings = uiSettings,
        )
        Column {
            MapScreenSearchbar(
                mainActivityViewModel = mainActivityViewModel,
                searchViewModel = searchViewModel
            )
            val modifier = Modifier.weight(1f)
            SearchResults(
                mainActivityViewModel = mainActivityViewModel,
                modifier = modifier,
            )
        }
    }

}

@Composable
fun MapScreenSearchbar(
    mainActivityViewModel: MainActivityViewModel,
    searchViewModel: SearchViewModel,
) {

    val interactSource = remember { MutableInteractionSource() }
    if (interactSource.collectIsPressedAsState().value) {
        mainActivityViewModel.showMapSettings.postValue(true)
    }

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    val view = LocalView.current
    val context = LocalContext.current

    var searchText by remember { mutableStateOf("Jernbanetorget") }
    val showMapSettings by mainActivityViewModel.showMapSettings.observeAsState()
    val autoCompleteSuggestions by searchViewModel.autoCompleteSuggestions.observeAsState()

    val startPlaceID = mainActivityViewModel.startPointData.value?.placeId ?: ""
    if (startPlaceID != "") {
        searchViewModel.toLoadPlaceResult(startPlaceID)
    }

    Column(
        Modifier
            .padding(Constants.PADDING_OUTER)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .zIndex(100f)
    ) {

        CustomAutoComplete(
            value = searchText,
            label = "from",
            items = autoCompleteSuggestions ?: emptyList(),
            dropDownSize = 180.dp,
            focusRequester = focusRequester1,
            nextFocusRequester = focusRequester2,
            interactionSource = interactSource,
            onValueChange = { searchString ->
                searchText = searchString
                searchViewModel.loadAutoCompleteSuggestions(context, searchText)
            },
            onDoneAction = { ACP ->
                Log.d("top DONE", ACP.toString())
                mainActivityViewModel.startPointData.postValue(ACP)
            },
            trailingIcon = {
                Row(
                    modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                ) {
                    IconButton(
                        content = {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        },
                        onClick = {
                            view.clearFocus()
                            mainActivityViewModel.tripsData.postValue(emptyList())
                            mainActivityViewModel.showTripsData.postValue(false)
                        },
                        modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    )
                }
            },
        )

        AnimatedVisibility(
            visible = showMapSettings ?: false,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            MapScreenSettings(
                mainActivityViewModel = mainActivityViewModel,
                searchViewModel = searchViewModel,
                focusRequester = focusRequester2,
            )
        }

    }
}

@Composable
fun MapScreenSettings(
    mainActivityViewModel: MainActivityViewModel,
    searchViewModel: SearchViewModel,
    focusRequester: FocusRequester,
) {

    val context = LocalContext.current

    var searchText by remember { mutableStateOf("Blindern vgs.") }
    val autoCompleteSuggestions by searchViewModel.autoCompleteSuggestions.observeAsState()

    val endPlaceID = mainActivityViewModel.endPointData.value?.placeId ?: ""
    if (endPlaceID != "") {
        searchViewModel.fromLoadPlaceResult(endPlaceID)
    }

    CustomAutoComplete(
        value = searchText,
        label = "to",
        items = autoCompleteSuggestions ?: emptyList(),
        dropDownSize = 180.dp,
        focusRequester = focusRequester,
        onValueChange = { searchString ->
            searchText = searchString
            searchViewModel.loadAutoCompleteSuggestions(context, searchText)
        },
        onDoneAction = { ACP ->
            Log.d("bottom DONE", ACP.toString())
            mainActivityViewModel.endPointData.postValue(ACP)
        },
        trailingIcon = {
            IconButton(
                modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS)),
                content = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                },
                onClick = {

                    val toPlaceLatLng = searchViewModel.toPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
                    val fromPlaceLatLng = searchViewModel.fromPlaceResult.value?.pos ?: LatLng(0.0, 0.0)

                    mainActivityViewModel.loadTrips(
                        start = fromPlaceLatLng,
                        end = toPlaceLatLng,
                    )

                    mainActivityViewModel.showTripsData.postValue(true)
                },
            )
        },
    )

}

fun searching() {

}

@Composable
fun SearchResults(
    mainActivityViewModel: MainActivityViewModel,
    modifier: Modifier,
) {

    val state = rememberScrollState()

    val showTripsData by mainActivityViewModel.showTripsData.observeAsState()

    val trips by mainActivityViewModel.tripsData.observeAsState()
    val start by mainActivityViewModel.startPointData.observeAsState()
    val end by mainActivityViewModel.endPointData.observeAsState()
    Log.d("trips!!!", "$trips $start $end")

    AnimatedVisibility(
        visible = showTripsData ?: false,
        enter = expandHorizontally(),
        exit = shrinkHorizontally(),
    ) {

        Column(
            modifier = Modifier
                //            .then(modifier)
                .padding(bottom = Constants.PADDING_OUTER)
                .fillMaxHeight()
                //            .width(100.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(
                        topEnd = Constants.CORNER_RADIUS,
                        bottomEnd = Constants.CORNER_RADIUS,
                    )
                )
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(
                        topEnd = Constants.CORNER_RADIUS,
                        bottomEnd = Constants.CORNER_RADIUS,
                    )
                )
                .zIndex(100f)
                .verticalScroll(state)
                .padding(Constants.PADDING_INNER)
        ) {
            if (trips != null) {
                print("hey 1")

                trips!!.forEach { tripPattern ->
                    CustomResultCard(tripPattern = tripPattern)
                }
            }

        }
    }

}
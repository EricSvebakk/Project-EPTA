package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import no.tepohi.projectepta.R
import no.tepohi.projectepta.ui.components.*
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.util.*

@Composable
fun TravelScreen(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val pl by searchViewModel.polylines.observeAsState()

    val cond1 = settingsViewModel.appColorPalette.value == Constants.THEME_DARK
    val cond2 = settingsViewModel.appColorPalette.value == Constants.THEME_SYSTEM && isSystemInDarkTheme()

    val mapProperties = MapProperties(
        latLngBoundsForCameraTarget = LatLngBounds(
            Constants. MAP_BOUNDS_SW,
            Constants.MAP_BOUNDS_NE
        ),
        minZoomPreference = 10f,
        mapStyleOptions = if (cond1 || cond2) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
    )

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.93, 10.74), 12f)
    }


    if (pl != null) {
        var idk = pl?.map { it.points }
        var idk2 = arrayListOf<LatLng>()
        idk?.forEach {
            idk2.addAll(it)
        }

        val bboxRes = bbox(idk2)

        cameraPosition.move(
            CameraUpdateFactory.newLatLngZoom(bboxRes["CC"]!!, 12f)
        )
    }

    val showTripsData by settingsViewModel.showTripsData.observeAsState()
    val showPopUp by settingsViewModel.showPopUpfavouriteStop.observeAsState()
    val favouriteStops by settingsViewModel.favouriteStops.observeAsState()

    val uiSettings = MapUiSettings(
        rotationGesturesEnabled = false,
        tiltGesturesEnabled = false,
        compassEnabled = true,
        zoomControlsEnabled = true,
        zoomGesturesEnabled = showTripsData == false,
        scrollGesturesEnabled = showTripsData == false,
    )

    val context = LocalContext.current


    Box {
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .wrapContentSize(Alignment.Center)
            ,
            properties = mapProperties,
            cameraPositionState = cameraPosition,
            uiSettings = uiSettings,
        ) {
            if (pl != null) {
                pl!!.forEach { line ->

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

                CustomMapMarker(
                    context = context,
                    position = pl!!.last().points[0],
                    title = pl!!.last().text,
                    iconResourceId = R.drawable.icon_map_end_36
                )
            }

            if (favouriteStops != null) {
                favouriteStops!!.forEach { stop ->
                    CustomMapMarker(
                        context = context,
                        position = LatLng(stop.latitude, stop.longitude),
                        title = stop.name,
                        iconResourceId = R.drawable.icon_map_end_36,
                        onClick = {
                            settingsViewModel.showPopUpfavouriteStop.postValue(!showPopUp!!)
                            searchViewModel.searchTempText.postValue(stop.name)
                        }
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TravelSearchbar(
                enturViewModel = enturViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel,
            )
        }

        AnimatedVisibility(
            visible = showTripsData ?: false,
            enter = slideInVertically(initialOffsetY = {offset -> offset}),
            exit = slideOutVertically(targetOffsetY = {offset -> offset}),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            TravelSearchResults(
                enturViewModel = enturViewModel,
                settingsViewModel = settingsViewModel,
                searchViewModel = searchViewModel,
                modifier = Modifier
            )
        }
        
        if (showPopUp == true) {
            Popup(
                alignment = Alignment.Center,
                onDismissRequest = {
                    settingsViewModel.showPopUpfavouriteStop.postValue(!showPopUp!!)
                }
            ) {
                Card(
                    elevation = 10.dp,
                ) {
                    Row(
                    modifier = Modifier
                        .padding(Constants.PADDING_OUTER)
                    ) {
                        CustomButton(
                            content = "from",
                            onClick = {
                                searchViewModel.searchFromText.postValue(searchViewModel.searchTempText.value)
                                settingsViewModel.showPopUpfavouriteStop.postValue(!showPopUp!!)
                            }
                        )
                        CustomButton(
                            content = "to",
                            onClick = {
                                searchViewModel.searchToText.postValue(searchViewModel.searchTempText.value)
                                settingsViewModel.showPopUpfavouriteStop.postValue(!showPopUp!!)
                            }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = (showPopUp ?: false) || (showTripsData ?: false),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(0.6f))
            )
        }
    }

}

@Composable
fun TravelSearchbar(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }

    val context = LocalContext.current

    var searchTextFrom by remember { mutableStateOf("Stortinget") }
    var searchTextTo by remember { mutableStateOf("Ullevål stadion") }

    val dateTime by settingsViewModel.dateTime.observeAsState()
    val timeString by settingsViewModel.timeString.observeAsState()
    val dateString by settingsViewModel.dateString.observeAsState()
    val showTimePicker by settingsViewModel.showTimePicker.observeAsState()
    val showDatePicker by settingsViewModel.showDatePicker.observeAsState()

    val allstops by enturViewModel.newStopsData.observeAsState()

    val mis = MutableInteractionSource()

//    mis.tryEmit(
//        PressInteraction.Press
//    )

    if (!searchViewModel.searchFromText.value.equals("")) {
        searchTextFrom = searchViewModel.searchFromText.value ?: ""
        searchViewModel.searchFromText.postValue("")
        focusRequester1.requestFocus()
        focusRequester1.freeFocus()
    }

    if (!searchViewModel.searchToText.value.equals("")) {
        searchTextTo = searchViewModel.searchToText.value ?: ""
        searchViewModel.searchToText.postValue("")
        focusRequester2.requestFocus()
    }

//    val autoCompleteSuggestions by searchViewModel.newStopData.observeAsState()

    if(showTimePicker == true) {
        CustomTimePicker(
            timeShown = dateTime ?: Calendar.getInstance(),
            onTimeSelected = { newTime ->
                settingsViewModel.updateTime(newTime)
            },
            onDismissRequest = {
                settingsViewModel.showTimePicker.postValue(!showTimePicker!!)
            }
        )
    }

    if (showDatePicker == true) {
        CustomDatePicker(
            dateShown = dateTime ?: Calendar.getInstance(),
            onDateSelected = { newDate ->
                settingsViewModel.updateDate(newDate)
            },
            onDismissRequest = {
                settingsViewModel.showDatePicker.postValue(!showDatePicker!!)
            }
        )
    }

    Column(
        Modifier
            .padding(Constants.PADDING_OUTER)
            .fillMaxWidth()
//            .zIndex(101f)
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
//            contentAlignment = Alignment.CenterEnd
        ) {

            Box(
                contentAlignment = Alignment.CenterEnd
            ) {
                Column {
                    CustomAutoComplete(
                        value = searchTextFrom ?: "",
                        label = "from",
//                        dropdownItems = autoCompleteSuggestions ?: allstops ?: emptyList(),
                        dropdownItems = allstops ?: emptyList(),
                        focusRequester = focusRequester1,
                        nextFocusRequester = focusRequester2,
                        onValueChange = { searchString ->
                            searchTextFrom = searchString
//                            searchViewModel.loadAutoCompleteSuggestions(context, searchTextFrom)
                        },
                        onDoneAction = { ACP ->
                            Log.d("top DONE", ACP.toString())

                            if (ACP.id != "") {
                                searchViewModel.fromLoadPlaceResult(ACP)
                            }

                        },
                        interactionSource = mis
                    )

                    Spacer(modifier = Modifier.height(5.dp))
                    
                    CustomAutoComplete(
                        value = searchTextTo ?: "",
                        label = "to",
//                        dropdownItems = autoCompleteSuggestions ?: allstops ?: emptyList(),
                        dropdownItems = allstops ?: emptyList(),
                        dropdownHeight = 180.dp,
                        focusRequester = focusRequester2,
                        onValueChange = { searchString ->
                            searchTextTo = searchString
//                            searchViewModel.loadAutoCompleteSuggestions(context, searchTextTo)
                        },
                        onDoneAction = { ACP ->
                            Log.d("bottom DONE", ACP.toString())

                            if (ACP.id != "") {
                                searchViewModel.toLoadPlaceResult(ACP)
                            }

                        },
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(end = 36.dp)
                ) {

                    IconButton(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
                            )
                            .border(
                                width = 1.5.dp,
                                color = MaterialTheme.colors.onBackground,
                                shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
                            )
                            .size(30.dp)
                            .zIndex(102f)
                        ,
                        content = {
                            Icon(imageVector = Icons.Filled.SwapVert, contentDescription = "SwapVert", tint = MaterialTheme.colors.onBackground)
                        },
                        onClick = {
                            val temp = searchTextFrom
                            searchTextFrom = searchTextTo
                            searchTextTo = temp ?: ""
                        },
                    )
                }
            }


        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {
            IconButton(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
                    )
                    .size(40.dp)
                ,
                content = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colors.primary)
                },
                onClick = {

                    focusRequester1.freeFocus()
                    focusRequester2.freeFocus()

                    val toPlaceLatLng = searchViewModel.toPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
                    val fromPlaceLatLng = searchViewModel.fromPlaceResult.value?.pos ?: LatLng(0.0, 0.0)

                    Log.d("PlaceLatLng tag", "$toPlaceLatLng $fromPlaceLatLng")

                    enturViewModel.loadTrips(
                        start = fromPlaceLatLng,
                        end = toPlaceLatLng,
                        time = dateTime ?: Calendar.getInstance()
                    )

                    settingsViewModel.showTripsData.postValue(true)
                },
            )
            Spacer(modifier = Modifier.width(5.dp))
            CustomButton(
                content = timeString ?: "",
                onClick = {
                    settingsViewModel.showTimePicker.postValue(!showTimePicker!!)
                }
            )
            Spacer(modifier = Modifier.width(5.dp))
            CustomButton(
                content = dateString ?: "",
                onClick = {
                    settingsViewModel.showDatePicker.postValue(!showDatePicker!!)
                }
            )
        }


    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TravelSearchResults(
    enturViewModel: EnturViewModel,
    settingsViewModel: SettingsViewModel,
    searchViewModel: SearchViewModel,
    modifier: Modifier
) {

    val trips by enturViewModel.tripsData.observeAsState()


    Column(
        modifier = Modifier
            .then(modifier)
            .padding(
                start = Constants.PADDING_OUTER,
                end = Constants.PADDING_OUTER,
                top = Constants.PADDING_OUTER,
            )
            .fillMaxSize()
            .fillMaxWidth()
            //            .width(100.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onBackground,
                shape = RoundedCornerShape(
                    topStart = Constants.CORNER_RADIUS,
                    topEnd = Constants.CORNER_RADIUS,
                )
            )
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(
                    topStart = Constants.CORNER_RADIUS,
                    topEnd = Constants.CORNER_RADIUS,
                )
            )
//            .zIndex(301f)
            //                .verticalScroll(state)
            .padding(Constants.PADDING_INNER)
    ) {

        Row(
            modifier = Modifier
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                content = {
                    Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                },
                onClick = {
                    enturViewModel.tripsData.postValue(emptyList())
                    settingsViewModel.showTripsData.postValue(false)
                },
                modifier = Modifier.border(
                    2.dp,
                    testColor,
                    RoundedCornerShape(Constants.CORNER_RADIUS)
                )
            )
        }

        if (trips != null) {
            Log.d("trips!!!", "$trips")

            LazyColumn {

                items(trips!!) { tripPattern ->
                    Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
                    //                    CustomResultCard(tripPattern = tripPattern)
                    CustomResultCard(
                        tripPattern = tripPattern,
                        searchViewModel = searchViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }




}
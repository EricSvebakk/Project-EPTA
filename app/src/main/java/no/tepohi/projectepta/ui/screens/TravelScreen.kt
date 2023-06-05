package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import no.tepohi.projectepta.ui.components.*
import no.tepohi.projectepta.ui.components.entur.TravelResultCard
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Constants.Companion.gesturesDisabled
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

    val cond1 = settingsViewModel.appColorPalette.value == Constants.THEME_DARK
    val cond2 = settingsViewModel.appColorPalette.value == Constants.THEME_SYSTEM && isSystemInDarkTheme()

    val mapProperties = MapProperties(
        latLngBoundsForCameraTarget = LatLngBounds(
            Constants.MAP_BOUNDS_SW,
            Constants.MAP_BOUNDS_NE
        ),
        minZoomPreference = 10f,
        mapStyleOptions = if (cond1 || cond2) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
    )

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Constants.MAP_BOUNDS_CENTER, 12f)
    }

    val showTripsData by settingsViewModel.showTripsData.observeAsState()
    val showPopUp by settingsViewModel.showPopUpFavouriteStop.observeAsState()
    val showFilterOptions by settingsViewModel.showFilterOptions.observeAsState()

    val uiSettings = MapUiSettings(
        rotationGesturesEnabled = false,
        tiltGesturesEnabled = false,
        compassEnabled = true,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false,
        zoomGesturesEnabled = showTripsData == false,
        scrollGesturesEnabled = showTripsData == false,
    )

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
            DrawMapRoute(
                cameraPosition,
                searchViewModel,
                settingsViewModel
            )

            // draws outer boundary of map
            Polyline(
                points = listOf(
                    Constants.MAP_BOUNDS_SW,
                    LatLng(Constants.MAP_BOUNDS_SW.latitude, Constants.MAP_BOUNDS_NE.longitude),
                    Constants.MAP_BOUNDS_NE,
                    LatLng(Constants.MAP_BOUNDS_NE.latitude, Constants.MAP_BOUNDS_SW.longitude),
                    Constants.MAP_BOUNDS_SW,
                ),
                color = testColor
            )
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

        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            AnimatedVisibility(
                visible = showTripsData ?: false,
                enter = slideInVertically(initialOffsetY = {offset -> offset}),
                exit = slideOutVertically(targetOffsetY = {offset -> offset}),
                modifier = Modifier
                    .height(560.dp)
            ) {
                TravelSearchResults(
                    enturViewModel = enturViewModel,
                    settingsViewModel = settingsViewModel,
                    searchViewModel = searchViewModel,
                    modifier = Modifier
                )
            }
        }

        if (showPopUp == true) {
            CustomMapMarkerSelector(
                settingsViewModel = settingsViewModel,
                searchViewModel = searchViewModel
            )
        }

        if (showFilterOptions == true) {
            CustomSelectTransport(
                settingsViewModel = settingsViewModel
            )
        }

        AnimatedVisibility(
            visible = (showPopUp ?: false) || (showTripsData ?: false) || (showFilterOptions ?: false),
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

    var searchTextFrom by remember { mutableStateOf("") }
    var searchTextTo by remember { mutableStateOf("") }

    val dateTime by settingsViewModel.dateTime.observeAsState()
    val timeString by settingsViewModel.timeString.observeAsState()
    val dateString by settingsViewModel.dateString.observeAsState()
    val showTimePicker by settingsViewModel.showTimePicker.observeAsState()
    val showDatePicker by settingsViewModel.showDatePicker.observeAsState()

    val show by settingsViewModel.showTripsData.observeAsState()
    val showFilter by settingsViewModel.showFilterOptions.observeAsState()

    val showFromSearch by settingsViewModel.showFromSearch.observeAsState()

    val allStops by enturViewModel.stopsData.observeAsState()


    // TODO: fix this shit
    if (!searchViewModel.searchFromText.value.equals("")) {
        searchTextFrom = searchViewModel.searchFromText.value ?: ""
        searchViewModel.searchFromText.postValue("")

        searchViewModel.fromLoadPlaceResult(
            allStops?.autoCompleteFilter(searchTextFrom)?.get(0)
        )
    }

    // TODO: fix this shit
    if (!searchViewModel.searchToText.value.equals("")) {
        searchTextTo = searchViewModel.searchToText.value ?: ""
        searchViewModel.searchToText.postValue("")

        searchViewModel.toLoadPlaceResult(
            allStops?.autoCompleteFilter(searchTextTo)?.get(0)
        )
    }

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
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            .gesturesDisabled((show ?: false) || (showFilter ?: false))
    ) {

        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
//            contentAlignment = Alignment.CenterEnd
        ) {

            // TODO: extract to separate composable function
            AnimatedVisibility(
                visible = showFromSearch == true,
                enter = expandVertically(expandFrom = Alignment.Top),
                exit = shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Box(
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column {
                        CustomAutoComplete(
                            value = searchTextFrom,
                            label = "from",
    //                        dropdownItems = autoCompleteSuggestions ?: allstops ?: emptyList(),
                            dropdownItems = allStops ?: emptyList(),
                            focusRequester = focusRequester1,
                            nextFocusRequester = focusRequester2,
                            onValueChange = { searchString ->
                                searchTextFrom = searchString
    //                            searchViewModel.loadAutoCompleteSuggestions(context, searchTextFrom)
                            },
                            onDoneAction = { ACP ->
                                Log.d("top DONE", ACP.toString())

                                if (ACP?.id != "") {
                                    searchViewModel.fromLoadPlaceResult(ACP)
                                }

                            },
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        CustomAutoComplete(
                            value = searchTextTo,
                            label = "to",
    //                        dropdownItems = autoCompleteSuggestions ?: allstops ?: emptyList(),
                            dropdownItems = allStops ?: emptyList(),
                            dropdownHeight = 180.dp,
                            focusRequester = focusRequester2,
                            onValueChange = { searchString ->
                                searchTextTo = searchString
    //                            searchViewModel.loadAutoCompleteSuggestions(context, searchTextTo)
                            },
                            onDoneAction = { ACP ->
                                Log.d("bottom DONE", ACP.toString())

                                if (ACP?.id != "") {
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
                                searchTextTo = temp
                            },
                        )
                    }
                }
            }


        }
        Spacer(modifier = Modifier.height(5.dp))

        // TODO: perhaps extract?
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

                    Log.d("temp dateTime tag", timeString.toString())

                    queryResult(
                        enturViewModel,
                        settingsViewModel,
                        searchViewModel,
                        dateTime
                    )
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
            Spacer(modifier = Modifier.width(5.dp))
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
                    Icon(imageVector = Icons.Filled.FilterAlt, contentDescription = "Filter", tint = MaterialTheme.colors.primary)
                },
                onClick = {
                    settingsViewModel.showFilterOptions.postValue(true)
                },
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(1f)
            ) {
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
                        Icon(
                            imageVector = if (showFromSearch == true) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                            contentDescription = "expand or fold",
                            tint = MaterialTheme.colors.primary
                        )
                    },
                    onClick = {
                        settingsViewModel.showFromSearch.postValue(!(showFromSearch ?: false))
                    },
                )
            }
        }


    }
}

// TODO: move to folder "methods"
fun queryResult(
    enturViewModel: EnturViewModel,
    settingsViewModel: SettingsViewModel,
    searchViewModel: SearchViewModel,
    dateTime: Calendar?,
//    numTrips: Int
) {

//    val dateTime = settingsViewModel.dateTime.value

    val numTrips = settingsViewModel.numTrips.value

    val toPlaceLatLng = searchViewModel.toPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
    val fromPlaceLatLng = searchViewModel.fromPlaceResult.value?.pos ?: LatLng(0.0, 0.0)

    Log.d("PlaceLatLng tag", "$toPlaceLatLng $fromPlaceLatLng")

    val modes = settingsViewModel.filter.value?.toList() ?: emptyList()

    enturViewModel.loadTrips(
        start = fromPlaceLatLng,
        end = toPlaceLatLng,
        time = dateTime ?: Calendar.getInstance(),
        modes = modes,
        numTrips = numTrips ?: 0
    )

    settingsViewModel.showTripsData.postValue(true)

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
//                start = Constants.PADDING_OUTER,
//                end = Constants.PADDING_OUTER,
                top = Constants.PADDING_OUTER,
            )
            .fillMaxSize()
            .fillMaxWidth()
            //            .width(100.dp)
            .border(
                width = 1.5.dp,
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

            var refreshing by remember { mutableStateOf(false) }
            LaunchedEffect(key1 = refreshing) {
                if (refreshing) {
                    delay(3000)
                    refreshing = false
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = refreshing),
                onRefresh = {
                    refreshing = true

                    queryResult(
                        enturViewModel,
                        settingsViewModel,
                        searchViewModel,
                        Calendar.getInstance()
                    )
                }
            ) {

                LazyColumn {

                    itemsIndexed(trips!!) { index, tripPattern ->

                        if (index == (trips!!.size -1)) {
                            settingsViewModel.numTrips.postValue(
                                settingsViewModel.numTrips.value!! + 5
                            )

                            Log.d("expanding tag", "YAY!!!")

                            val dateTime = settingsViewModel.dateTime.value

                            queryResult(
                                enturViewModel,
                                settingsViewModel,
                                searchViewModel,
                                dateTime ?: Calendar.getInstance()
                            )
                        }

                        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
                        //                    CustomResultCard(tripPattern = tripPattern)
                        TravelResultCard(
                            tripPattern = tripPattern,
                            searchViewModel = searchViewModel,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }

            }

        }
    }




}
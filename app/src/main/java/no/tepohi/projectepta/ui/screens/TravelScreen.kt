package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.projectepta.R
import no.tepohi.projectepta.ui.components.*
import no.tepohi.projectepta.ui.components.entur.TransportLabel
import no.tepohi.projectepta.ui.components.entur.TravelResultCard
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Constants.Companion.allTransports
import no.tepohi.projectepta.ui.theme.Constants.Companion.gesturesDisabled
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.lang.IllegalStateException
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
            Constants.MAP_BOUNDS_SW,
            Constants.MAP_BOUNDS_NE
        ),
        minZoomPreference = 10f,
        mapStyleOptions = if (cond1 || cond2) MapStyleOptions(Constants.JSON_MAP_DARKMODE) else null,
    )

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(59.93, 10.74), 12f)
    }


    val idk3 = LatLngBounds.builder()

    if (pl != null) {

        val idk = pl?.map { it.points }

        idk?.forEach { list ->
            list.forEach { coord ->
                idk3.include(coord)
            }
        }

        LaunchedEffect(key1 = idk3) {
            CoroutineScope(Dispatchers.Main).launch {

                cameraPosition.animate(
                    CameraUpdateFactory.newLatLngBounds(idk3.build(), 800, 1400, 0)
                )
            }
        }

    }

    val showTripsData by settingsViewModel.showTripsData.observeAsState()
    val showPopUp by settingsViewModel.showPopUpFavouriteStop.observeAsState()
    val showFilterOptions by settingsViewModel.showFilterOptions.observeAsState()
    val favouriteStops by settingsViewModel.favouriteStops.observeAsState()

    val currentLocation by searchViewModel.currentLocation.observeAsState()

    val uiSettings = MapUiSettings(
        rotationGesturesEnabled = false,
        tiltGesturesEnabled = false,
        compassEnabled = true,
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false,
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

//            if (currentLocation != null) {
//                CustomMapMarker(
//                    context = context,
//                    position = currentLocation!!,
//                    title = "YOU!",
//                    iconResourceId = R.drawable.icon_map_train_36,
//                    onInfoWindowClick = {
//                        settingsViewModel.showPopUpFavouriteStop.postValue(!showPopUp!!)
//                        searchViewModel.searchTempText.postValue("Your position")
//                    }
//                )
//            }

            var temp: LatLngBounds? = null
            try {
                temp = idk3.build()
            }
            catch (e: IllegalStateException) {
                Log.e("$e", "uh nu")
            }

            if (temp != null) {
                Polyline(
                    points = listOf(
                        temp.southwest,
                        LatLng(temp.southwest.latitude, temp.northeast.longitude),
                        temp.northeast,
                        LatLng(temp.northeast.latitude, temp.southwest.longitude),
                        temp.southwest,
                    ),
                    color = testColor
                )
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
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(dismissOnBackPress = true),
                onDismissRequest = {
                    settingsViewModel.showPopUpFavouriteStop.postValue(!showPopUp!!)
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
                                settingsViewModel.showPopUpFavouriteStop.postValue(!showPopUp!!)
                            }
                        )
                        CustomButton(
                            content = "to",
                            onClick = {
                                searchViewModel.searchToText.postValue(searchViewModel.searchTempText.value)
                                settingsViewModel.showPopUpFavouriteStop.postValue(!showPopUp!!)
                            }
                        )
                    }
                }
            }
        }

        if (showFilterOptions == true) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(dismissOnBackPress = true),
                onDismissRequest = {
                    settingsViewModel.showFilterOptions.postValue(!showFilterOptions!!)
                }
            ) {
                Card(
                    elevation = 10.dp,
                    modifier = Modifier
                        .height(220.dp)
                        .width(280.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        IconButton(
                            content = {
                                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                            },
                            onClick = {
                                settingsViewModel.showFilterOptions.postValue(false)
                            },
                            modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS)
                            )
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        
                        Text(text = "Select transports:")
                        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))

                        val filter by settingsViewModel.filter.observeAsState()

                        val sub = allTransports.filter { it.tm != null }

                        listOf(
                            sub.subList(0, (allTransports.size/2)),
                            sub.subList((allTransports.size/2), allTransports.size-1)
                        ).forEach {

                            Row(
                                modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                            ) {
                                it.forEach {
                                    var toggle by remember { mutableStateOf(true) }

                                    TextButton(
                                        onClick = {
                                            toggle = !toggle
                                            val temp = filter?.toMutableSet() ?: mutableSetOf()

                                            if (toggle) {
                                                temp.add(it.tm!!)
                                            } else {
                                                temp.remove(it.tm!!)
                                            }
                                            Log.d("filter tag", temp.toString())
                                            settingsViewModel.filter.postValue(temp)
                                        },
                                        modifier = Modifier
                                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                                    ) {
                                        TransportLabel(
                                            item = it,
                                            color = if (toggle) it.color else Color.Gray,
                                            text = it.mode,
                                            width = 120.dp
                                        )
                                    }
                                }
                            }
                        }



                    }


//                    Button(
//                        onClick = { toggle = !toggle },
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = Color.Transparent,
//                            contentColor = Color.Transparent,
//                        ),
//                        elevation = null,
//                        border = BorderStroke(2.dp, testColor),
//                        contentPadding = PaddingValues(0.dp)
//                    ) {
//                    }
                }
            }
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

    var searchTextFrom by remember { mutableStateOf("Stortinget") }
    var searchTextTo by remember { mutableStateOf("Ullevål stadion") }

    val dateTime by settingsViewModel.dateTime.observeAsState()
    val timeString by settingsViewModel.timeString.observeAsState()
    val dateString by settingsViewModel.dateString.observeAsState()
    val showTimePicker by settingsViewModel.showTimePicker.observeAsState()
    val showDatePicker by settingsViewModel.showDatePicker.observeAsState()

    val show by settingsViewModel.showTripsData.observeAsState()
    val showFilter by settingsViewModel.showFilterOptions.observeAsState()

    val showFromSearch by settingsViewModel.showFromSearch.observeAsState()

    val allStops by enturViewModel.stopsData.observeAsState()

    if (!searchViewModel.searchFromText.value.equals("")) {
        searchTextFrom = searchViewModel.searchFromText.value ?: ""
        searchViewModel.searchFromText.postValue("")

        searchViewModel.fromLoadPlaceResult(
            allStops?.autoCompleteFilter(searchTextFrom)?.get(0)
        )
    }

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

                    val toPlaceLatLng = searchViewModel.toPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
                    val fromPlaceLatLng = searchViewModel.fromPlaceResult.value?.pos ?: LatLng(0.0, 0.0)

                    Log.d("PlaceLatLng tag", "$toPlaceLatLng $fromPlaceLatLng")

                    val modes = settingsViewModel.filter.value?.toList() ?: emptyList()

                    enturViewModel.loadTrips(
                        start = fromPlaceLatLng,
                        end = toPlaceLatLng,
                        time = dateTime ?: Calendar.getInstance(),
                        modes = modes
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

            LazyColumn {

                items(trips!!) { tripPattern ->
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
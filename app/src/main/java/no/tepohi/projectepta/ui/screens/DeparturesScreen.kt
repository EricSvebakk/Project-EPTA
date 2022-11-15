package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.components.entur.DepartureResultCard
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

@Composable
fun DeparturesScreen(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val state = rememberScrollState()

    var searchText by remember { mutableStateOf("Jernbanetorget") }

//    val autoCompleteSuggestions by searchViewModel .observeAsState()
    val allStops by enturViewModel.stopsData.observeAsState()
    val departureSmt by searchViewModel.departurePlaceResult.observeAsState()
    val dd by enturViewModel.departuresData.observeAsState()


    Column(
        modifier = Modifier
            .padding(Constants.PADDING_OUTER)
            .fillMaxWidth()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
    ) {

        CustomAutoComplete(
            value = searchText,
            label = "find stop",
            dropdownItems = allStops ?: emptyList(),
            dropdownHeight = 180.dp,
            focusRequester = focusRequester,
            onValueChange = { searchString ->
                searchText = searchString
                Log.d("top DONE 1",searchText)
                searchViewModel.loadAutoCompleteSuggestions(context, searchText)
            },
            onDoneAction = { ACP ->
                Log.d("top DONE 2", ACP.toString())
                searchViewModel.departurePlaceResult(ACP)
            },
        )

        Spacer(modifier = Modifier.height(5.dp))

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

                focusRequester.freeFocus()

                val depPlaceLatLng = searchViewModel.departurePlaceResult.value?.pos ?: LatLng(0.0, 0.0)

                Log.d("depPlaceLatLng tag", "$depPlaceLatLng")

                enturViewModel.loadDepartures(
                    depPlaceLatLng
                )

//                settingsViewModel.showTripsData.postValue(true)
            },
        )

        Spacer(modifier = Modifier.height(5.dp))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))

        ) {
            if (dd != null) {

                LazyColumn {

                    items(dd!!) { tripPattern ->
//                        Log.d("EC tag", tripPattern.toString())

                        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
                        //                    CustomResultCard(tripPattern = tripPattern)
                        DepartureResultCard(
                            EC = tripPattern,
                            searchViewModel = searchViewModel,
                            settingsViewModel = settingsViewModel
                        )
                    }
                }
            }
        }

    }

}
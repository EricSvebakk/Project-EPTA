package no.tepohi.projectepta.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.components.CustomDepartureResultCard
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

//    val autoCompleteSuggestions by searchViewModel.newStopData.observeAsState()
    val departureSmt by searchViewModel.departurePlaceResult.observeAsState()
    val dd by enturViewModel.departuresData.observeAsState()

    val placeID = enturViewModel.departurePointData.value?.placeId ?: ""

    if (placeID != "") {
        searchViewModel.DeparturePlaceResult(placeID)
    }

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
            label = "from",
//            dropdownItems = autoCompleteSuggestions ?: emptyList(),
            dropdownItems = emptyList(),
            dropdownHeight = 180.dp,
            focusRequester = focusRequester,
            onValueChange = { searchString ->
                searchText = searchString
                Log.d("top DONE 1",searchText)
                searchViewModel.loadAutoCompleteSuggestions(context, searchText)
            },
            onDoneAction = { ACP ->
                Log.d("top DONE 2", ACP.toString())
    //                searchViewModel.loadPlaceResult(ACP.placeId)
//                enturViewModel.departurePointData.postValue(ACP)
            },
//            trailingIcon = {
//                Row(
//                    modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//                ) {
//                    IconButton(
//                        modifier = Modifier.border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS)),
//                        content = {
//                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
//                        },
//                        onClick = {
//                            enturViewModel.loadDepartures(departureSmt?.pos ?: LatLng(0.0, 0.0))
//                        },
//                    )
//                }
//            },
        )

        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.onBackground,
                    shape = RoundedCornerShape(Constants.CORNER_RADIUS)
                )
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                .verticalScroll(state)

        ) {
            if (dd != null) {
                dd!!.forEach { EC ->
                    CustomDepartureResultCard(EstimatedCall = EC)
                    Log.d("EC tag", EC.toString())
                }
            }
        }

    }

}
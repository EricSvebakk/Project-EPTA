package no.tepohi.projectepta.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val showPopUp by settingsViewModel.showPopUp.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .padding(Constants.PADDING_OUTER)
                .border(
                    2.dp,
                    MaterialTheme.colors.onBackground,
                    RoundedCornerShape(Constants.CORNER_RADIUS)
                )
                .padding(Constants.PADDING_OUTER)
        ) {
            Favourites(
                enturViewModel = enturViewModel,
                searchViewModel = searchViewModel,
                settingsViewModel = settingsViewModel
            )
        }

        Column(
            modifier = Modifier
                .padding(Constants.PADDING_OUTER)
                .border(
                    2.dp,
                    MaterialTheme.colors.onBackground,
                    RoundedCornerShape(Constants.CORNER_RADIUS)
                )
                .fillMaxWidth()
                .height(200.dp)
        ) {

            val mod = Modifier.weight(1f)

            ChoosingTheme(
                enturViewModel = enturViewModel,
                settingsViewModel = settingsViewModel,
                searchViewModel = searchViewModel,
                modifier = mod
            )
        }

    }

    AnimatedVisibility(
        visible = showPopUp ?: false,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.5f))
                .zIndex(1000f)
        ) {
        }
    }


}

@Composable
fun ChoosingTheme(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier
) {
    val acp by settingsViewModel.appColorPalette.observeAsState()

    listOf(
        Constants.THEME_LIGHT,
        Constants.THEME_DARK,
        Constants.THEME_SYSTEM
    ).forEach { theme ->

        if (acp == theme)
            modifier.border(2.dp, MaterialTheme.colors.onBackground)

        Button(
            modifier = Modifier
                .padding(Constants.PADDING_INNER)
                .fillMaxWidth()
                .then(modifier)
            ,
            onClick = {
                settingsViewModel.appColorPalette.postValue(theme)
            }
        ) {
            Text(text = theme)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Favourites(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    val focusRequester1 = remember { FocusRequester() }

    val allStops by enturViewModel.stopsData.observeAsState()
    val favouriteStops by settingsViewModel.favouriteStops.observeAsState()

    val showPopup by settingsViewModel.showPopUp.observeAsState()

    val context = LocalContext.current

    var searchText by remember { mutableStateOf("") }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
//            .padding(Constants.PADDING_INNER)
            .fillMaxWidth()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {
        Text(text = "Favourite stops")
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        CustomAutoComplete(
            value = searchText,
            label = "Stop name",
            dropdownItems = allStops ?: emptyList(),
            focusRequester = focusRequester1,
            onValueChange = { searchString ->
                searchText = searchString
                searchViewModel.loadAutoCompleteSuggestions(context, searchText)
            },
            onDoneAction = { ACP ->

                val temp = arrayListOf<StopPlacesByBoundaryQuery.StopPlacesByBbox?>()
                favouriteStops?.forEach { stop ->
                    temp.add(stop)
                }
                temp.add(ACP)

                settingsViewModel.favouriteStops.postValue(temp)
                searchText = ""
            },
        )

        favouriteStops?.forEach { heh ->

            var toggle by remember { mutableStateOf(false) }

            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .clip(RectangleShape)
            ) {
                ActionsRow(
                    actionIconSize = 50.dp,
                    onDelete = {
                        val temp = arrayListOf<StopPlacesByBoundaryQuery.StopPlacesByBbox?>()
                        favouriteStops?.forEach { stop ->
                            temp.add(stop)
                        }
                        temp.remove(heh)

                        settingsViewModel.favouriteStops.postValue(temp)
                    },
                    onEdit = { /*TODO*/ },
                    onFavorite = {  }
                )
                DraggableCard(
                    card = CardModel(heh?.id ?: "", heh?.name ?: ""),
                    cardHeight = 50.dp,
                    isRevealed = toggle,
                    cardOffset = -200f,
                    onExpand = { toggle = true },
                    onCollapse = { toggle = false }
                )
            }

//            Spacer(modifier = Modifier.height(Constants.PADDING_INNER))

//            val ds = rememberDismissState()
//
//            if (ds.isDismissed(DismissDirection.EndToStart)) {
//                val temp = arrayListOf<StopPlacesByBoundaryQuery.StopPlacesByBbox?>()
//                favouriteStops?.forEach { stop ->
//                    temp.add(stop)
//                }
//                temp.remove(heh)
//
//                settingsViewModel.favouriteStops.postValue(temp)
//            }
//
//
//           SwipeToDismiss(
//               modifier = Modifier,
//               state = ds,
//               directions = setOf(DismissDirection.EndToStart),
//               background = {
//
////                   if (ds.isDismissed(DismissDirection.EndToStart)) {
////
////                       LaunchedEffect(key1 = 1) {
////                           CoroutineScope(Dispatchers.Main).launch {
////
////                               ds.animateTo(Diss)
////                           }
////                       }
////                   }
//
//                   Box(
//                       contentAlignment = Alignment.CenterEnd,
//                       modifier = Modifier
//                           .fillMaxSize()
//                           .padding(Constants.PADDING_INNER)
//                   ) {
//                       Icon(imageVector = Icons.Filled.Delete, contentDescription = "delete")
//                   }
//               },
//           ) {
//               Card(
//                   elevation = 10.dp,
//                   backgroundColor = MaterialTheme.colors.secondary,
//                   modifier = Modifier
//                       .padding(Constants.PADDING_INNER)
//                       .fillMaxWidth()
//               ) {
//                    Text(
//                        text = heh?.name ?: "",
//                        color = MaterialTheme.colors.onSecondary,
//                        modifier = Modifier
//                            .padding(Constants.PADDING_INNER)
//                    )
//               }
//               Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
//           }
       }

    }
}
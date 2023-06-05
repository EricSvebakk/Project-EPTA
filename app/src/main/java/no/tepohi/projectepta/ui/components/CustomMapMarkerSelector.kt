package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel


@Composable
fun CustomMapMarkerSelector(
    settingsViewModel: SettingsViewModel,
    searchViewModel: SearchViewModel,
) {

    val showPopUp by settingsViewModel.showPopUpFavouriteStop.observeAsState()

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
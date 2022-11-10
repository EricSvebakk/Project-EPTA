package no.tepohi.projectepta.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.viewmodels.EnturViewModel
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    enturViewModel: EnturViewModel,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel,
) {

    Column(
        modifier = Modifier
            .padding(Constants.PADDING_OUTER)
            .border(2.dp, MaterialTheme.colors.onBackground, RoundedCornerShape(Constants.CORNER_RADIUS))
            .fillMaxWidth()
            .height(200.dp)
    ) {

        listOf(
            Constants.THEME_LIGHT,
            Constants.THEME_DARK,
            Constants.THEME_SYSTEM
        ).forEach { theme ->

            Button(
                modifier = Modifier
                    .padding(Constants.PADDING_INNER)
                    .fillMaxWidth()
                    .weight(1f)
                ,
                onClick = {
                    settingsViewModel.appColorPalette.postValue(theme)
                }
            ) {
                Text(text = theme)
            }
        }
    }


}
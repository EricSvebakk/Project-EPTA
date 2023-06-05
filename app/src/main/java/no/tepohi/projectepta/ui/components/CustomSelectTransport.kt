package no.tepohi.projectepta.ui.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import no.tepohi.projectepta.ui.components.entur.TransportLabel
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel

@Composable
fun CustomSelectTransport(
    settingsViewModel: SettingsViewModel,
) {

    val showFilterOptions by settingsViewModel.showFilterOptions.observeAsState()

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

                val sub = Constants.allTransports.filter { it.tm != null }

                listOf(
                    sub.subList(0, (Constants.allTransports.size/2)),
                    sub.subList((Constants.allTransports.size/2), Constants.allTransports.size-1)
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
        }
    }

}
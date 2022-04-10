package no.tepohi.projectepta.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.tepohi.projectepta.BottomNavItem
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.components.CustomButton
import no.tepohi.projectepta.ui.data.MainActivityViewModel
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripsScreen(
    navController: NavController,
    viewModel: MainActivityViewModel,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            .padding(Constants.PADDING_OUTER)
    ) {
        SearchField(viewModel)
        Spacer(modifier = Modifier.height(Constants.PADDING_OUTER))
        SearchResult(navController, viewModel)
    }
}

@Composable
fun SearchField(viewModel: MainActivityViewModel) {

    val context = LocalContext.current

    val stops by viewModel.stopsData.observeAsState()
    val textFieldFrom by viewModel.textFieldFrom.observeAsState()
    val textFieldTo by viewModel.textFieldTo.observeAsState()
    val dateString by viewModel.dateString.observeAsState()
    val timeString by viewModel.timeString.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        stops?.let { stopsData ->

            CustomAutoComplete(
                value = textFieldFrom ?: "",
                label = "from",
                items = stopsData.map { it!!.name },
                onValueChange = { value -> viewModel.textFieldFrom.postValue(value) },
                onClearClick = { viewModel.textFieldFrom.postValue("") },
            )
            Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
            CustomAutoComplete(
                value = textFieldTo ?: "",
                label = "to",
                items = stopsData.map { it!!.name },
                onValueChange = { value -> viewModel.textFieldTo.postValue(value) },
                onClearClick = { viewModel.textFieldTo.postValue("") },
            )
            Spacer(modifier = Modifier.height(Constants.PADDING_INNER))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(47.dp)
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                ,
            ) {

                CustomButton(
                    content = "Search",
                    onClick = {

                        if (stopsData.map { it!!.name }.containsAll(listOf(textFieldFrom, textFieldTo))) {

                            viewModel.loadTrips(
                                stopsData.filter { it!!.name == textFieldFrom }[0]!!.id,
                                stopsData.filter { it!!.name == textFieldTo }[0]!!.id,
                                viewModel.dateTime.value!!
                            )
                        }
                        else {
                            Toast.makeText(context,"Invalid arguments", Toast.LENGTH_LONG).show()
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                    ),
                    border = false
                )
                Spacer(modifier = Modifier.width(Constants.PADDING_INNER))
                CustomButton(
                    content = timeString ?: "",
                    onClick = { viewModel.selectTime(context) }
                )
                Spacer(modifier = Modifier.width(Constants.PADDING_INNER))
                CustomButton(
                    content = dateString ?: "",
                    onClick = { viewModel.selectDate(context) }
                )
            }
        }

    }

}

@Composable
fun SearchResult(
    navController: NavController,
    viewModel: MainActivityViewModel,
) {

    val trips by viewModel.tripsData.observeAsState()

    val items = listOf(
        Transports.Foot,
        Transports.Bus,
        Transports.Tram,
        Transports.Metro,
        Transports.Rail,
    )

    val state = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(
                2.dp,
                MaterialTheme.colors.onSurface,
                RoundedCornerShape(Constants.CORNER_RADIUS)
            )
            .verticalScroll(state)
            .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        if (trips?.isNotEmpty() == true) {

            trips!!.forEach { tripPattern ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectedTripData.postValue(tripPattern)
                            navController.navigate(BottomNavItem.Map.screen_route) {

                                navController.graph.startDestinationRoute?.let { screen_route ->
                                    popUpTo(screen_route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }

                        }
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .padding(Constants.PADDING_INNER)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    val dateAsRaw = tripPattern.expectedStartTime.toString()
                    val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
                    val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
                    val duration = (tripPattern.duration.toString().toInt() / 60.0).toInt().toString()

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .width(60.dp)
                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = dateAsString)
                        Text(text = "(${duration}min)", fontSize = 12.sp)
                    }

                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        ,
                        horizontalAlignment = Alignment.Start
                    ) {
                        tripPattern.legs.forEach { leg ->

                            Row(
                                modifier = Modifier
                                    .border(
                                        2.dp,
                                        testColor,
                                        RoundedCornerShape(Constants.CORNER_RADIUS)
                                    )
                                    .padding(5.dp)
                                ,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                items.forEach { item ->
                                    if (item.description == leg?.mode.toString()) {

                                        val modeFoot = item.description != Transports.Foot.description

                                        Row(
                                            modifier = Modifier
                                                .width(68.dp)
                                                .border(
                                                    2.dp,
                                                    testColor,
                                                    RoundedCornerShape(Constants.CORNER_RADIUS)
                                                )
                                                .background(item.color, RoundedCornerShape(4.dp))
                                                .padding(1.dp)
                                            ,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Image(
                                                imageVector = item.icon,
                                                contentDescription = item.description,
                                                colorFilter = ColorFilter.tint(
                                                    if (modeFoot) Color.White else Color.Black
                                                )
                                            )
                                            if (modeFoot) {
                                                Text(leg?.line?.id?.split(":")?.get(2).toString(), color = Color.White)
                                            } else {
                                                Text(text = "${(leg?.duration.toString().toInt() / 60.0).toInt()}", color = Color.Black)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(Constants.PADDING_INNER))
                                Text(
                                    modifier = Modifier
                                        .border(
                                            2.dp,
                                            testColor,
                                            RoundedCornerShape(Constants.CORNER_RADIUS)
                                        )
                                        .padding(2.dp)
                                    ,
                                    text = "to ${leg?.toPlace?.name}"
                                )

                            }
                        }
                    }


                }
                Divider()
            }
        }


    }

}
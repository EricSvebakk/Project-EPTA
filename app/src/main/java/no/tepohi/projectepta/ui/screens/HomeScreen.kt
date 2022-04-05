package no.tepohi.projectepta.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.components.CustomAutoComplete
import no.tepohi.projectepta.ui.components.CustomButton
import no.tepohi.projectepta.ui.data.DateTimeViewModel
import no.tepohi.projectepta.ui.data.MainActivityViewModel
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat

@Composable
fun HomeScreen(
    navController: NavController,
    stops: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>
) {

    val viewModel: MainActivityViewModel = viewModel()
    val trips = viewModel.tripsData.observeAsState()

//    Log.d("stops-arraylist tag home", stops.toString())
//    println("trips: ${trips.value}")

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

        SearchField(stops, viewModel)

        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))

        SearchResult(navController, trips)
    }

}

@Composable
fun SearchField(
    stops: List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>,
    mainViewModel: MainActivityViewModel
) {

    var fromValue by remember { mutableStateOf("Oslo S") }
    var toValue by remember { mutableStateOf("Blindern vgs.") }

    val context = LocalContext.current
    val viewModel: DateTimeViewModel = viewModel()
    val dateString = viewModel.dateString.observeAsState()
    val timeString = viewModel.timeString.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        CustomAutoComplete(
            value = fromValue,
            label = "from",
            items = stops.map { it!!.name },
            onValueChange = { value -> fromValue = value }
        )
        Spacer(modifier = Modifier.height(Constants.PADDING_INNER))
        CustomAutoComplete(
            value = toValue,
            label = "to",
            items = stops.map { it!!.name },
            onValueChange = { value -> toValue = value }
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

                    if (stops.map { it!!.name }.containsAll(listOf(fromValue, toValue))) {

                        mainViewModel.loadTrips(
                            stops.filter { it!!.name == fromValue }[0]!!.id,
                            stops.filter { it!!.name == toValue }[0]!!.id,
                            viewModel.timeDate.value!!
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
                content = timeString.value!!,
                onClick = { viewModel.selectTime(context) }
            )
            Spacer(modifier = Modifier.width(Constants.PADDING_INNER))
            CustomButton(
                content = dateString.value!!,
                onClick = { viewModel.selectDate(context) }
            )
        }
    }

}

sealed class EptaTransport(var description: String, var icon: ImageVector, var color: Color){
    object Foot : EptaTransport("foot", Icons.Filled.DirectionsWalk, Color(94, 200, 231, 151))
    object Bus : EptaTransport("bus", Icons.Filled.DirectionsBus, Color.Red)
    object Tram : EptaTransport("tram", Icons.Filled.Tram, Color.Blue)
    object Metro : EptaTransport("metro", Icons.Filled.DirectionsSubway, Color(194, 78, 0, 255))
    object Rail : EptaTransport("rail", Icons.Filled.Train, Color(0, 100, 40, 255))
}

@Composable
fun SearchResult(
    navController: NavController,
    trips: State<List<FindTripQuery.TripPattern>?>
) {

    val items = listOf(
        EptaTransport.Foot,
        EptaTransport.Bus,
        EptaTransport.Tram,
        EptaTransport.Metro,
        EptaTransport.Rail,
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

        if (trips.value?.isNotEmpty() == true) {

            trips.value!!.forEach { tripPattern ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
//                            navController.navigate(BottomNavItem.Map.screen_route)
                        }
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .padding(Constants.PADDING_INNER)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    val dateAsRaw = tripPattern.expectedStartTime.toString()
                    val dateAsObject = SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ssXXX").parse(dateAsRaw)!!
                    val dateAsString = SimpleDateFormat("kk:mm").format(dateAsObject)
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
                                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                                    .padding(5.dp)
                                ,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                items.forEach { item ->
                                    if (item.description == leg?.mode.toString()) {

                                        val modeFoot = item.description != EptaTransport.Foot.description

                                        Row(
                                            modifier = Modifier
                                                .width(55.dp)
                                                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
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
                                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
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
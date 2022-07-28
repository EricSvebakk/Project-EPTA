package no.tepohi.projectepta.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomResultCard(
    tripPattern: FindTripQuery.TripPattern
) {

    val items = listOf(
        Transports.Foot,
        Transports.Bus,
        Transports.Tram,
        Transports.Metro,
        Transports.Rail,
    )

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            navigateToMap(viewModel, navController, tripPattern)
//                        }
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
            .clickable(enabled = true) {

            }
            .padding(Constants.PADDING_INNER)
    ) {

        val dateAsRaw = tripPattern.expectedStartTime.toString()
        val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
        val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
        val duration = (tripPattern.duration.toString().toInt() / 60.0).toInt().toString()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
//                            .weight(1f)
                .width(55.dp)
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
        ) {
            Text(text = dateAsString, textAlign = TextAlign.Center)
            Text(text = "(${duration}min)", textAlign = TextAlign.Center,fontSize = 12.sp)
        }

        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
//                            .weight(3f)
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
        ) {

            tripPattern.legs.forEach { leg ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .padding(5.dp)
                ) {

                    items.forEach { item ->
                        if (item.mode == leg?.mode.toString()) {

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .width(78.dp)
                                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                                    .background(item.color, RoundedCornerShape(4.dp))
                                    .padding(2.dp)
                            ) {

                                Image(
                                    modifier = Modifier
                                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                                    ,
                                    painter = painterResource(id = item.iconTableId),
                                    contentDescription = item.mode,
//                                                colorFilter = ColorFilter.tint( Color.White)
                                )

                                val modeFoot = item.mode != Transports.Foot.mode

                                val text = if (modeFoot) {
                                    leg?.line?.publicCode.toString()
                                } else {
                                    "${(leg?.duration.toString().toInt() / 60.0).toInt()}min"
                                }

                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                                    ,
                                    textAlign = TextAlign.Center,
                                    text = text,
                                    color = Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

//                                Spacer(modifier = Modifier.width(Constants.PADDING_INNER))
//                                Text(
//                                    text = "to ${leg?.toPlace?.name}",
//                                    modifier = Modifier
//                                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//                                        .padding(2.dp)
//                                )

                }
            }
        }


    }
//                Divider()

}
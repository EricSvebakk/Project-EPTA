package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun NewCustomResultCard(
    tripPattern: FindTripQuery.TripPattern
) {

    val items = listOf(
        Transports.Foot,
        Transports.Bus,
        Transports.Tram,
        Transports.Metro,
        Transports.Rail,
    )

    Card(

    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
//                .border(
//                    width = 1.dp,
//                    color = MaterialTheme.colors.onBackground,
//                    shape = RoundedCornerShape(Constants.CORNER_RADIUS)
//                )
//                .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
                .padding(Constants.PADDING_INNER)
        ) {

            // outer result border
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    .padding(Constants.PADDING_INNER + 10.dp)
            ) {

                tripPattern.legs.forEach { leg ->
                    val item = items.find { it.mode == leg?.mode.toString() }!!
                    ShowLeg(leg, item)
                }
            }

            val dateAsRaw = tripPattern.expectedStartTime.toString()
            val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
            val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
            val duration = (tripPattern.duration.toString().toInt() / 60.0).toInt().toString()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(55.dp)
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            ) {
                Text(text = dateAsString, textAlign = TextAlign.Center)
                Text(text = "(${duration}min)", textAlign = TextAlign.Center,fontSize = 12.sp)
            }
        }
    }

}
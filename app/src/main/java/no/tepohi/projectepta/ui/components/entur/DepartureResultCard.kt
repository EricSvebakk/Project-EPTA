package no.tepohi.projectepta.ui.components.entur

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.DepartureBoardQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepartureResultCard(
    EC:  DepartureBoardQuery.EstimatedCall,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel
) {

    val item = Constants.allTransports.find { it.mode == EC.serviceJourney?.line?.transportMode?.toString() }!!

    Card(
        elevation = 10.dp
    ) {

        //
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(Constants.PADDING_INNER)
        ) {

            val hex = EC.serviceJourney?.line?.presentation?.colour
            val c = if (hex != null)
                Color(android.graphics.Color.parseColor("#$hex"))
            else
                Color(148, 148, 148, 255)

            // Orders tripPatterns correctly
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(2f)
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                ) {

                    TransportLabel(
                        item = item,
                        color = c,
                        text = EC.serviceJourney?.line?.publicCode.toString()
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = EC.destinationDisplay?.frontText.toString(),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            val dateAsRaw = EC.expectedArrivalTime.toString()
            val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
            val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
//                val duration = (EC. .duration.toString().toInt() / 60.0).toInt().toString()

            // Top right text
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    .weight(1f)
                    .padding(Constants.PADDING_INNER)
            ) {
                Text(
                    text = dateAsString,
                    textAlign = TextAlign.Center
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        fontSize = 14.sp,
                        text = try {
                            EC.quay?.publicCode?.toInt()
                            "track"
                        }
                        catch (e: NumberFormatException) {
                            "platform"
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(255, 193, 7, 255))
                            .border(1.dp, Color.Black, CircleShape)
                            .size(22.dp)
//                            .padding(5.dp)
                    ) {
                        Text(
                            fontSize = 12.sp,
                            text = EC.quay?.publicCode.toString()
                        )
                    }
                }
//                    Text(text = "(${duration}min)", textAlign = TextAlign.Center,fontSize = 12.sp)
            }



        // row end
        }

    // card end
    }

}

//data class polyline(val points: List<LatLng>, val color: Color, val text: String, val startPointIconId: Int)
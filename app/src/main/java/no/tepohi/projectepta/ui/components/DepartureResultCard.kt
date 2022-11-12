package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.DepartureBoardQuery
import no.tepohi.projectepta.ui.screens.decode
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DepartureResultCard(
    EC:  DepartureBoardQuery.EstimatedCall,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel
) {

    val items = listOf(
        Transports.Foot,
        Transports.Bus,
        Transports.Tram,
        Transports.Metro,
        Transports.Rail,
    )

    val item = items.find { it.mode == EC.serviceJourney?.journeyPattern?.line?.transportMode?.toString() }!!


    Card(
        elevation = 10.dp
    ) {

        //
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.PADDING_INNER)
        ) {

            val hex = EC.serviceJourney?.journeyPattern?.line?.presentation?.colour
            val c = if (hex != null)
                Color(android.graphics.Color.parseColor("#$hex"))
            else
                Color(148, 148, 148, 255)

            // Orders tripPatterns correctly
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    .fillMaxHeight()
                    .weight(1f)
            ) {

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .width(78.dp)
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .background(c, RoundedCornerShape(4.dp))
                        .padding(2.dp)
                ) {

                    Image(
                        modifier = Modifier
                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        ,
                        painter = painterResource(id = item.iconTableId),
                        contentDescription = item.mode,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.background)
                    )


                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        ,
                        textAlign = TextAlign.Center,
                        text = EC.serviceJourney?.journeyPattern?.line?.publicCode.toString(),
                        color = MaterialTheme.colors.background,
                        fontSize = 14.sp
                    )

                    // symbol end
                }

                Spacer(modifier = Modifier.height(Constants.PADDING_INNER))

                Text(text = EC.serviceJourney?.journeyPattern?.line?.name.toString())

//                var s = EC.serviceJourney?.journeyPattern?.line?.transportMode.toString()
//                s += " " + EC.serviceJourney?.journeyPattern?.line?.publicCode
//
//                Text(text = s)

//                    EC.serviceJourney.journeyPattern.
//                        .legs.forEach { leg ->
//
//                        val item = items.find { it.mode == leg?.mode.toString() }!!
//
//                        val result = decode(leg?.pointsOnLink?.points.toString())
//                        val hex = leg?.line?.presentation?.colour
//                        val c = if (hex != null)
//                            Color(android.graphics.Color.parseColor("#$hex"))
//                        else
//                            Color(148, 148, 148, 255)
//
//                        val pl = polyline(
//                            result,
//                            c,
//                            leg?.fromPlace?.name ?: "",
//                            item.iconMapId
//                        )
//
//                        ShowLeg(leg, item, c)
//                        polylines.add(pl)
//                    }
            }

            val dateAsRaw = EC.expectedArrivalTime.toString()
            val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
            val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
//                val duration = (EC. .duration.toString().toInt() / 60.0).toInt().toString()

            // Top right text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(55.dp)
                    .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            ) {
                Text(text = dateAsString, textAlign = TextAlign.Center)
//                    Text(text = "(${duration}min)", textAlign = TextAlign.Center,fontSize = 12.sp)
            }

        // row end
        }

    // card end
    }

}

//data class polyline(val points: List<LatLng>, val color: Color, val text: String, val startPointIconId: Int)
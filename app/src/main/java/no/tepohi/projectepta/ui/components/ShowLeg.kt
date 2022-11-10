package no.tepohi.projectepta.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ShowLeg(
    leg:  FindTripQuery.Leg?,
    item: Transports
) {

    var clicked by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        var dateAsRaw = leg?.expectedStartTime.toString()
        var dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
        val startDate = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)

        dateAsRaw = leg?.expectedEndTime.toString()
        dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
        val endDate = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)

        if (leg?.intermediateEstimatedCalls?.isEmpty() == true) {
            NoIntermediateCalls(startDate, endDate, leg, item, 80.dp)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { clicked = !clicked }
            ) {
                AnimatedVisibility(
                    visible = clicked,
//                    enter = expandVertically(expandFrom = Alignment.Top),
//                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                    enter = slideIn(
                        initialOffset = { inoff -> IntOffset( 0, -inoff.height) }
                    ),
                    exit = slideOut(
                        targetOffset = { inoff -> IntOffset(0, -inoff.height) }
                    ),
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .clip(RectangleShape)
                ) {
                    IntermediateCalls(startDate, leg, item, 40.dp)
                }
                AnimatedVisibility(
                    visible = !clicked,
//                    enter = expandVertically(expandFrom = Alignment.Top),
//                    exit = shrinkVertically(shrinkTowards = Alignment.Top),
                    enter = slideIn(
                        initialOffset = { inoff -> IntOffset( 0, inoff.height) }
                    ),
                    exit = slideOut(
                        targetOffset = { inoff -> IntOffset(0, inoff.height) }
                    ),
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .clip(RectangleShape)
                ) {
                    NoIntermediateCalls(startDate, endDate, leg, item, 80.dp, false)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 50.dp)
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
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.background)
                )

                val modeFoot = item.mode != Transports.Foot.mode

                val text = if (modeFoot) {
                    leg?.line?.publicCode.toString()
                } else {
                    "${(leg?.duration.toString().toInt() / 60.0).toInt()} min"
                }

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                    ,
                    textAlign = TextAlign.Center,
                    text = text,
                    color = MaterialTheme.colors.background,
                    fontSize = 14.sp
                )

                // symbol end
            }
        }

    // box end
    }
// end
}

@Composable
fun IntermediateCalls(
    date: String,
    leg:  FindTripQuery.Leg?,
    item: Transports,
    lineLength: Dp
) {

    Column (
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TripLine(
            startText = leg?.fromPlace?.name.toString(),
            startTime = date,
            endText = "",
            endTime = "",
            lineColour = item.color,
            nodeColour = MaterialTheme.colors.onSurface,
            canvasHeight = lineLength
        )
        leg?.intermediateEstimatedCalls?.forEach { EC ->

            val dateAsRaw = EC?.expectedArrivalTime.toString()
            val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
            val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)

            TripLine(
                startText = EC?.quay?.name.toString(),
                startTime = dateAsString,
                lineColour = item.color,
                nodeColour = MaterialTheme.colors.onSurface,
                canvasHeight = lineLength
            )
        }
    }

}

@Composable
fun NoIntermediateCalls(
    startDate: String,
    endDate: String,
    leg:  FindTripQuery.Leg?,
    item: Transports,
    lineLength: Dp,
    lineExpanded: Boolean = true,
) {

    TripLine(
        startText = leg?.fromPlace?.name.toString(),
        startTime = startDate,
        endText = leg?.toPlace?.name.toString(),
        endTime = endDate,
        lineColour = item.color,
        nodeColour = MaterialTheme.colors.onSurface,
        canvasHeight = lineLength,
        lineExpanded = lineExpanded
    )

}
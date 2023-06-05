package no.tepohi.projectepta.ui.components.entur

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripLeg(
    leg:  FindTripQuery.Leg?,
    item: Transports,
    color: Color
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
            NoIntermediateCalls(startDate, endDate, leg, color, 80.dp)
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
                    IntermediateCalls(startDate, leg, color, 40.dp)
                }
                AnimatedVisibility(
                    visible = !clicked,
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
                    NoIntermediateCalls(startDate, endDate, leg, color, 80.dp, false)
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            val modeFoot = item.mode != Transports.Foot.mode

            TransportLabel(
                item = item,
                color = color,
                text = if (modeFoot) {
                    leg?.line?.publicCode.toString()
                } else {
                    "${(leg?.duration.toString().toInt() / 60.0).toInt()} min"
                },
            )
        }

    // box end
    }
// end
}

@Composable
fun IntermediateCalls(
    date: String,
    leg:  FindTripQuery.Leg?,
    color: Color,
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
            lineColour = color,
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
                lineColour = color,
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
    color: Color,
//    item: Transports,
    lineLength: Dp,
    lineExpanded: Boolean = true,
) {

    TripLine(
        startText = leg?.fromPlace?.name.toString(),
        startTime = startDate,
        endText = leg?.toPlace?.name.toString(),
        endTime = endDate,
        lineColour = color,
        nodeColour = MaterialTheme.colors.onSurface,
        canvasHeight = lineLength,
        lineExpanded = lineExpanded
    )

}
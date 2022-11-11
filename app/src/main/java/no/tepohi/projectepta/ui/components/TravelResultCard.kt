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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.DepartureBoardQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomDepartureResultCard(
    EstimatedCall: DepartureBoardQuery.EstimatedCall
) {
//
//    val mode = EstimatedCall.serviceJourney?.journeyPattern?.line?.transportMode?.name ?: "none"
//    val line = EstimatedCall.serviceJourney?.journeyPattern?.line?.publicCode ?: "none"
//
//    val items = listOf(
//        Transports.Bus,
//        Transports.Tram,
//        Transports.Metro,
//        Transports.Rail,
//    )
//
//    Row(
//        horizontalArrangement = Arrangement.SpaceBetween,
//        modifier = Modifier
//            .fillMaxWidth()
//            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//            .clip(RoundedCornerShape(Constants.CORNER_RADIUS))
//            .clickable(enabled = true) {
//
//            }
//            .padding(Constants.PADDING_INNER)
//    ) {
//
//        Column(
//            horizontalAlignment = Alignment.Start,
//            modifier = Modifier
////                            .weight(3f)
//                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//        ) {
//
//            items.forEach { item ->
//
//                if (item.mode == mode) {
//
//                    Row(
//                        horizontalArrangement = Arrangement.Start,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .width(78.dp)
//                            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//                            .background(c, RoundedCornerShape(4.dp))
//                            .padding(2.dp)
//                    ) {
//
//                        Image(
//                            modifier = Modifier
//                                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//                            ,
//                            painter = painterResource(id = item.iconTableId),
//                            contentDescription = item.mode,
////                                                colorFilter = ColorFilter.tint( Color.White)
//                        )
//
//                        Text(
//                            modifier = Modifier
//                                .weight(1f)
//                                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//                            ,
//                            textAlign = TextAlign.Center,
//                            text = line,
//                            color = Color.Black,
//                            fontSize = 14.sp
//                        )
//                    }
//                }
//            }
//        }
//
//        val aimedTimeAsRaw = EstimatedCall.aimedArrivalTime.toString()
//        val aimedTimeAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(aimedTimeAsRaw)!!
//        val aimedTimeAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(aimedTimeAsObject)
//
//        val expectedTimeAsRaw = EstimatedCall.expectedArrivalTime.toString()
//        val expectedTimeAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(expectedTimeAsRaw)!!
//        val expectedTimeAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(expectedTimeAsObject)
//
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
////                            .weight(1f)
//                .width(55.dp)
//                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//        ) {
//
//            if (aimedTimeAsString.equals(expectedTimeAsString)) {
//                Text(text = aimedTimeAsString, textAlign = TextAlign.Center)
//                Text(text = expectedTimeAsString, textAlign = TextAlign.Center, color = Color.Transparent)
//            }
//            else {
//                Text(text = aimedTimeAsString, textAlign = TextAlign.Center, textDecoration = TextDecoration.LineThrough)
//                Text(text = expectedTimeAsString, textAlign = TextAlign.Center)
//            }
//        }
//
//    }
////                Divider()
//
}
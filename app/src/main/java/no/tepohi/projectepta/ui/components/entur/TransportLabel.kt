package no.tepohi.projectepta.ui.components.entur

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor

@Composable
fun TransportLabel(
    item: Transports,
    color: Color,
    text: String,
//    leg: FindTripQuery.Leg?,
    width: Dp = 78.dp,
) {

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(width)
            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            .background(color, RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {

        Image(
            modifier = Modifier
                .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
            ,
            painter = painterResource(id = item.iconTableId),
            contentDescription = item.mode,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.background)
        )

//        val modeFoot = item.mode != Transports.Foot.mode

//        val text = if (leg != null) {
//            if (modeFoot) {
//                leg.line?.publicCode.toString()
//            } else {
//                "${(leg.duration.toString().toInt() / 60.0).toInt()} min"
//            }
//        } else item.mode

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
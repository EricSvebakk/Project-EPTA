package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.LatLng
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.ui.screens.decode
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Transports
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomResultCard(
    tripPattern: FindTripQuery.TripPattern,
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

    var polylines: ArrayList<polyline> = arrayListOf()

    Card(
        elevation = 10.dp
    ) {

        Column() {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
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

                        val result = decode(leg?.pointsOnLink?.points.toString())

                        val hex = leg?.line?.presentation?.colour

                        val c = if (hex != null)
                            Color(android.graphics.Color.parseColor("#$hex"))
                        else
                            Color(148, 148, 148, 255)

                        val pl = polyline(
                            result,
                            c,
                            leg?.fromPlace?.name ?: "",
                            item.iconMapId
                        )

                        polylines.add(pl)

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(
                        bottom = Constants.PADDING_INNER,
                        end = Constants.PADDING_INNER,
                    )
                    .fillMaxWidth()
            ) {
                CustomButton(
                    content = "show",
                    onClick = {
                        searchViewModel.polylines.postValue(polylines)
                        settingsViewModel.showTripsData.postValue(false)
                    }
                )
            }
        }
    }

}

data class polyline(val points: List<LatLng>, val color: Color, val text: String, val startPointIconId: Int)
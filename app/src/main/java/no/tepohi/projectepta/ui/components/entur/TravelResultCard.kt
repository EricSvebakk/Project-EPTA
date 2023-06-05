package no.tepohi.projectepta.ui.components.entur

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
import no.tepohi.projectepta.ui.components.CustomButton
import no.tepohi.projectepta.ui.methods.decode
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.testColor
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TravelResultCard(
    tripPattern: FindTripQuery.TripPattern,
    searchViewModel: SearchViewModel,
    settingsViewModel: SettingsViewModel
) {

    val polylines: ArrayList<polyline> = arrayListOf()

    Card(
        elevation = 10.dp
    ) {

        // places travel-results over show-map-button
        Column {

            //
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Constants.PADDING_INNER)
            ) {

                // Orders tripPatterns correctly
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
                        .padding(Constants.PADDING_INNER)
                ) {

                    tripPattern.legs.forEach { leg ->

                        val item = Constants.allTransports.find { it.mode == leg?.mode.toString() }!!

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

                        TripLeg(leg, item, c)
                        polylines.add(pl)
                    }
                }

                val dateAsRaw = tripPattern.expectedStartTime.toString()
                val dateAsObject = SimpleDateFormat(Constants.ENTUR_FORMAT, Locale.getDefault()).parse(dateAsRaw)!!
                val dateAsString = SimpleDateFormat(Constants.TRIP_TIME, Locale.getDefault()).format(dateAsObject)
                val duration = (tripPattern.duration.toString().toInt() / 60.0).toInt().toString()

                // Top right text
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

            // Button
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
                        settingsViewModel.showFromSearch.postValue(false)
                    }
                )
            }
        }
    }

}

data class polyline(val points: List<LatLng>, val color: Color, val text: String, val startPointIconId: Int)
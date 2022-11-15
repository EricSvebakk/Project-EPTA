package no.tepohi.projectepta.ui.components
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.FilterAlt
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.google.android.gms.maps.model.LatLng
//import no.tepohi.projectepta.ui.theme.Constants
//import java.util.*
//
//class ControlPanel {
//
//    @Composable
//    fun drawControlPanel(
//        searchOnClick: () -> Unit,
//
//    ) {
//
//        Row {
//            IconButton(
//                modifier = Modifier
//                    .background(
//                        color = MaterialTheme.colors.background,
//                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
//                    )
//                    .border(
//                        width = 1.5.dp,
//                        color = MaterialTheme.colors.primary,
//                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
//                    )
//                    .size(40.dp)
//                ,
//                content = {
//                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colors.primary)
//                },
//                onClick = {
//
//                    searchOnClick()
//
//                    val toPlaceLatLng = searchViewModel.toPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
//                    val fromPlaceLatLng = searchViewModel.fromPlaceResult.value?.pos ?: LatLng(0.0, 0.0)
//
//                    Log.d("PlaceLatLng tag", "$toPlaceLatLng $fromPlaceLatLng")
//
//                    val modes = settingsViewModel.filter.value?.toList() ?: emptyList()
//
//                    enturViewModel.loadTrips(
//                        start = fromPlaceLatLng,
//                        end = toPlaceLatLng,
//                        time = dateTime ?: Calendar.getInstance(),
//                        modes = modes
//                    )
//
//                    settingsViewModel.showTripsData.postValue(true)
//                },
//            )
//            Spacer(modifier = Modifier.width(5.dp))
//            CustomButton(
//                content = timeString ?: "",
//                onClick = {
//                    settingsViewModel.showTimePicker.postValue(!showTimePicker!!)
//                }
//            )
//            Spacer(modifier = Modifier.width(5.dp))
//            CustomButton(
//                content = dateString ?: "",
//                onClick = {
//                    settingsViewModel.showDatePicker.postValue(!showDatePicker!!)
//                }
//            )
//            Spacer(modifier = Modifier.width(5.dp))
//            IconButton(
//                modifier = Modifier
//                    .background(
//                        color = MaterialTheme.colors.background,
//                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
//                    )
//                    .border(
//                        width = 1.5.dp,
//                        color = MaterialTheme.colors.primary,
//                        shape = RoundedCornerShape(Constants.CORNER_RADIUS + 15.dp)
//                    )
//                    .size(40.dp)
//                ,
//                content = {
//                    Icon(imageVector = Icons.Filled.FilterAlt, contentDescription = "Filter", tint = MaterialTheme.colors.primary)
//                },
//                onClick = {
//                    settingsViewModel.showFilterOptions.postValue(true)
//                },
//            )
//        }
//
//        // draw end
//    }
//
//}
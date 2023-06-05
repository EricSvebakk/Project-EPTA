package no.tepohi.projectepta.ui.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.example.DepartureBoardQuery
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.example.type.TransportMode
import no.tepohi.projectepta.ui.sources.EnturDataSource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class EnturViewModel: ViewModel() {

    private val dataSource = EnturDataSource()
    val stopsData = MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>>()

    val tripsData = MutableLiveData<List<FindTripQuery.TripPattern>>()
    val selectedTripData = MutableLiveData<FindTripQuery.TripPattern>()

    val departuresData = MutableLiveData<List<DepartureBoardQuery.EstimatedCall>>()

//    val newStopsData = MutableLiveData<List<StopData>>()

    fun loadStops() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {

                stopsData.postValue(it)

                Log.d("loadstops?", it.toString())
            }
        }
    }

    fun loadTrips(
        start: LatLng,
        end: LatLng,
        modes: List<TransportMode?>,
        time: Calendar = Calendar.getInstance(),
        numTrips: Int = 12,
    ) {
        val timeString = SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ssXXX", Locale.getDefault()).format(time.time)

//        Log.d("sdv", stopsData.value.toString())

//        stopsData.value?.forEach {
//            Log.d("test tagx", it.toString())
//        }

        Log.d("start/end", "$start $end")

//        if (stopsData.value != null) {
//
//            var startClosest = stopsData.value!![0]!!
//            var startBestDistance = Double.POSITIVE_INFINITY
//
//            var endClosest = stopsData.value!![0]!!
//            var endBestDistance = Double.POSITIVE_INFINITY
//
//            stopsData.value!!.forEach { next ->
//                if (next != null) {
//
//                    val startDist = start.distanceTo(
//                        latitude = next.latitude ?: 0.0,
//                        longitude = next.longitude ?: 0.0
//                    )
//
//                    if (startDist < startBestDistance) {
//                        startBestDistance = startDist
//                        startClosest = next
//                    }
//
//                    val endDist = end.distanceTo(
//                        latitude = next.latitude ?: 0.0,
//                        longitude = next.longitude ?: 0.0
//                    )
//
//                    if (endDist < endBestDistance) {
//                        endBestDistance = endDist
//                        endClosest = next
//                    }
//                }
//            }
//
//            Log.d("closest", "${startClosest.name} ${endClosest.name}")
//
//        }
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchTrips(
                fromLatLng = start, // LatLng(startClosest.latitude!!, startClosest.longitude!!),
                toLatLng = end, // LatLng(endClosest.latitude!!, endClosest.longitude!!),
                time = timeString,
                modes = modes,
                numTrips = numTrips
//                    startClosest.name, endClosest.name, timeString
            ).also {
                tripsData.postValue(it)
            }
        }

    }

    fun loadDepartures(start: LatLng) {

        if (stopsData.value != null) {

            var startClosest = stopsData.value!![0]!!
            var startBestDistance = Double.POSITIVE_INFINITY

            stopsData.value!!.forEach { next ->
                if (next != null) {

                    val startDist = start.distanceTo(
                        latitude = next.latitude ?: 0.0,
                        longitude = next.longitude ?: 0.0
                    )

                    if (startDist < startBestDistance) {
                        startBestDistance = startDist
                        startClosest = next
                    }
                }
            }

            viewModelScope.launch(Dispatchers.IO) {
                dataSource.fetchDepartures(
                    stopPlaceId = startClosest.id
                ).also {
                    departuresData.postValue(it)
                }
            }
        }


//        Log.d("loadDepartures tag", departuresData.value.toString())

    }

    val startPointData = MutableLiveData<AutocompletePrediction>()
    val endPointData = MutableLiveData<AutocompletePrediction>()
    val departurePointData = MutableLiveData<AutocompletePrediction>()

}

private fun LatLng.distanceTo(
    latitude: Double?,
    longitude: Double?,
//    endPoint: LatLng
): Double {

    return if (latitude != null && longitude != null) {

        val startDeltaX = this.longitude - longitude
        val startDeltaY = this.latitude - latitude

        sqrt((startDeltaX * startDeltaX) + (startDeltaY * startDeltaY))

    } else {
        Log.d("ERROROROROR", "oi")
        Double.POSITIVE_INFINITY
    }

}
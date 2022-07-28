package no.tepohi.projectepta.ui.viewmodels

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.Spinner
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
import no.tepohi.projectepta.ui.sources.EnturDataSource
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sqrt

class MainActivityViewModel: ViewModel() {

    private val dataSource = EnturDataSource()
    val stopsData = MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>>()

    val tripsData = MutableLiveData<List<FindTripQuery.TripPattern>>()
    val selectedTripData = MutableLiveData<FindTripQuery.TripPattern>()
    val showTripsData = MutableLiveData(false)

    val departuresData = MutableLiveData<List<DepartureBoardQuery.EstimatedCall>>()

    fun loadStops() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {
                stopsData.postValue(it)
            }
        }
    }

    fun loadTrips(
        start: LatLng,
        end: LatLng,
        time: Calendar = Calendar.getInstance()
    ) {
        val timeString = SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ssXXX").format(time.time)

        Log.d("sdv", stopsData.value.toString())
        Log.d("start/end", "$start $end")

        if (stopsData.value != null) {

            var startClosest = stopsData.value!![0]!!
            var startBestDistance = Double.POSITIVE_INFINITY

            var endClosest = stopsData.value!![0]!!
            var endBestDistance = Double.POSITIVE_INFINITY

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

                    val endDist = end.distanceTo(
                        latitude = next.latitude ?: 0.0,
                        longitude = next.longitude ?: 0.0
                    )

                    if (endDist < endBestDistance) {
                        endBestDistance = endDist
                        endClosest = next
                    }
                }
            }

            Log.d("closest", "${startClosest.name} ${endClosest.name}")

            viewModelScope.launch(Dispatchers.IO) {
                dataSource.fetchTrips(
                    fromLatLng = LatLng(startClosest.latitude!!, startClosest.longitude!!),
                    toLatLng = LatLng(endClosest.latitude!!, endClosest.longitude!!),
                    time = timeString,
//                    startClosest.name, endClosest.name, timeString
                ).also {
                    tripsData.postValue(it)
                }
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

    fun getSelectedTrip(): FindTripQuery.TripPattern? {
        return selectedTripData.value
    }


    var showMapSettings = MutableLiveData(false)

    fun toggleShowMapSettings() {
        showMapSettings.postValue(!(showMapSettings.value ?: false))
//        gesturesEnabled.postValue(!(gesturesEnabled.value ?: true))
    }

    val startPointData = MutableLiveData<AutocompletePrediction>()
    val endPointData = MutableLiveData<AutocompletePrediction>()

    val departurePointData = MutableLiveData<AutocompletePrediction>()

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("kk:mm", Locale.getDefault())
    val dateTime = MutableLiveData(Calendar.getInstance())
    var dateString = MutableLiveData(dateFormat.format(Date()))
    var timeString = MutableLiveData(timeFormat.format(Date()))

    var textFieldFrom = MutableLiveData("Oslo S")
    var textFieldTo = MutableLiveData("Blindern vgs.")

    fun resetDateTime() {
        println(dateTime.value)

        val old = dateTime.value!!
        val new = Calendar.getInstance()

        old.set(Calendar.YEAR, new.get(Calendar.YEAR))
        old.set(Calendar.MONTH, new.get(Calendar.MONTH))
        old.set(Calendar.DAY_OF_MONTH, new.get(Calendar.DAY_OF_MONTH))
        old.set(Calendar.HOUR_OF_DAY, new.get(Calendar.HOUR_OF_DAY))
        old.set(Calendar.MINUTE, new.get(Calendar.MINUTE))

        updateDate(old)
        updateTime(old)

        println(dateTime.value)
    }

    fun selectDate(context: Context) {

        DatePickerDialog(context).also { dpd ->
            dpd.datePicker.minDate = Date().time
            dpd.setOnDateSetListener { _, year, month, day ->
                val date = Calendar.getInstance().also { it.set(year, month, day) }

                updateDate(date)
            }

            val yearID = Resources.getSystem().getIdentifier("year","id","android")
            if (yearID != 0) {
                val year = dpd.findViewById<Spinner>(yearID)
                if (year != null) {
                    year.visibility = View.GONE
                }
            }

        }.show()

    }

    fun selectTime(context: Context) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, hour, minute ->

                val time = Calendar.getInstance().also {
                    it.set(startYear, startMonth, startDay, hour, minute)
                }
                updateTime(time)

            },
            startHour, startMinute, true
        ).show()

    }

    private fun updateDate(c: Calendar) {
        val cal = dateTime.value!!

        cal.set(Calendar.YEAR, c.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, c.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))

        dateTime.postValue(cal)


        val hoy = dateFormat.format(dateTime.value!!.time)
        dateString.postValue(hoy)
            .also { println("updateDate: $it") }
    }

    private fun updateTime(c: Calendar) {
        val cal = dateTime.value!!

        cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE))

        dateTime.postValue(cal)


        val hoy = timeFormat.format(dateTime.value!!.time)
        timeString.postValue(hoy)
            .also { println("updateTime: $it") }
    }

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
package no.tepohi.projectepta.ui.viewmodels

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.example.type.TransportMode
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.Constants.Companion.allTransports
import java.text.SimpleDateFormat
import java.util.*

class SettingsViewModel: ViewModel() {

    var appColorPalette = MutableLiveData(Constants.THEME_LIGHT)

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("kk:mm", Locale.getDefault())

    val dateTime = MutableLiveData(Calendar.getInstance())

    var dateString = MutableLiveData(dateFormat.format(Date()))
    var timeString = MutableLiveData(timeFormat.format(Date()))

    val showTimePicker = MutableLiveData(false)
    val showDatePicker = MutableLiveData(false)
    val showTripsData = MutableLiveData(false)
    val showDepData = MutableLiveData(false)

    val showPopUp = MutableLiveData(false)

    val favouriteStops = MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>>()
    val showPopUpFavouriteStop = MutableLiveData(false)

    val showFilterOptions = MutableLiveData(false)
    val filter = MutableLiveData(allTransports.filter { it.tm != null }.map { it.tm }.toSet())

    val showFromSearch = MutableLiveData(true)

    val numTrips = MutableLiveData(12)


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

    fun updateDate(c: Calendar) {
        val cal = dateTime.value!!

        cal.set(Calendar.YEAR, c.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, c.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))

        dateTime.postValue(cal)


        val hoy = dateFormat.format(dateTime.value!!.time)
        dateString.postValue(hoy)
            .also { println("updateDate: $it") }
    }

    fun updateTime(c: Calendar) {
        val cal = dateTime.value!!

        cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE))

        dateTime.postValue(cal)

        val hoy = timeFormat.format(dateTime.value!!.time)
        timeString.postValue(hoy)

        Log.d("updateTime tag", "${timeString.value}")
    }

}
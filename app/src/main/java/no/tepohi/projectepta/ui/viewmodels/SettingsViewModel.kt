package no.tepohi.projectepta.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.theme.Constants
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
    val showPopUpfavouriteStop = MutableLiveData(false)

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
            .also { println("updateTime: $it") }
    }

}
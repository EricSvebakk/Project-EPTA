package no.tepohi.projectepta.ui.data

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopPlacesByBoundaryQuery
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel: ViewModel() {

    private val dataSource = EnturDataSource()
    val stopsData = MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>>()
    val tripsData = MutableLiveData<List<FindTripQuery.TripPattern>>()
    val selectedTripData = MutableLiveData<FindTripQuery.TripPattern>()

    fun loadStops() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {
                stopsData.postValue(it)
            }
        }
    }

    fun loadTrips(from: String, to: String, time: Calendar) {
        val timeString = SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ssXXX").format(time.time)

        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchTrips(from, to, timeString).also {
                tripsData.postValue(it)
            }
        }
    }

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
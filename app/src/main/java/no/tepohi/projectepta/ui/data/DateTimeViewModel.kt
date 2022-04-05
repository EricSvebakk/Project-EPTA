package no.tepohi.projectepta.ui.data

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.Spinner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*


class DateTimeViewModel: ViewModel() {

    private val dateFormat = SimpleDateFormat("dd MMM, yy")
    private val timeFormat = SimpleDateFormat("kk:mm")
    var timeDate = MutableLiveData(Calendar.getInstance())
    var dateString = MutableLiveData( dateFormat.format(Date()) )
    var timeString = MutableLiveData( timeFormat.format(Date()) )

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
        val cal = timeDate.value!!

        cal.set(Calendar.YEAR, c.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, c.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH))

        timeDate.postValue(cal)


        val hoy = dateFormat.format(timeDate.value!!.time)
        dateString.postValue(hoy)
            .also { println("updateDate: $it") }
    }

    private fun updateTime(c: Calendar) {
        val cal = timeDate.value!!

        cal.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY))
        cal.set(Calendar.MINUTE, c.get(Calendar.MINUTE))

        timeDate.postValue(cal)


        val hoy = timeFormat.format(timeDate.value!!.time)
        timeString.postValue(hoy)
            .also { println("updateTime: $it") }
    }


}

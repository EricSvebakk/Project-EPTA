package no.tepohi.projectepta.ui.data

import android.util.Log
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
    private val stopsData = MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>>()
    val tripsData = MutableLiveData<List<FindTripQuery.TripPattern>>()

    fun loadStops(): MutableLiveData<List<StopPlacesByBoundaryQuery.StopPlacesByBbox?>> {

        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {
                stopsData.postValue(it)
            }
        }
        return stopsData
    }

    fun loadTrips(from: String, to: String, time: Calendar) {
//        : LiveData<List<FindTripQuery.TripPattern>>

        val timeString = SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ssXXX").format(time.time)

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("load trips launch tag", "done! $from $to $timeString")

            dataSource.fetchTrips(from, to, timeString).also {
                Log.d("load trips launch tag", it.toString())
                tripsData.postValue(it)
            }
        }
        Log.d("load trips final tag", "done!")

//        return tripsData
    }
}
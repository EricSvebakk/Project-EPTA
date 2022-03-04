package no.tepohi.projectenturpublictransportapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopsQuery

class MainActivityViewModel: ViewModel() {

    private val dataSource = DataSource()
    val tripsData = MutableLiveData<MutableList<FindTripQuery.TripPattern>>()
    val stopsData = MutableLiveData<MutableList<StopsQuery.StopPlace?>>()

    fun loadTrips(from: String, to: String, bike: Boolean): LiveData<MutableList<FindTripQuery.TripPattern>> {

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("load trips launch tag", "done!")
            dataSource.fetchTrips(from, to, bike).also {
                tripsData.postValue(it)
            }
        }
        Log.d("load trips final tag", "done!")

        return tripsData
    }

    fun loadStops(): MutableLiveData<MutableList<StopsQuery.StopPlace?>> {

        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {
                stopsData.postValue(it)
            }
        }

        return stopsData
    }
}
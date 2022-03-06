package no.tepohi.projectepta

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
    private val stopsData = MutableLiveData<List<StopsQuery.StopPlace?>>()
    private val tripsData = MutableLiveData<List<FindTripQuery.TripPattern>>()

    fun loadStops(): MutableLiveData<List<StopsQuery.StopPlace?>> {

        viewModelScope.launch(Dispatchers.IO) {
            dataSource.fetchStops().also {
                stopsData.postValue(it)
            }
        }
        return stopsData
    }

    fun loadTrips(from: String, to: String, time: String): LiveData<List<FindTripQuery.TripPattern>> {

        viewModelScope.launch(Dispatchers.IO) {
            Log.d("load trips launch tag", "done!")

            dataSource.fetchTrips(from, to, time).also {
                tripsData.postValue(it)
            }
        }
        Log.d("load trips final tag", "done!")

        return tripsData
    }
}
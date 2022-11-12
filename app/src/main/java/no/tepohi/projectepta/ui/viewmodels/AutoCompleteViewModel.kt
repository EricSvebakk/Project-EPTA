package no.tepohi.projectepta.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.projectepta.ui.components.polyline
import no.tepohi.projectepta.ui.sources.AutoCompleteSource
import no.tepohi.projectepta.ui.sources.GeocodeSource
import no.tepohi.projectepta.ui.sources.Result

class AutoCompleteViewModel : ViewModel() {

    // sources
    private val autocompleteSource = AutoCompleteSource()
    private val geocodeSource = GeocodeSource()

    // temporaryStrings
    val searchTempText = MutableLiveData("")
    val searchFromText = MutableLiveData("")
    val searchToText = MutableLiveData("")

    // suggestions
    val autoCompleteSuggestions = MutableLiveData(mutableListOf<AutocompletePrediction>())

    //
    val toPlaceResult = MutableLiveData<Result>()
    val fromPlaceResult = MutableLiveData<Result>()
    val departurePlaceResult = MutableLiveData<Result>()

    // for drawing paths on map
    val polylines = MutableLiveData<ArrayList<polyline>>()

    /**
     * Predicts a list of searchResults based on query-string using autocompleteSource
     */
    fun loadAutoCompleteSuggestions(
        context: Context,
        query: String,
//        startPointData: MutableLiveData<Result>
    ) {

//        autocompleteSource.fetchAutoCompleteSearch(
//            context = context,
//            query = query,
//            searchViewModel = this,
////            mavm = mavm,
////            idk = startPointData
//        )
    }

    /**
     * Translates placeID to Result using geocodeSource
     */
    fun toLoadPlaceResult(sd: StopPlacesByBoundaryQuery.StopPlacesByBbox?) {

        if (sd != null) {
            if (sd.latitude!! > 0 && sd.longitude!! > 0) {
                toPlaceResult.postValue(Result(sd.name, LatLng(sd.latitude, sd.longitude)))
            }
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    geocodeSource.fetchGeoLocation(placeID = sd.id)
                        .also { toPlaceResult.postValue(it) }
                }
            }
        }

//        return placeResult
    }

    fun fromLoadPlaceResult(sd: StopPlacesByBoundaryQuery.StopPlacesByBbox?) {

        if (sd != null) {
            if (sd.latitude!! > 0 && sd.longitude!! > 0.0) {
                fromPlaceResult.postValue(Result(sd.name, LatLng(sd.latitude, sd.longitude)))
            }
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    geocodeSource.fetchGeoLocation(placeID = sd.id)
                        .also { fromPlaceResult.postValue(it) }
                }
            }
        }


//        return placeResult
    }

    fun DeparturePlaceResult(placeID: String) {

        viewModelScope.launch(Dispatchers.IO) {
            geocodeSource.fetchGeoLocation(placeID = placeID)
                .also { departurePlaceResult.postValue(it) }
        }
//        return placeResult
    }

}
package no.tepohi.projectepta.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.tepohi.projectepta.ui.sources.AutoCompleteSource

class SearchViewModel : ViewModel() {

    private val autocompleteSource = AutoCompleteSource()
    private val geocodeSource = GeocodeSource()

//    var mavm: MainActivityViewModel? = null

    val autoCompleteSuggestions = MutableLiveData(mutableListOf<AutocompletePrediction>())
    val toPlaceResult = MutableLiveData<Result>()
    val fromPlaceResult = MutableLiveData<Result>()
    val departurePlaceResult = MutableLiveData<Result>()

    /**
     * Predicts a list of searchResults based on query-string using autocompleteSource
     */
    fun loadAutoCompleteSuggestions(
        context: Context,
        query: String,
//        startPointData: MutableLiveData<Result>
    ) {

        autocompleteSource.fetchAutoCompleteSearch(
            context = context,
            query = query,
            searchViewModel = this,
//            mavm = mavm,
//            idk = startPointData
        )
    }

    /**
     * Translates placeID to Result using geocodeSource
     */
    fun toLoadPlaceResult(placeID: String) {

        viewModelScope.launch(Dispatchers.IO) {
            geocodeSource.fetchGeoLocation(placeID = placeID)
                .also { toPlaceResult.postValue(it) }
        }
//        return placeResult
    }

    fun fromLoadPlaceResult(placeID: String) {

        viewModelScope.launch(Dispatchers.IO) {
            geocodeSource.fetchGeoLocation(placeID = placeID)
                .also { fromPlaceResult.postValue(it) }
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


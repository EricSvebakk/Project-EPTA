package no.tepohi.projectepta.ui.sources

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import no.tepohi.projectepta.BuildConfig
import no.tepohi.projectepta.ui.viewmodels.SearchViewModel
import no.tepohi.projectepta.ui.theme.Constants

class AutoCompleteSource {

    fun fetchAutoCompleteSearch(
        context: Context,
        query: String,
        searchViewModel: SearchViewModel,
//        mavm: MainActivityViewModel,
//        idk: MutableLiveData<Result>
    ) {

        // Initialize the SDK and create PlacesClient
        Places.initialize(context, BuildConfig.MAPS_API_KEY)
        val placesClient = Places.createClient(context)

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        val token = AutocompleteSessionToken.newInstance()

        // Create a RectangularBounds object.
        val bounds = RectangularBounds.newInstance(
            Constants.MAP_BOUNDS_SW,
            Constants.MAP_BOUNDS_NE
        )

        // Use the builder to create a FindAutocompletePredictionsRequest.
        val request = FindAutocompletePredictionsRequest.builder()
            .setLocationRestriction(bounds)
//            .setTypeFilter(TypeFilter.ADDRESS)
//            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->

                val ids = response.autocompletePredictions //.map { it.placeId }.toMutableList()
//                Log.d("loadPlaces fetchAutoCompleteSearch", ids.toString())

//                idk.postValue(ids)

                searchViewModel.autoCompleteSuggestions.postValue(ids)

                val temp = ArrayList<StopData>()

                ids.forEach { sp ->

                    temp.add(
                        StopData(
                            sp.getPrimaryText(null).toString(),
                            sp.placeId,
                            -0.0,
                            -0.0
                        )
                    )
                }

//                searchViewModel.loadPlaces(ids)

//                mavm.startPointData.postValue()


//                for (prediction in response.autocompletePredictions) {
//                    Log.i(ContentValues.TAG, prediction.getPrimaryText(null).toString())
//                    Log.i(ContentValues.TAG, prediction.getSecondaryText(null).toString())
//                    Log.i(ContentValues.TAG, prediction.placeId)
//                }
            }

            .addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e(ContentValues.TAG, "Place not found: $exception")
                }
            }

    }
}
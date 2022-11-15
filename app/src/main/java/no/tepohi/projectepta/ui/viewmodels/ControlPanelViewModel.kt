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
import no.tepohi.projectepta.ui.components.entur.polyline
import no.tepohi.projectepta.ui.sources.AutoCompleteSource
import no.tepohi.projectepta.ui.sources.GeocodeSource
import no.tepohi.projectepta.ui.sources.Result

class ControlPanelViewModel : ViewModel() {



}
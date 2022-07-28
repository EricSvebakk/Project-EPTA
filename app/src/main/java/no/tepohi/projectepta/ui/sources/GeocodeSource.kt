package no.tepohi.projectepta.ui.viewmodels

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import no.tepohi.projectepta.BuildConfig.MAPS_API_KEY


class GeocodeSource {

    private var path = "https://maps.googleapis.com/maps/api/geocode/json?place_id="

    suspend fun fetchGeoLocation(placeID: String): Result? {

        var fullPath = path + placeID
        fullPath += "&key=$MAPS_API_KEY"

        Log.d("test geocode api path", fullPath)

        val gson = Gson()

        return try {
            val response = gson.fromJson(Fuel.get(fullPath).awaitString(), Base::class.java)
            Log.d("test geocode response lat:", response.results?.get(0)?.geometry?.location?.lat.toString())
            Log.d("test geocode response lng:", response.results?.get(0)?.geometry?.location?.lng.toString())

            val lat = response.results?.get(0)?.geometry?.location?.lat ?: 0.0
            val lng = response.results?.get(0)?.geometry?.location?.lng ?: 0.0
            val address = response.results?.get(0)?.formatted_address ?: ""

//            callback(Result(address, LatLng(lat, lng)))

            return Result(address, LatLng(lat, lng))

        } catch (exception: Exception) {
            Log.d("MAIN_ACTIVITY", "A network request exception was thrown: ${exception.message}")
            null
        }

    }

    data class Base(val results: List<Mid>?, val status: String?)

    data class Mid(val address_components: List<AddressComponents>?, val formatted_address: String?, val geometry: Geometry?, val place_id: String?, val types: List<String>?)
    data class AddressComponents(val long_name: String?, val short_name: String?, val types: List<String>?)
    data class Geometry(val location: Location?, val location_type: String?, val viewport: Viewport?)
    data class Location(val lat: Double?, val lng: Double?)
    data class Viewport(val northeast: Northeast?, val southwest: Southwest?)
    data class Northeast(val lat: Double?, val lng: Double?)
    data class Southwest(val lat: Double?, val lng: Double?)
}

data class Result(val address: String, val pos: LatLng)



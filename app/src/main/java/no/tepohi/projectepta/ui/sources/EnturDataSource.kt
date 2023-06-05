package no.tepohi.projectepta.ui.sources

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.google.android.gms.maps.model.LatLng
import no.tepohi.example.DepartureBoardQuery
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.example.StopsQuery
import no.tepohi.example.type.StreetMode
import no.tepohi.example.type.TransportMode
import no.tepohi.example.type.TransportModes
import java.lang.Exception

class EnturDataSource {

    private val path = "https://api.entur.io/journey-planner/v3/graphql"
    private val apolloClient = ApolloClient.Builder().serverUrl(path).build()

    suspend fun fetchStops(): List<StopPlacesByBoundaryQuery.StopPlacesByBbox?> {

        val response = try {

            val query = StopPlacesByBoundaryQuery(
                59.809,
                10.456,
                60.136,
                10.954,
                null
            )

            val temp = apolloClient.query(query).execute()

            Log.d("fetchStops temp", temp.toString())

            temp.data?.stopPlacesByBbox ?: emptyList()
        }

        catch (e: Exception) {
            println("${e.cause}: fetchStops: A network request exception was thrown: ${e.message}")
            emptyList()
        }

        Log.d("fetchStops tag", response.toString())
        return response
    }

    suspend fun fetchTrips(
        fromLatLng: LatLng,
        toLatLng: LatLng,
        time: String,
        modes: List<TransportMode?>? = null,
        numTrips: Int = 12
    ): List<FindTripQuery.TripPattern> {

        val tm = modes?.map { TransportModes(Optional.presentIfNotNull(it)) }

        val response = try {
            val query = FindTripQuery(
                fromLat = fromLatLng.latitude,
                fromLon = fromLatLng.longitude,
                toLat = toLatLng.latitude,
                toLon = toLatLng.longitude,
                date = time,
                accessMode = StreetMode.flexible,
                egressMode = StreetMode.flexible,
                transportModes = tm,
                numTrips = numTrips
            )

            Log.d("fetchTrips tag", query.toString())

            val temp = apolloClient.query(query).execute()
            temp.data?.trip?.tripPatterns ?: emptyList()
        }

        catch (e: Exception) {
            println("fetchTrips: A network request exception was thrown: ${e.message}")
            emptyList()
        }

        return response
    }

    suspend fun fetchDepartures(
        stopPlaceId: String
    ): List<DepartureBoardQuery.EstimatedCall> {

        val response = try {

            val query = DepartureBoardQuery(
                stopPlaceId = stopPlaceId,
                numberOfDepartures = 12
            )

//            Log.d("fetchDepartures tag1", query.toString())

            val temp = apolloClient.query(query).execute()

            temp.data?.stopPlace?.estimatedCalls ?: emptyList()
        }

        catch (e: Exception) {
            println("fetchDepartures: A network request exception was thrown: ${e.message}")
            emptyList()
        }
        Log.d("fetchDepartures tag2", response.toString())

        return response

    }
}
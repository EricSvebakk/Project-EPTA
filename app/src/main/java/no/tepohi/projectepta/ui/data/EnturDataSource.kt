package no.tepohi.projectepta.ui.data

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.google.android.gms.maps.model.LatLng
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopPlacesByBoundaryQuery
import no.tepohi.example.StopsQuery
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
                true
            )

            val temp = apolloClient.query(query).execute()
            temp.data?.stopPlacesByBbox ?: emptyList()
        }

        catch (e: Exception) {
            println("fetchStops: A network request exception was thrown: ${e.message}")
            emptyList()
        }

        Log.d("fetchStops tag", response.toString())
        return response
    }

    suspend fun fetchTrips(from: String, to: String, time: String): List<FindTripQuery.TripPattern> {

        val response = try {
            val query = FindTripQuery(from, to, time)
            val temp = apolloClient.query(query).execute()
            temp.data?.trip?.tripPatterns ?: emptyList()
        }

        catch (e: Exception) {
            println("fetchTrips: A network request exception was thrown: ${e.message}")
            emptyList()
        }

        return response
    }
}
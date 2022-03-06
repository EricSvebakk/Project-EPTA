package no.tepohi.projectepta

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopsQuery
import java.lang.Exception

class DataSource {

    private val path = "https://api.entur.io/journey-planner/v3/graphql"
    private val apolloClient = ApolloClient.Builder().serverUrl(path).build()

    suspend fun fetchStops(): List<StopsQuery.StopPlace?> {

        val response = try {
            val temp = apolloClient.query(StopsQuery()).execute()
            temp.data?.stopPlaces ?: emptyList()
        }

        catch (e: Exception) {
            println("A network request exception was thrown: ${e.message}")
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
            println("A network request exception was thrown: ${e.message}")
            emptyList()
        }

        return response
    }
}
package no.tepohi.projectenturpublictransportapp

import android.util.Log
import com.apollographql.apollo3.ApolloClient
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopsQuery
import java.lang.Exception

class DataSource {

    private val path = "https://api.entur.io/journey-planner/v3/graphql"
    private val apolloClient = ApolloClient.Builder().serverUrl(path).build()

    suspend fun fetchTrips(from: String, to: String): MutableList<FindTripQuery.TripPattern> {

        val response = try {
            val query = FindTripQuery(from, to)
            val temp = apolloClient.query(query).execute()
            temp.data?.trip?.tripPatterns?.toMutableList() ?: emptyList<FindTripQuery.TripPattern>().toMutableList()
        }

        catch (e: Exception) {
            println("A network request exception was thrown: ${e.message}")
            emptyList<FindTripQuery.TripPattern>().toMutableList()
        }

        return response
    }

    suspend fun fetchStops(): MutableList<StopsQuery.StopPlace?> {

        val response = try {
            val temp = apolloClient.query(StopsQuery()).execute()
            temp.data?.stopPlaces?.toMutableList() ?: emptyList<StopsQuery.StopPlace?>().toMutableList()
        }

        catch (e: Exception) {
            println("A network request exception was thrown: ${e.message}")
            emptyList<StopsQuery.StopPlace?>().toMutableList()
        }

        Log.d("fetchStops tag", response.toString())
        return response
    }
}
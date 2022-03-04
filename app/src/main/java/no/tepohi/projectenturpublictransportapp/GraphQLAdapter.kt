package no.tepohi.projectenturpublictransportapp

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.tepohi.example.FindTripQuery
import java.text.SimpleDateFormat
import java.util.*

class GraphQLAdapter(private val dataset: MutableList<FindTripQuery.TripPattern>): RecyclerView.Adapter<GraphQLAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardText1: TextView
        val cardText2: TextView
        val cardText3: TextView

        init {
            cardText1 = view.findViewById(R.id.cardText1)
            cardText2 = view.findViewById(R.id.cardText2)
            cardText3 = view.findViewById(R.id.cardText3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trip_card_element, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {

        val duration = (dataset[pos].duration.toString().toInt() / 60.0).toFloat()
        holder.cardText2.text = "Duration: ${duration.toInt()}min"

        // now time
        val date1 = Date().toString()

        // matches trip time format
        val format = holder.itemView.context.getString(R.string.EnturDateFormat)
        var formatter = SimpleDateFormat(format)
        val date3 = formatter.parse(dataset[pos].expectedStartTime.toString())

        // reformats trip time
        formatter = SimpleDateFormat("HH:mma")
        val date4 = formatter.format(date3)

        Log.d("time tag1", date1)
        Log.d("time tag4", date4)

        holder.cardText1.text = "Departure: $date4"

        var string = ""
        dataset[pos].legs.forEach {

            val method = if (it?.mode.toString() == "foot") {
                "walk ${it!!.distance!!.toInt()}m"
            } else {
                val mode = it?.mode
                val line = it?.line?.id?.split(":")?.get(2)
                "$mode $line"
            }
            string +=  "$method to ${it?.toPlace?.name}\n"
        }

        holder.cardText3.text = string.trim()
    }

    override fun getItemCount() = dataset.size
}
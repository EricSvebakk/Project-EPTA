package no.tepohi.projectepta

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import no.tepohi.example.FindTripQuery
import java.text.SimpleDateFormat

class TripResultAdapter(private val dataset: List<FindTripQuery.TripPattern>): RecyclerView.Adapter<TripResultAdapter.ViewHolder>() {

    private lateinit var moicl: View.OnClickListener

    fun setOnItemClickListener(itemClickListener: View.OnClickListener) {
        moicl = itemClickListener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardText1: TextView
        val cardText2: TextView
        val cardText3: TextView

        init {
            view.tag = this
            view.setOnClickListener(moicl)

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
        holder.cardText2.text = "${duration.toInt()}min"

        // matches trip time format
        val enturFormat = holder.itemView.context.getString(R.string.EnturDateFormat)

        val dateDataset = dataset[pos].expectedStartTime.toString()
        val dateAsObject = SimpleDateFormat(enturFormat).parse(dateDataset)!!
        val dateAsString = SimpleDateFormat("kk:mm").format(dateAsObject)

        Log.d("time tag4", dateAsString)

        holder.cardText1.text = "Departure: $dateAsString"

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
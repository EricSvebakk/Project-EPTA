package no.tepohi.projectepta

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.PagerSnapHelper
import no.tepohi.example.FindTripQuery
import no.tepohi.projectepta.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var tripStops: Map<String, String>
    private lateinit var tripsALl: List<FindTripQuery.TripPattern>

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTime()
        appSetup()
        getStops()

        binding.tripButtonStartQuery.setOnClickListener {
            hideKeyboard()

            val from: String = binding.tripEditTextFrom.text.toString()
            val to: String = binding.tripEditTextTo.text.toString()

            val formatter = SimpleDateFormat(getString(R.string.EnturDateFormat))
            val calendarObj = Calendar.getInstance()

            calendarObj.time = Date()
            calendarObj.set(Calendar.HOUR_OF_DAY, binding.tripTimePicker.hour)
            calendarObj.set(Calendar.MINUTE, binding.tripTimePicker.minute)

            val queryTime = formatter.format(calendarObj.time)

            findTrip(from, to, queryTime, formatter)
        }

        binding.tripButtonShowMap.setOnClickListener {
            Intent(this, MapActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    /**
     * Sets time-format to 24-hour,
     * hides keyboard on Enter,
     * snaps recyclerview to visible selected-page
     */
    private fun appSetup() {

        binding.tripTimePicker.setIs24HourView(true)

        binding.tripEditTextTo.setOnEditorActionListener { _, actionID: Int, _ ->
            binding.tripEditTextTo.dismissDropDown()
            hideKeyboard()
            actionID == EditorInfo.IME_ACTION_DONE
        }

        binding.tripEditTextFrom.setOnEditorActionListener { _, actionID: Int, _ ->
            binding.tripEditTextFrom.dismissDropDown()
            hideKeyboard()
            actionID == EditorInfo.IME_ACTION_DONE
        }

        PagerSnapHelper().attachToRecyclerView(binding.tripRecyclerviewTrips)
    }

    /**
     * Queries stop-data from Entur GraphQL-endpoint
     */
    private fun getStops() {
        binding.waitingForStops.visibility = View.VISIBLE
        viewModel.loadStops().observe(this) {

            tripStops = it.associate { x -> x!!.name to x.id }

            Log.d("stops-arraylist tag", tripStops.toString())

            val adapter = LimitArrayAdapter(
                this,
                R.layout.custom_dropdown_item_1line,
                ArrayList(tripStops.keys),
                4
            )

            binding.tripEditTextFrom.setAdapter(adapter)
            binding.tripEditTextTo.setAdapter(adapter)

            binding.waitingForStops.visibility = View.INVISIBLE
        }
    }

    /**
     * Queries trip-patterns from Entur GraphQL-endpoint
     */
    private fun findTrip(from: String, to: String, queryTime: String, formatter: SimpleDateFormat) {
        binding.tripLoadingCard.visibility = View.VISIBLE

        if (tripStops.containsKey(from) && tripStops.containsKey(to)) {

            viewModel.loadTrips(tripStops[from]!!, tripStops[to]!!, queryTime).observe(this) { trips ->

                tripsALl = trips
                Log.d("trip-patterns tag", trips.toString())

                var result = trips.sortedWith(compareBy { temp ->
                    formatter.parse(temp.expectedStartTime.toString())
                })

                val resultFiltered = result.filter { temp ->
                    formatter.parse(temp.expectedStartTime.toString())!!.after(Date())
                }

                result = if (resultFiltered.size > 0) resultFiltered else result

                binding.tripNumberTrips.text = result.size.toString()
                binding.tripRecyclerviewTrips.adapter = GraphQLAdapter(result)

                binding.tripLoadingCard.visibility = View.INVISIBLE
            }
        }

        else {
            Toast.makeText(this, "Invalid trip arguments", Toast.LENGTH_LONG).show()
            Log.d("invalid tag", "invalid arguments")
            binding.tripLoadingCard.visibility = View.INVISIBLE
        }
    }

    /**
     * Hides soft keyboard
     */
    private fun hideKeyboard() {
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }

    /**
     * Updates view with current/selected time
     */
    private fun updateTime() {
        val hour = String.format("%02d",binding.tripTimePicker.hour)
        val min = String.format("%02d",binding.tripTimePicker.minute)
        binding.tripTimeStart.text = "$hour:$min"
    }

    /**
     * Shows/hides timePicker-view and updates time
     */
    fun toggleTimePickerDialog(v: View) {
        if (binding.tripTimePickerCard.isVisible) binding.tripTimePickerCard.visibility = View.INVISIBLE
        else binding.tripTimePickerCard.visibility = View.VISIBLE
        updateTime()
    }
}

/**
 * Extends ArrayAdapter and limits results shown
 */
class LimitArrayAdapter<T>(
    context: Context,
    textViewResourceId: Int,
    objects: List<T>?,
    num: Int
): ArrayAdapter<T>(context, textViewResourceId, objects!!) {

    private val limit = num
    override fun getCount(): Int {
        return min(limit, super.getCount())
    }

}
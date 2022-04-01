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
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import no.tepohi.example.FindTripQuery.TripPattern
import no.tepohi.projectepta.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var tripStops: Map<String, String>
    private lateinit var tripsAll: List<TripPattern>
    private lateinit var formatter: SimpleDateFormat

    private var queryTime: String = Date().toString()
    private var from: String = ""
    private var to: String = ""

    private var mItemClickListener: View.OnClickListener = View.OnClickListener { view ->

        val viewHolder: RecyclerView.ViewHolder = view.tag as RecyclerView.ViewHolder

        Intent(this, MapActivity::class.java).also { intent ->
            var points = ""

            tripsAll[viewHolder.adapterPosition].legs.forEach { temp ->
                points += "${temp?.pointsOnLink?.points}\n"
            }
            points = points.trim()

            intent.putExtra("pointsOnLink", points)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateTime()
        appSetup()
        getStops()

        binding.tripButtonStartQuery.setOnClickListener {
            hideKeyboard()

            from = binding.tripEditTextFrom.text.toString()
            to = binding.tripEditTextTo.text.toString()

            Calendar.getInstance().also { calendar ->
                calendar.time = Date()
                calendar.set(Calendar.HOUR_OF_DAY, binding.tripTimePicker.hour)
                calendar.set(Calendar.MINUTE, binding.tripTimePicker.minute)
                queryTime = formatter.format(calendar.time)
            }

            getTrips()
        }
    }

    /**
     * Sets time-format to 24-hour,
     * hides keyboard on Enter,
     * snaps recyclerview to visible selected-page
     */
    @SuppressLint("SimpleDateFormat")
    private fun appSetup() {

        formatter = SimpleDateFormat(getString(R.string.EnturDateFormat))

        binding.tripRefresh.setOnRefreshListener(this)
        binding.tripRefresh.post {
            Runnable {
                binding.tripRefresh.isRefreshing = true
                showTrips()
                binding.tripRefresh.isRefreshing = false
            }
        }

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

//        PagerSnapHelper().attachToRecyclerView(binding.tripRecyclerviewTrips)
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
                android.R.layout.simple_dropdown_item_1line,
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
    private fun getTrips() {
        binding.tripLoadingCard.visibility = View.VISIBLE

        if (tripStops.containsKey(from) && tripStops.containsKey(to)) {
            viewModel.loadTrips(tripStops[from]!!, tripStops[to]!!, queryTime).observe(this) {
                tripsAll = it

                showTrips()

                binding.tripLoadingCard.visibility = View.INVISIBLE
            }
        }

        else {
            Toast.makeText(this, "Invalid trip arguments", Toast.LENGTH_LONG).show()
            Log.d("invalid tag", "invalid arguments")
            binding.tripLoadingCard.visibility = View.INVISIBLE
        }
    }

    private fun showTrips() {
        Log.d("trip-patterns tag", tripsAll.toString())

        var result = tripsAll.sortedWith(compareBy { temp ->
            formatter.parse(temp.expectedStartTime.toString())
        })

        val resultFiltered = result.filter { temp ->
            formatter.parse(temp.expectedStartTime.toString())!!.after(Date())
        }

        result = if (resultFiltered.isNotEmpty()) resultFiltered else result

        binding.tripNumberTrips.text = result.size.toString()

        val adapter = TripResultAdapter(result)
        adapter.setOnItemClickListener(mItemClickListener)

        binding.tripRecyclerviewTrips.adapter = adapter
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

    override fun onRefresh() {
        binding.tripRefresh.isRefreshing = true
        getTrips()
        binding.tripRefresh.isRefreshing = false
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
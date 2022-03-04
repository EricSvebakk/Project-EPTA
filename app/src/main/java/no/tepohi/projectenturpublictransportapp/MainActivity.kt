package no.tepohi.projectenturpublictransportapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import no.tepohi.example.FindTripQuery
import no.tepohi.example.StopsQuery
import no.tepohi.projectenturpublictransportapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    private lateinit var mytrips: MutableList<FindTripQuery.TripPattern>
    private lateinit var mystops: Map<String, String>

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var yours: Map<String, String>? = null


        viewModel.loadStops().observe(this) {
            Log.d("stops tag", it.toString())

            yours = it.associate { x -> x!!.name to x.id }
            mystops = yours as Map<String, String>

            Log.d("arraylist tag", yours.toString())

            val adapter = LimitArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(yours!!.keys),
                4
            )

            binding.tripEditTextFrom.setAdapter(adapter)
            binding.tripEditTextTo.setAdapter(adapter)
        }


        binding.tripButtonStartQuery.setOnClickListener {

            binding.tripLoadingCard.visibility = View.VISIBLE

            val from: String = binding.tripEditTextFrom.text.toString()
            val to: String = binding.tripEditTextTo.text.toString()
            val bike: Boolean = binding.tripBikeOnly.isChecked

            if (mystops.containsKey(from) && mystops.containsKey(to)) {

                viewModel.loadTrips(yours!![from]!!, yours!![to]!!, bike).observe(this) {
                    mytrips = it

                    Log.d("tag", it.toString())

                    val formatter = SimpleDateFormat(getString(R.string.EnturDateFormat))
                    var result = it.sortedWith(compareBy { result -> formatter.parse(result.expectedStartTime.toString()) }).toMutableList()

                    if (!bike) {
                        val temp = result.filter { mine -> formatter.parse(mine.expectedStartTime.toString()).after(Date()) }.toMutableList()
                        result = if (temp.size > 0) temp else result
                    }

                    binding.tripNumberTrips.text = result.size.toString()
                    binding.tripRecyclerviewTrips.adapter = GraphQLAdapter(result)

                    Timer().schedule(500) {
                        this@MainActivity.runOnUiThread {
                            binding.tripLoadingCard.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            else {
                binding.tripLoadingCard.visibility = View.INVISIBLE
            }


        }

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

//        binding.tripEditTextTo.setOnClickListener {
//            binding.tripEditTextTo.dismissDropDown()
//            hideKeyboard()
//        }
//        binding.tripEditTextFrom.setOnClickListener {
//            binding.tripEditTextFrom.dismissDropDown()
//            hideKeyboard()
//        }

        PagerSnapHelper().attachToRecyclerView(binding.tripRecyclerviewTrips)

        binding.tripButtonShowMap.setOnClickListener {
            Intent(this, MapActivity::class.java).also {
//                it.putExtra("myData",mydate)
                startActivity(it)
            }
        }
    }

    // Hides soft keyboard
    private fun hideKeyboard() {
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
    }
}

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
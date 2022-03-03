package no.tepohi.projectenturpublictransportapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import no.tepohi.projectenturpublictransportapp.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var yours: Map<String, String>? = null


        viewModel.loadStops().observe(this) {
            Log.d("stops tag", it.toString())

            yours = it.associate { x -> x!!.name to x.id }

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

            if (from != "" && to != "") {
                viewModel.loadTrips(yours!![from]!!, yours!![to]!!).observe(this) {

                    Log.d("tag", it.toString())

                    binding.tripNumberTrips.text = it.size.toString()
                    binding.tripRecyclerviewTrips.adapter = GraphQLAdapter(it)

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

        binding.tripEditTextTo.setOnClickListener {
            binding.tripEditTextTo.dismissDropDown()
            hideKeyboard()


        }

        binding.tripEditTextFrom.setOnClickListener {
            binding.tripEditTextFrom.dismissDropDown()
            hideKeyboard()
        }

        PagerSnapHelper().attachToRecyclerView(binding.tripRecyclerviewTrips)
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
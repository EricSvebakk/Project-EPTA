package no.tepohi.projectepta

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import no.tepohi.projectepta.R
import no.tepohi.projectepta.databinding.ActivityMapBinding

class MapActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding : ActivityMapBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.the_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val legs = viewModel.tripsData.value?.get(0)?.legs
//
//        Log.d("tripsdata tag", viewModel.tripsData.value.toString())
//        Log.d("polyline tag", legs.toString())

//        if (legs != null) {
//            for (leg in legs) {
//
//                val polyline = leg!!.pointsOnLink!!.points.toString()
//                Log.d("polyline tag", polyline)
//
//                val decoded = decode(polyline)
//                Log.d("polyline tag", decoded.toString())
//
//                mMap.addPolyline(
//                    PolylineOptions()
//                        .clickable(true)
//                        .addAll(decoded)
//                )
//            }
//        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(59.91, 10.74)))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(5.0F))

//        viewModel.tripsData.value?.get(0)?.legs?.forEach {
//
//        }
    }

    fun decode(polyline: String): List<LatLng> {
        val coordinateChunks: MutableList<MutableList<Int>> = mutableListOf()
        coordinateChunks.add(mutableListOf())

        for (char in polyline.toCharArray()) {
            // convert each character to decimal from ascii
            var value = char.code - 63

            // values that have a chunk following have an extra 1 on the left
            val isLastOfChunk = (value and 0x20) == 0
            value = value and (0x1F)

            coordinateChunks.last().add(value)

            if (isLastOfChunk)
                coordinateChunks.add(mutableListOf())
        }

        coordinateChunks.removeAt(coordinateChunks.lastIndex)

        var coordinates: MutableList<Double> = mutableListOf()

        for (coordinateChunk in coordinateChunks) {
            var coordinate = coordinateChunk.mapIndexed { i, chunk -> chunk shl (i * 5) }.reduce { i, j -> i or j }

            // there is a 1 on the right if the coordinate is negative
            if (coordinate and 0x1 > 0)
                coordinate = (coordinate).inv()

            coordinate = coordinate shr 1
            coordinates.add((coordinate).toDouble() / 100000.0)
        }

        val points: MutableList<LatLng> = mutableListOf()
        var previousX = 0.0
        var previousY = 0.0

        for(i in 0..coordinates.size-1 step 2) {
            if(coordinates[i] == 0.0 && coordinates[i+1] == 0.0)
                continue

            previousX += coordinates[i + 1]
            previousY += coordinates[i]

            points.add(LatLng(previousX, previousY))
        }
        return points
    }
}
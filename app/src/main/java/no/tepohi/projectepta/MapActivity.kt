package no.tepohi.projectepta

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import no.tepohi.projectepta.databinding.ActivityMapBinding
import java.util.*

class MapActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding : ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.the_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

//        val centre = LatLng(59.91, 10.74)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 15.0F))

        addTripPolyLines()
    }

    private fun addTripPolyLines() {
        val lines = intent.getStringExtra("pointsOnLink")

        if (lines != null) {
            Log.d("stuff tag", lines)

            val temp = LatLngBounds.builder()

            for (line in lines.split("\n")) {

                val decoded = decode(line)
                Log.d("polyline tag", decoded.toString())

                val r = Random()
                val c = Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256))

                decoded.forEach { pos -> temp.include(pos) }

                mMap.addPolyline(
                    PolylineOptions()
                        .clickable(true)
                        .color(c)
                        .visible(true)
                        .addAll(decoded)
                        .zIndex(30f)
                )
            }

            val bounds = temp.build()
            val centre = LatLng(bounds.center.latitude, bounds.center.longitude)
            Log.d("bounds tag", bounds.toString())
            Log.d("bounds tag", centre.toString())

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 13f))
        }
    }

    private fun decode(polyline: String): List<LatLng> {
        val coordinateChunks = mutableListOf<MutableList<Int>>()
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

        val coordinates = mutableListOf<Double>()

        for (coordinateChunk in coordinateChunks) {
            var coordinate = coordinateChunk.mapIndexed { i, chunk -> chunk shl (i * 5) }.reduce { i, j -> i or j }

            // there is a 1 on the right if the coordinate is negative
            if (coordinate and 0x1 > 0)
                coordinate = (coordinate).inv()

            coordinate = coordinate shr 1
            coordinates.add((coordinate).toDouble() / 100000.0)
        }

        val points = mutableListOf<LatLng>()
        var prevLon = 0.0
        var prevLat = 0.0

        for(i in 0 until coordinates.size step 2) {
            if(coordinates[i] == 0.0 && coordinates[i+1] == 0.0)
                continue

            prevLon += coordinates[i + 1]
            prevLat += coordinates[i]

            points.add(LatLng(prevLat, prevLon))
        }
        return points
    }
}
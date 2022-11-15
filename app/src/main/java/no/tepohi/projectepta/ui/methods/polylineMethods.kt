package no.tepohi.projectepta.ui.methods

import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Decodes a polyline that has been encoded using Google's algorithm
 * (http://code.google.com/apis/maps/documentation/polylinealgorithm.html)
 *
 * code derived from : https://gist.github.com/signed0/2031157
 *
 * @param polyline-string
 * @return LatLng-coordinates
 */
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

    val coordinates: MutableList<Double> = mutableListOf()

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

    for(i in 0 until coordinates.size step 2) {
        if(coordinates[i] == 0.0 && coordinates[i+1] == 0.0)
            continue

        previousX += coordinates[i + 1]
        previousY += coordinates[i]

        points.add(
            LatLng(
                round(previousY, 5),
                round(previousX, 5)
            )
        )
    }

    return simplify(points, 0.0001)
}

private fun round(value: Double, precision: Int): Double {
    return (value * (10.0).pow(precision.toDouble())).toInt().toDouble() / (10.0).pow(precision.toDouble())
}

/**
 * https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm
 */
fun simplify(points: List<LatLng>, epsilon: Double): List<LatLng> {
    // Find the point with the maximum distance
    var dmax = 0.0
    var index = 0
    val end = points.size

    for(i in 1..(end - 2)) {
        val d = perpendicularDistance(points[i], points[0], points[end-1])
        if ( d > dmax ) {
            index = i
            dmax = d
        }
    }
    // If max distance is greater than epsilon, recursively simplify
    return if (dmax > epsilon) {
        // Recursive call
        val recResults1: List<LatLng> = simplify(points.subList(0,index+1), epsilon)
        val recResults2: List<LatLng> = simplify(points.subList(index,end), epsilon)

        // Build the result list
        listOf(recResults1.subList(0,recResults1.lastIndex), recResults2).flatMap { it.toList() }
    } else {
        listOf(points[0], points[end - 1])
    }
}

private fun perpendicularDistance(pt: LatLng, lineFrom: LatLng, lineTo: LatLng): Double =
    abs((lineTo.longitude - lineFrom.longitude)*(lineFrom.latitude - pt.latitude) - (lineFrom.longitude- pt.longitude)*(lineTo.latitude - lineFrom.latitude)) /
            sqrt((lineTo.longitude - lineFrom.longitude).pow(2.0) + Math.pow(lineTo.latitude - lineFrom.latitude, 2.0))

package no.tepohi.projectepta.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


sealed class Transports(var description: String, var icon: ImageVector, var color: Color){
    object Foot : Transports("foot", Icons.Filled.DirectionsWalk, Color(94, 200, 231, 151))
    object Bus : Transports("bus", Icons.Filled.DirectionsBus, Color.Red)
    object Tram : Transports("tram", Icons.Filled.Tram, Color.Blue)
    object Metro : Transports("metro", Icons.Filled.DirectionsSubway, Color(194, 78, 0, 255))
    object Rail : Transports("rail", Icons.Filled.Train, Color(0, 100, 40, 255))
}

interface Constants {

    companion object {
        val PADDING_INNER = 10.dp
        val PADDING_OUTER = 20.dp
        val CORNER_RADIUS = 15.dp

        const val ENTUR_FORMAT = "yyyy-MM-dd\'T\'kk:mm:ssXXX"
        const val TRIP_TIME = "kk:mm"

        const val JSON_MAP_DARKMODE = "" +
                "[\n" +
                "  {\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#212121\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"elementType\": \"labels.icon\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#757575\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"elementType\": \"labels.text.stroke\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#212121\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"administrative\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#757575\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"administrative.country\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#9e9e9e\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"administrative.locality\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#bdbdbd\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#757575\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi.park\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#181818\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi.park\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#616161\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi.park\",\n" +
                "    \"elementType\": \"labels.text.stroke\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#1b1b1b\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road\",\n" +
                "    \"elementType\": \"geometry.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#2c2c2c\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#8a8a8a\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road.arterial\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#373737\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road.highway\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#3c3c3c\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road.highway.controlled_access\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#4e4e4e\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road.local\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#616161\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"transit\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#757575\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"water\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#000000\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"water\",\n" +
                "    \"elementType\": \"labels.text.fill\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#3d3d3d\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]"

    }
}


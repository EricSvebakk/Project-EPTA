package no.tepohi.projectepta.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import no.tepohi.example.type.TransportMode
import no.tepohi.projectepta.R


sealed class Transports(
    var mode: String,
    var iconTableId: Int,
    var iconMapId: Int,
    var color: Color,
    var tm: TransportMode?,
    ){

    object Foot : Transports(
        mode = "foot",
        iconTableId = R.drawable.icon_table_walk_24,
        iconMapId = R.drawable.icon_map_walk_36,
        color = Color(37, 195, 241, 151),
        tm = null
    )
    object Bus : Transports(
        mode = "bus",
        iconTableId = R.drawable.icon_table_bus_24,
        iconMapId = R.drawable.icon_map_bus_36,
        color = Color(196, 26, 74, 255),
        tm = TransportMode.bus
    )
    object Tram : Transports(
        mode = "tram",
        iconTableId = R.drawable.icon_table_tram_24,
        iconMapId = R.drawable.icon_map_tram_36,
        color = Color(3, 169, 244, 255),
        tm = TransportMode.tram
    )
    object Metro : Transports(
        mode = "metro",
        iconTableId = R.drawable.icon_table_metro_24,
        iconMapId = R.drawable.icon_map_metro_36,
        color = Color(236, 112, 12, 255),
        tm = TransportMode.metro
    )
    object Rail : Transports(
        mode = "rail",
        iconTableId = R.drawable.icon_table_train_24,
        iconMapId = R.drawable.icon_map_train_36,
        color = Color(20, 162, 77, 255),
        tm = TransportMode.rail
    )
}

interface Constants {

    companion object {
        val PADDING_INNER = 10.dp
        val PADDING_OUTER = 10.dp
        val CORNER_RADIUS = 15.dp

        const val ENTUR_FORMAT = "yyyy-MM-dd\'T\'kk:mm:ssXXX"
        const val TRIP_TIME = "kk:mm"

        val MAP_BOUNDS_SW = LatLng(59.809, 10.456)
        val MAP_BOUNDS_NE = LatLng(60.136, 10.954)
        val MAP_BOUNDS_CENTER = LatLng(
            (MAP_BOUNDS_NE.latitude + MAP_BOUNDS_SW.latitude) / 2,
            (MAP_BOUNDS_NE.longitude + MAP_BOUNDS_SW.longitude) / 2
        )

        const val THEME_LIGHT = "Light theme"
        const val THEME_DARK = "Dark theme"
        const val THEME_CONTRAST = "Contrast theme"
        const val THEME_SYSTEM = "System theme"

        @OptIn(ExperimentalComposeUiApi::class)
        fun Modifier.gesturesDisabled(disabled: Boolean = true): Modifier {
            return if (disabled) {
                pointerInput(Unit) {
                    awaitPointerEventScope {
                        // we should wait for all new pointer events
                        while (true) {
                            awaitPointerEvent(pass = PointerEventPass.Initial)
                                .changes
                                .forEach(PointerInputChange::consume)
                        }
                    }
                }
            } else {
                this
            }
        }

        val allTransports = listOf(
            Transports.Foot,
            Transports.Bus,
            Transports.Tram,
            Transports.Metro,
            Transports.Rail,
        )

//        fun Modifier.moveFocus(
//            focusRequester: FocusRequester? = null,
//            nextFocusRequester: FocusRequester? = null,
//        ) = composed {
//            if (focusRequester != null && nextFocusRequester != null) {
//                this.focusOrder(focusRequester) {
//                    nextFocusRequester.requestFocus()
//                }
//            }
//            else if (focusRequester != null && nextFocusRequester == null) {
//                this.focusOrder(focusRequester)
//            }
//            else {
//                this
//            }
//        }

        const val JSON_MAP = "" +
                "[\n" +
                "  {\n" +
                "    \"featureType\": \"administrative.land_parcel\",\n" +
                "    \"elementType\": \"labels\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi\",\n" +
                "    \"elementType\": \"labels.text\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi.attraction\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"poi.business\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"road.local\",\n" +
                "    \"elementType\": \"labels\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"transit.line\",\n" +
                "    \"elementType\": \"geometry\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"color\": \"#bc2e0b\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"weight\": 4\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  {\n" +
                "    \"featureType\": \"transit.station\",\n" +
                "    \"stylers\": [\n" +
                "      {\n" +
                "        \"visibility\": \"off\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "]"

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



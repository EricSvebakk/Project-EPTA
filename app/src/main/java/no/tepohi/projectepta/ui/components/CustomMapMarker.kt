package no.tepohi.projectepta.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.ContextThemeWrapper
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker

// https://stackoverflow.com/questions/70598043/how-to-use-custom-icon-of-google-maps-marker-in-compose
@Composable
fun CustomMapMarker(
    context: Context,
    position: LatLng,
    title: String,
    onClick: () -> Unit = { },
    snippet: String? = null,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptor(
        context, iconResourceId
    )

    Marker(
        position = position,
        title = title,
        snippet = snippet,
        icon = icon,
        onClick = {
            onClick()
            false
        }
    )
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

//    DrawableCompat.setTint(drawable, Color.CYAN)

    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

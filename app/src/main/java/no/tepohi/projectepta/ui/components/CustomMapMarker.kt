package no.tepohi.projectepta.ui.components

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker

// https://stackoverflow.com/questions/70598043/how-to-use-custom-icon-of-google-maps-marker-in-compose
@Composable
fun CustomMapMarker(
    context: Context,
    position: LatLng,
    title: String,
    snippet: String? = null,
    @DrawableRes iconResourceId: Int
) {
    val icon = bitmapDescriptor(
        context, iconResourceId
    )

    println("icon: $icon")

    Marker(
        position = position,
        title = title,
        snippet = snippet,
        icon = icon,
    )
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
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

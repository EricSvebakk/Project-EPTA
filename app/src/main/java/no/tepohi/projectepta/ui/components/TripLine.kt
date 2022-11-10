package no.tepohi.projectepta.ui.components

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TripLine(
    lineColour: Color,
    nodeColour: Color,
    startText: String = "",
    startTime: String = "",
    endText: String = "",
    endTime: String = "",
    lineExpanded: Boolean = true,
    canvasWidth: Dp = 50.dp,
    canvasHeight: Dp = 100.dp,
    textColor: Int = MaterialTheme.colors.onSurface.toArgb()
) {

    val textPaintLeft = Paint().asFrameworkPaint().apply {
        textAlign = android.graphics.Paint.Align.RIGHT
        isAntiAlias = true
        textSize = 26.sp.value
        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }

    val textPaintRight = Paint().asFrameworkPaint().apply {
        textAlign = android.graphics.Paint.Align.LEFT
        isAntiAlias = true
        textSize = 26.sp.value
        color = textColor
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }

    Canvas(
        modifier = Modifier
//            .padding(Constants.PADDING_OUTER)
            .width(canvasWidth)
            .height(canvasHeight)
            .fillMaxWidth()
//            .border(2.dp, testColor, RoundedCornerShape(Constants.CORNER_RADIUS))
//            .background(MaterialTheme.colors.background, RoundedCornerShape(Constants.CORNER_RADIUS))
    ) {

        val x = size.width / 2
        val y1 = 0f
        val y2 = size.height

        val lineWidth = 8f

        drawLine(
            color = lineColour,
            start = Offset(x, y1),
            end = Offset(x, y2),
            strokeWidth = lineWidth,
            pathEffect = if (lineExpanded) null else {
                PathEffect.dashPathEffect(
                    intervals = floatArrayOf(10f, 5f),
                )
            }
        )
        drawCircle(
            color = nodeColour,
            radius = 15f,
            center = Offset(x, y1)
        )
        drawCircle(
            color = nodeColour,
            radius = 15f,
            center = Offset(x,y2)
        )
        // start left
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                startTime,
                x - 25,
                y1 + 5,
                textPaintLeft
            )
        }
        // start right
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                startText,
                x + 25,
                y1 + 5,
                textPaintRight
            )
        }
        // end left
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                endTime,
                x - 25,
                y2 + 5,
                textPaintLeft
            )
        }
        // end right
        drawIntoCanvas {
            it.nativeCanvas.drawText(
                endText,
                x + 25,
                y2 + 5,
                textPaintRight
            )
        }
    }

}
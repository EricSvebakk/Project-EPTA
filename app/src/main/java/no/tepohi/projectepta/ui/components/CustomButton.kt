package no.tepohi.projectepta.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.tepohi.projectepta.ui.theme.Constants
import no.tepohi.projectepta.ui.theme.customButtonColors

@Composable
fun CustomButton(
    content: String,
    onClick: () -> Unit,
    colors: ButtonColors = customButtonColors(),
    border: Boolean = true
) {

    val contentColor = customButtonColors().contentColor(enabled = true).value

    Button(
        modifier = Modifier
//            .fillMaxHeight()
        ,
        colors = colors,
        shape = RoundedCornerShape(Constants.CORNER_RADIUS),
        border = if (border) BorderStroke(width = 2.dp, color = contentColor) else null,
        contentPadding = PaddingValues(13.dp),
        onClick = { onClick() },
    ) {
        Text(text = content)
    }

}
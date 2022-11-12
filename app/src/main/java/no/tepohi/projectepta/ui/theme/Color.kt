package no.tepohi.projectepta.ui.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val testColor = if (false) Color(0xFFFF0000) else Color.Transparent

val LightColorPalette = AppColors(
    materialColors = lightColors(
        primary = Color(0xFF176638),
        secondary = Color(0xFFDB3A00),
        surface = Color(0xFFFFFFFF),
        background = Color(0xFFFFFFFF),
        onPrimary = Color(0xFFFFFFFF),
        onSecondary = Color(0xFFFFFFFF),
        onSurface = Color(0xFF757575),
        onBackground = Color(0xFF757575),
    )
)

val DarkColorPalette = AppColors(
    materialColors = darkColors(
        primary = Color(0xFF29AE60),
        secondary = Color(0xFFFF6229),
        surface = Color(0xFF222222),
        background = Color(0xFF1B1B1B),
        onPrimary = Color(0xFF000000),
        onSecondary = Color(0xFF000000),
        onSurface = Color(0xFF757575),
        onBackground = Color(0xFF757575),
    )
)


@Composable
fun customSliderColors(): SliderColors = SliderDefaults.colors(
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
    activeTrackColor = MaterialTheme.colors.primary.copy(0.5f),
    inactiveTrackColor = MaterialTheme.colors.onSurface,
    thumbColor = MaterialTheme.colors.primary,
)

@Composable
fun customTextFieldColors(): TextFieldColors = TextFieldDefaults.textFieldColors(
    textColor = MaterialTheme.colors.onSurface,
    placeholderColor = MaterialTheme.colors.onSurface,
    trailingIconColor = MaterialTheme.colors.secondary,
    backgroundColor = MaterialTheme.colors.surface,
//    backgroundColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
)

@Composable
fun customCheckBoxColors(): CheckboxColors = CheckboxDefaults.colors(
    checkmarkColor = MaterialTheme.colors.onSecondary,
    checkedColor = MaterialTheme.colors.secondary,
    uncheckedColor = MaterialTheme.colors.onSurface,
)

@Composable
fun customButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
    backgroundColor = MaterialTheme.colors.surface,
    contentColor = MaterialTheme.colors.onSurface
)
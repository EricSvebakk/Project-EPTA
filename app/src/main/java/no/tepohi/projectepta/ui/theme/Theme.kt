package no.tepohi.projectepta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.Colors
import androidx.compose.runtime.*

// needs sharedPreferences support
//enum class AppColorPalette { THEME1, THEME2, }

@Stable
data class AppColors(
    val materialColors: Colors,
//    val warmColor: Color,
//    val coldColor: Color
)

private val LocalAppColors = staticCompositionLocalOf { LightColorPalette }

@Composable
fun appColors(colorPalette: String, darkTheme: Boolean): AppColors =
    when (colorPalette) {
        Constants.THEME_LIGHT -> LightColorPalette
        Constants.THEME_DARK -> DarkColorPalette
        Constants.THEME_SYSTEM -> if (darkTheme) DarkColorPalette else LightColorPalette
        else -> LightColorPalette
    }

@Composable
fun EptaTheme(
    colorPalette: String = Constants.THEME_LIGHT,
    content: @Composable () -> Unit
) {

    val colors = appColors(
        colorPalette = colorPalette,
        darkTheme = isSystemInDarkTheme()
    )

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colors = colors.materialColors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }

}

object EptaTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

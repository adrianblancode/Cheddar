package co.adrianblan.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    inversePrimary = Color.Black,
    primaryContainer = Light80,
    onPrimaryContainer = Color.White,
    secondary = Orange,
    onSecondary = Color.White,
    secondaryContainer = Orange,
    onSecondaryContainer = Color.White,
    tertiary = Orange,
    onTertiary = Color.White,
    tertiaryContainer = Orange,
    onTertiaryContainer = Orange,
    error = Color.Red,
    onError = Color.White,
    errorContainer = Color.Red,
    onErrorContainer = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Light80,
    onSurfaceVariant = Color.Black,
    inverseSurface = Dark80,
    inverseOnSurface = Color.White,
    outline = Light70,
    scrim = Color.White.copy(alpha = 0.8f)
)

val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    inversePrimary = Color.White,
    primaryContainer = Dark80,
    onPrimaryContainer = Color.Black,
    secondary = DarkOrange,
    onSecondary = Color.Black,
    secondaryContainer = DarkOrange,
    onSecondaryContainer = Color.Black,
    tertiary = DarkOrange,
    onTertiary = Color.Black,
    tertiaryContainer = DarkOrange,
    onTertiaryContainer = DarkOrange,
    error = Color.Red,
    onError = Color.Black,
    errorContainer = Color.Red,
    onErrorContainer = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Dark80,
    onSurfaceVariant = Color.White,
    inverseSurface = Light80,
    inverseOnSurface = Color.Black,
    outline = Dark70,
    scrim = Color.Black.copy(alpha = 0.8f)
)

@Composable
fun AppTheme(
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    /*
    val colors =
        if (isSystemInDarkTheme) {
            darkColors(
                primary = colorResource(R.color.colorPrimary),
                primaryVariant = colorResource(R.color.colorPrimary),
                secondary = colorResource(R.color.colorAccent),
                onPrimary = Color.White,
                onSecondary = Color.White,
                background = colorResource(R.color.colorBackground),
                surface = colorResource(R.color.colorBackground),
            )
        } else {
            lightColors(
                primary = colorResource(R.color.colorPrimary),
                primaryVariant = colorResource(R.color.colorPrimary),
                secondary = colorResource(R.color.colorAccent),
                secondaryVariant = colorResource(R.color.colorAccent),
                onPrimary = Color.Black,
                background = colorResource(R.color.colorBackground),
                surface = colorResource(R.color.colorBackground),
            )
        }
    */

    val colorScheme =
        if (isSystemInDarkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = themeTypography, content = content)
}
package co.adrianblan.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    inversePrimary = Color.Black,
    primaryContainer = Light90,
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
    surface = Light90,
    onSurface = Light70,
    surfaceVariant = Light80,
    onSurfaceVariant = Light90,
    inverseSurface = Dark10,
    inverseOnSurface = Color.White,
    outline = Light90,
    scrim = Color.White.copy(alpha = 0.8f)
)

val DarkColorScheme = darkColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    inversePrimary = Color.White,
    primaryContainer = Dark10,
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
    surface = Dark30,
    onSurface = Dark10,
    surfaceVariant = Dark20,
    onSurfaceVariant = Dark30,
    inverseSurface = Light90,
    inverseOnSurface = Color.Black,
    outline = Dark20,
    scrim = Color.Black.copy(alpha = 0.8f)
)

@Composable
fun AppTheme(
    isSystemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (isSystemInDarkTheme) DarkColorScheme
        else LightColorScheme

    MaterialTheme(colorScheme = colorScheme, typography = themeTypography) {
        Surface(color = colorScheme.background, content = content)
    }
}
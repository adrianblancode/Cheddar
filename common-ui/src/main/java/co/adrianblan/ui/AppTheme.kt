package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import androidx.ui.res.colorResource
import co.adrianblan.ui.extensions.isNightModeActive

@Composable
fun AppTheme(content: @Composable() () -> Unit) {

    val colors =
        if (isNightModeActive()) {
            darkColorPalette(
                primary = colorResource(R.color.colorPrimary),
                primaryVariant = colorResource(R.color.colorPrimary),
                secondary = colorResource(R.color.colorAccent),
                background = Color.Black,
                surface = Color.Black,
                onPrimary = Color.White,
                onSecondary = Color.White
            )
        } else {
            lightColorPalette(
                primary = colorResource(R.color.colorPrimary),
                primaryVariant = colorResource(R.color.colorPrimary),
                secondary = colorResource(R.color.colorAccent),
                secondaryVariant = colorResource(R.color.colorAccent),
                onPrimary = Color.Black
            )
        }

    MaterialTheme(colors = colors, typography = themeTypography, content = content)
}

@Composable
fun isNightModeActive(): Boolean {
    val context = ContextAmbient.current
    return context.resources.isNightModeActive()
}
package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import androidx.ui.res.colorResource
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
import androidx.ui.text.font.font
import androidx.ui.text.font.fontFamily
import androidx.ui.unit.sp
import co.adrianblan.ui.extensions.isNightModeActive

@Composable
fun AppTheme(children: @Composable() () -> Unit) {

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

    MaterialTheme(colors = colors, typography = themeTypography, children = children)
}

@Composable
fun isNightModeActive(): Boolean {
    val context = ContextAmbient.current
    return context.resources.isNightModeActive()
}
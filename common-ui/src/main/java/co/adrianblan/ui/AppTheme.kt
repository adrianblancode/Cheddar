package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.core.ContextAmbient
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import androidx.ui.res.colorResource
import androidx.ui.text.font.FontFamily
import androidx.ui.text.font.FontWeight
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
                onPrimary = Color.Black
            )
        }

    val typography =
        Typography()
            .let {

                // val mediumFont = FontFamily("sans-serif-medium")
                val mediumFont = FontFamily.SansSerif

                it.copy(
                    h1 = it.h1.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500),
                    h2 = it.h2.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500),
                    h3 = it.h3.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500),
                    h4 = it.h4.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500),
                    h5 = it.h5.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500),
                    h6 = it.h6.copy(fontFamily = mediumFont, fontWeight = FontWeight.W500)
                )
            }

    MaterialTheme(colors = colors, typography = typography, children = children)
}

@Composable
fun isNightModeActive(): Boolean {
    val context = ContextAmbient.current
    return context.resources.isNightModeActive()
}
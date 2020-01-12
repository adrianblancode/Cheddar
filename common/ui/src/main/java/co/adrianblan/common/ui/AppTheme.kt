package co.adrianblan.common.ui

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Typography
import androidx.ui.res.colorResource
import androidx.ui.text.font.FontFamily

@Composable
fun AppTheme(children: @Composable() () -> Unit) {
    val colors = ColorPalette(
        primary = +colorResource(R.color.colorPrimary),
        primaryVariant = +colorResource(R.color.colorPrimaryDark),
        secondary = +colorResource(R.color.colorAccent)
    )

    val typography =
        Typography()
            .let {

                val mediumFont = FontFamily("sans-serif-medium")

                it.copy(
                    h1 = it.h1.copy(fontFamily = mediumFont),
                    h2 = it.h2.copy(fontFamily = mediumFont),
                    h3 = it.h3.copy(fontFamily = mediumFont),
                    h4 = it.h4.copy(fontFamily = mediumFont),
                    h5 = it.h5.copy(fontFamily = mediumFont),
                    h6 = it.h6.copy(fontFamily = mediumFont)
                )
            }

    MaterialTheme(colors = colors, typography = typography, children = children)
}
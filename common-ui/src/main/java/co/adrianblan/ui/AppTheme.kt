package co.adrianblan.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import co.adrianblan.ui.utils.isNightModeActive

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val colors =
        if (isNightModeActive()) {
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

    MaterialTheme(colors = colors, typography = themeTypography, content = content)
}

@Composable
fun isNightModeActive(): Boolean {
    return LocalContext.current.resources.isNightModeActive()
}
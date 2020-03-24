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

const val textSecondaryAlpha = 0.87f
const val textTertiaryAlpha = 0.6f

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

    val regular = font(R.font.avenir_roman)
    val medium = font(R.font.avenir_black, FontWeight.W500)
    val semibold = font(R.font.avenir_black, FontWeight.W600)

    val appFontFamily = fontFamily(fonts = listOf(regular, medium, semibold))
    val bodyFontFamily = appFontFamily

    val themeTypography = Typography(
        h4 = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 30.sp
        ),
        h5 = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 24.sp
        ),
        h6 = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp
        ),
        subtitle1 = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        ),
        subtitle2 = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        body1 = TextStyle(
            fontFamily = bodyFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        body2 = TextStyle(
            fontFamily = appFontFamily,
            fontSize = 14.sp
        ),
        button = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        caption = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        ),
        overline = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp
        )
    )

    MaterialTheme(colors = colors, typography = themeTypography, children = children)
}

@Composable
fun isNightModeActive(): Boolean {
    val context = ContextAmbient.current
    return context.resources.isNightModeActive()
}
package co.adrianblan.ui

import android.content.Context
import androidx.compose.material.Typography
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


const val textSecondaryAlpha = 0.87f
const val textTertiaryAlpha = 0.6f

private val regular = Font(R.font.avenir_roman)
private val medium = Font(R.font.avenir_heavy, FontWeight.W500)
private val bold = Font(R.font.avenir_black, FontWeight.W600)

private val appFontFamily = FontFamily(listOf(medium))
private val boldFamily = FontFamily(listOf(bold))
private val bodyFontFamily = FontFamily(listOf(regular))

val themeTypography: Typography =
    Typography(
        h1 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 72.sp
        ),
        h2 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 60.sp
        ),
        h3 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 48.sp
        ),
        h4 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 30.sp
        ),
        h5 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 24.sp
        ),
        h6 = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp
        ),
        subtitle1 = TextStyle(
            fontFamily = boldFamily,
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
            fontWeight = FontWeight.W400,
            fontSize = 16.sp
        ),
        body2 = TextStyle(
            fontFamily = bodyFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        ),
        button = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        caption = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp
        ),
        overline = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp
        )
    )
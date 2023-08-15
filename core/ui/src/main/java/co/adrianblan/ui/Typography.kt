package co.adrianblan.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


const val textSecondaryAlpha = 0.87f
const val textTertiaryAlpha = 0.6f

private val regular = Font(R.font.avenir_roman)
private val medium = Font(R.font.avenir_heavy, FontWeight.W500)
private val bold = Font(R.font.avenir_black, FontWeight.W600)

private val regularFontFamily = FontFamily(listOf(regular))
private val mediumFontFamily = FontFamily(listOf(medium))
private val boldFamily = FontFamily(listOf(bold))

val themeTypography: Typography =
    Typography(
        displayLarge = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 57.sp
        ),
        displayMedium = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 45.sp
        ),
        displaySmall = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 36.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 30.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 26.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 22.sp
        ),
        titleLarge = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        ),
        titleSmall = TextStyle(
            fontFamily = boldFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = regularFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = regularFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = regularFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 12.sp
        ),
        labelLarge = TextStyle(
            fontFamily = mediumFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 14.sp
        ),
        labelMedium = TextStyle(
            fontFamily = mediumFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 12.sp
        ),
        labelSmall = TextStyle(
            fontFamily = mediumFontFamily,
            fontWeight = FontWeight.W500,
            fontSize = 10.sp
        ),
    )

@Preview
@Composable
fun TypographyPreview() {
    AppTheme {
        Column {
            Text(text = "Display Large", style = MaterialTheme.typography.displayLarge)
            Text(text = "Display Medium", style = MaterialTheme.typography.displayMedium)
            Text(text = "Display Small", style = MaterialTheme.typography.displaySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Headline Large", style = MaterialTheme.typography.headlineLarge)
            Text(text = "Headline Medium", style = MaterialTheme.typography.headlineMedium)
            Text(text = "Headline Small", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Title Large", style = MaterialTheme.typography.titleLarge)
            Text(text = "Title Medium", style = MaterialTheme.typography.titleMedium)
            Text(text = "Title Small", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Body Large", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Body Medium", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Body Small", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Label Large", style = MaterialTheme.typography.labelLarge)
            Text(text = "Label Medium", style = MaterialTheme.typography.labelMedium)
            Text(text = "Label Small", style = MaterialTheme.typography.labelSmall)
        }
    }
}
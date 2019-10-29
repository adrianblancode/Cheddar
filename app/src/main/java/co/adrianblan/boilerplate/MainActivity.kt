package co.adrianblan.boilerplate

import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.unaryPlus
import androidx.core.content.ContextCompat
import androidx.ui.core.*
import androidx.ui.graphics.Color
import androidx.ui.layout.Align
import androidx.ui.layout.Center
import androidx.ui.layout.Container
import androidx.ui.layout.Spacing
import androidx.ui.material.*
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.text.font.FontFamily
import androidx.ui.tooling.preview.Preview

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                GreetingView()
            }
        }
    }
}

@Composable
fun GreetingView() {
    Align(alignment = Alignment.TopCenter) {
        TopAppBar(
            title = {
                Text(
                    text = +stringResource(R.string.app_name),
                    style = +themeTextStyle { h6 }
                )
            }
        )
    }
    Center {
        Text(text = "Hello world!", style = (+themeTextStyle { h3 }).copy(color = +themeColor { primary }))
    }
    Align(alignment = Alignment.BottomRight) {
        Container(modifier = Spacing(bottom = 16.dp, right = 8.dp)) {
            Ripple(bounded = true) {
                FloatingActionButton(
                    icon = +imageResource(android.R.drawable.ic_menu_search),
                    color = +themeColor { secondary },
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun AppTheme(children: @Composable() () -> Unit) {
    val colors =
        MaterialColors().copy(
            primary = +colorResource(R.color.colorPrimary),
            primaryVariant = +colorResource(R.color.colorPrimaryDark),
            secondary = +colorResource(R.color.colorAccent)
        )

    val typography =
        MaterialTypography()
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

@Preview
@Composable
fun DefaultPreview() {
    AppTheme {
        GreetingView()
    }
}

@CheckResult(suggest = "+")
private fun colorResource(@ColorRes id: Int) = effectOf<Color> {
    val context = +ambient(ContextAmbient)
    Color(ContextCompat.getColor(context, id))
}
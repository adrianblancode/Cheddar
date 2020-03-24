package co.adrianblan.ui

import androidx.animation.FloatPropKey
import androidx.animation.Infinite
import androidx.animation.LinearEasing
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Draw
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Paint
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.Px
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp

@Preview
@Composable
fun LoadingView(
    textStyle: TextStyle = MaterialTheme.typography().h6,
    modifier: Modifier = Modifier.None
) {
    Container(
        expanded = true,
        padding = EdgeInsets(32.dp),
        modifier = modifier
    ) {

        Row(
            arrangement = Arrangement.Center,
            modifier = LayoutAlign.Bottom
        ) {
            Text(
                text = stringResource(id = R.string.loading_title),
                style = textStyle
            )
            AnimatedEllipsisView(
                fontSize = textStyle.fontSize
            )
        }
    }
}


private val loadingState = FloatPropKey()

private val loadingDefinition = transitionDefinition {
    state(0) { this[loadingState] = 0f }
    state(1) { this[loadingState] = 1f }

    transition {
        loadingState using repeatable<Float> {
            animation = tween {
                easing = LinearEasing
                duration = 1500
            }
            iterations = Infinite
        }
    }
}

// Draws an ellipis which animates
@Composable
private fun AnimatedEllipsisView(fontSize: TextUnit) {

    val size: Dp = with(DensityAmbient.current) { fontSize.toDp() }

    Container(
        height = size,
        width = size * 1.2f,
        modifier = LayoutAlign.BottomCenter
    ) {

        val textColor = MaterialTheme.colors().onBackground

        val selectedPaint = Paint()
            .apply {
                color = MaterialTheme.colors().onBackground
            }

        val disabledPaint = Paint()
            .apply {
                color = textColor.copy(alpha = 0.35f)
            }

        Transition(
            definition = loadingDefinition,
            initState = 0,
            toState = 1
        ) { transitionState ->

            val progress: Float = transitionState[loadingState]
            val dotIndex: Int = ((progress * 3) % 3).toInt()

            Draw { canvas, parentSize ->

                val widthStep: Px = parentSize.width * 0.25f
                val circleOffsetY: Px = parentSize.height * 0.90f

                val circleRadius = (parentSize.width * 0.08f).value

                // Every step one dot is selected
                canvas.drawCircle(
                    Offset(dx = widthStep.value, dy = circleOffsetY.value),
                    radius = circleRadius,
                    paint = if (dotIndex == 0) selectedPaint else disabledPaint
                )
                canvas.drawCircle(
                    Offset(dx = (widthStep * 2).value, dy = circleOffsetY.value),
                    radius = circleRadius,
                    paint = if (dotIndex == 1) selectedPaint else disabledPaint
                )
                canvas.drawCircle(
                    Offset(dx = (widthStep * 3).value, dy = circleOffsetY.value),
                    radius = circleRadius,
                    paint = if (dotIndex == 2) selectedPaint else disabledPaint
                )
            }
        }
    }
}
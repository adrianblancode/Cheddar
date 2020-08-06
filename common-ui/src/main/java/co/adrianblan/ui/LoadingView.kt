package co.adrianblan.ui

import androidx.compose.animation.Transition
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import co.adrianblan.ui.utils.lerp
import kotlin.math.sin

@Preview
@Composable
fun LoadingView(
    textStyle: TextStyle = MaterialTheme.typography.h6,
    modifier: Modifier = Modifier
) {
    Box(
        padding = 32.dp,
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxSize() + modifier
    ) {

        Row(
            horizontalArrangement = Arrangement.Center
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

private val loadingDefinition = transitionDefinition<Int> {
    state(0) { this[loadingState] = 0f }
    state(1) { this[loadingState] = 1f }

    transition {
        loadingState using repeatable(
            iterations = AnimationConstants.Infinite,
            animation = tween(
                easing = LinearEasing,
                durationMillis = 1200
            )
        )
    }
}

// Draws an ellipis which animates
@Composable
private fun AnimatedEllipsisView(fontSize: TextUnit) {

    val size: Dp = with(DensityAmbient.current) { fontSize.toDp() }

    val dotColor = MaterialTheme.colors.onBackground

    val transitionState = transition(
        definition = loadingDefinition,
        toState = 1,
        initState = 0,
    )

    val progress: Float = transitionState[loadingState]

    Box(gravity = ContentGravity.Center,
        modifier = Modifier.preferredSize(width = size * 1.2f, height = size)
            .drawBehind {

                val parentSize = this.size

                val widthStep: Float = (parentSize.width * 0.25f)

                // Positions for the lowest and highest points in animation
                val circleBaseY: Float = (parentSize.height * 0.90f)
                val circleTopY: Float = (parentSize.height * 0.80f)

                val progressAngleBase: Double = progress * Math.PI * 2f
                val maxProgressAngleOffset: Double = Math.PI * 0.7

                val circleRadius: Float = (parentSize.width * 0.08f)

                repeat(3) { index ->

                    // Dots have increasing progress offset to give a wave look
                    val progressAngleOffset: Double =
                        -(maxProgressAngleOffset / 2) * index

                    // [-1, 1]
                    val value = sin(progressAngleBase + progressAngleOffset).toFloat()

                    // Factor applied to negative values
                    val downBounceFactor = 0.25f

                    // Coercion leads to two cycles, positive one animates up and negative is small bounce
                    // [-downBounceFactor, 1]
                    val yFraction =
                        if (value > 0) value
                        else value * downBounceFactor

                    // Normalize to [0, 1]
                    val normalizedYFraction =
                        (yFraction + downBounceFactor / (1f + downBounceFactor))

                    val circleDyValue: Float =
                        lerp(circleBaseY, circleTopY, normalizedYFraction)

                    val circleDxValue: Float = widthStep * (index + 1)

                    drawCircle(
                        center = Offset(circleDxValue, circleDyValue),
                        color = dotColor,
                        radius = circleRadius
                    )
                }
            })
}
package co.adrianblan.ui

import androidx.animation.*
import androidx.animation.AnimationConstants.Infinite
import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Text
import androidx.ui.geometry.Offset
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.*
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

private val loadingDefinition = transitionDefinition {
    state(0) { this[loadingState] = 0f }
    state(1) { this[loadingState] = 1f }

    transition {
        loadingState using repeatable(
            iterations = Infinite,
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

    Transition(
        definition = loadingDefinition,
        initState = 0,
        toState = 1
    ) { transitionState ->

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
}
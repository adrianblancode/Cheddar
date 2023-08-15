package co.adrianblan.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import co.adrianblan.ui.utils.lerp
import kotlin.math.sin

@Composable
fun LoadingText(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
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

@Composable
fun LoadingVisual(
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(54.dp)
        )
    }
}

// Draws an ellipis which animates
@Composable
private fun AnimatedEllipsisView(fontSize: TextUnit) {

    val size: Dp = with(LocalDensity.current) { fontSize.toDp() }

    val dotColor = MaterialTheme.colorScheme.onBackground

    val animationSpec =
        infiniteRepeatable(tween<Float>(durationMillis = 1200, easing = LinearEasing))

    val progress: Float by rememberInfiniteTransition("ellipsis")
        .animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec)

    Box(
        modifier = Modifier
            .size(width = size * 1.2f, height = size)
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

                    // Factor applied to up and down bounce
                    val upBounceFactor = 0.6f
                    val downBounceFactor = 0.25f

                    // Coercion leads to two cycles, positive one animates up and negative is small bounce
                    // [-downBounceFactor, 1]
                    val yFraction =
                        if (value > 0) value * upBounceFactor
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

@Preview
@Composable
private fun LoadingTextPreview() {
    AppTheme {
        LoadingText()
    }
}

@Preview
@Composable
private fun LoadingVisualPreview() {
    AppTheme {
        LoadingVisual()
    }
}

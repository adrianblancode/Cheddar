package co.adrianblan.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.adrianblan.ui.utils.lerp

private const val AnimationTime = 1500

/** View with a shimmering effect, for loading content */
@Composable
fun ShimmerView(modifier: Modifier) {

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val shimmerColor = MaterialTheme.colorScheme.onSurfaceVariant

    val animationSpec = remember {
        val clock = System.currentTimeMillis()

        infiniteRepeatable(
            tween<Float>(durationMillis = AnimationTime, easing = LinearEasing),
            initialStartOffset = StartOffset(
                (clock % AnimationTime).toInt(),
                StartOffsetType.FastForward
            )
        )
    }

    val progress by rememberInfiniteTransition("progress")
        .animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec)

    val offset by remember(progress) {
        derivedStateOf {
            // Convert from [0, 1] to [-1, 2]
            lerp(-1f, 2f, progress)
        }
    }

    Box(modifier = modifier
        .graphicsLayer(shape = RoundedCornerShape(2.dp), clip = true)
        .drawBehind {

            val parentSize = this.size

            val rect = parentSize.toRect()
            val width = parentSize.width

            val left = width * offset

            val shimmerGradient =
                Brush.linearGradient(
                    colors = listOf(backgroundColor, shimmerColor, backgroundColor),
                    start = Offset(left, 0f),
                    end = Offset(left + width, 0f)
                )

            drawRect(
                topLeft = Offset(0f, 0f),
                size = Size(width = rect.width, height = rect.height),
                brush = shimmerGradient
            )
        }
    )
}

@Preview
@Composable
private fun ShimmerViewPreview() {
    AppTheme(true) {
        ShimmerView(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(40.dp)
        )
    }
}
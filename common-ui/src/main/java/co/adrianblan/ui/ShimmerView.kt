package co.adrianblan.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.StartOffsetType
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import co.adrianblan.ui.utils.lerp

private const val AnimationTime = 1500

/** View with a shimmering effect, for loading content */
@Preview
@Composable
fun ShimmerView() {

    val backgroundColor = colorResource(id = R.color.contentMuted)
    val shimmerColor = colorResource(id = R.color.contentShimmer)

    val clock = System.currentTimeMillis()

    val animationSpec = infiniteRepeatable(
        tween<Float>(durationMillis = AnimationTime, easing = LinearEasing),
        initialStartOffset = StartOffset((clock % AnimationTime).toInt(), StartOffsetType.FastForward)
    )

    val progress by rememberInfiniteTransition("kek")
        .animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {

                val parentSize = this.size

                val rect = parentSize.toRect()
                val width = parentSize.width

                // Convert from [0, 1] to [-1, 2]
                val offset = lerp(-1f, 2f, progress)
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
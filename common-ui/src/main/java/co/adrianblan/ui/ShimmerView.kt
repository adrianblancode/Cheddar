package co.adrianblan.ui

import androidx.animation.AnimationClockObserver
import androidx.compose.*
import androidx.ui.core.AnimationClockAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.drawBehind
import androidx.ui.foundation.Box
import androidx.ui.geometry.Offset
import androidx.ui.geometry.Size
import androidx.ui.geometry.toRect
import androidx.ui.graphics.LinearGradient
import androidx.ui.layout.fillMaxSize
import androidx.ui.res.colorResource
import androidx.ui.tooling.preview.Preview
import co.adrianblan.ui.utils.lerp

/** View with a shimmering effect, for loading content */
@Preview
@Composable
fun ShimmerView() {

    val backgroundColor = colorResource(id = R.color.contentMuted)

    val shimmerColor = colorResource(id = R.color.contentShimmer)

    var progress by state { 0f }

    val observer = remember {
        object : AnimationClockObserver {
            override fun onAnimationFrame(frameTimeMillis: Long) {
                val animationTime = 1500f
                progress = (frameTimeMillis % animationTime) / animationTime
            }
        }
    }

    // All shimmers should have synchronized animation
    val animationClock = AnimationClockAmbient.current

    onCommit(animationClock) {
        animationClock.subscribe(observer)

        onDispose {
            animationClock.unsubscribe(observer)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .drawBehind {

                val parentSize = this.size

                val rect = parentSize.toRect()
                val width = parentSize.width

                // Convert from [0, 1] to [-1, 2]
                val offset = lerp(-1f, 2f, progress)
                val left = width * offset

                val shimmerGradient =
                    LinearGradient(
                        0f to backgroundColor,
                        0.5f to shimmerColor,
                        1f to backgroundColor,
                        startX = left,
                        endX = left + width,
                        startY = 0f,
                        endY = 0f
                    )

                drawRect(
                    topLeft = Offset(0f, 0f),
                    size = Size(width = rect.width, height = rect.height),
                    brush = shimmerGradient
                )
            }
    )
}
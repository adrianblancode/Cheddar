package co.adrianblan.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawShadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.*

@Composable
fun CollapsingToolbar(
    scrollState: ScrollState,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 128.dp,
    toolbarContent: @Composable (collapsedFraction: Float, height: Dp) -> Unit
) {

    val totalCollapseDistance: Dp = maxHeight - minHeight

    with(DensityAmbient.current) {

        // 1f is fully collapsed
        val collapsedFraction: Float =
            minOf(scrollState.value.toDp() / totalCollapseDistance, 1f)

        val height = minHeight + (maxHeight - minHeight) * (1f - collapsedFraction)

        val elevation = lerp(0.dp, 8.dp, collapsedFraction)

        val insets = InsetsAmbient.current
        val topInsets = insets.top.toDp()

        // Toolbar background
        Column {
            Surface(color = MaterialTheme.colors.primary.copy(alpha = overInsetAlpha)) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .preferredHeight(height + topInsets)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = topInsets)
                ) {
                    toolbarContent(collapsedFraction, height)
                }
            }

            // Shape which starts at bottom of toolbar, and extends a few dp
            val toolbarShadowShape: Shape = remember {
                object : Shape {
                    override fun createOutline(size: Size, density: Density): Outline =
                        Outline.Rectangle(
                            Rect(
                                0f,
                                size.height,
                                size.width,
                                size.height + 12.dp.toPx()
                            )
                        )
                }
            }
            // Elevation draws shadows underneath the shape, which is a problem if shape is transparent
            // We must clip the shadow to outside of the toolbar to not draw under it
            Surface(
                modifier = Modifier.drawShadow(shape = RectangleShape, elevation = elevation)
                    .clip(toolbarShadowShape)
            ) {}
        }
    }
}

// Collapsing toolbar with body content drawn behind it
@Composable
fun CollapsingScaffold(
    scrollState: ScrollState,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 128.dp,
    toolbarContent: @Composable (collapsedFraction: Float, height: Dp) -> Unit,
    bodyContent: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background color for body
            Surface(color = MaterialTheme.colors.background) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    bodyContent()
                }
            }
        }

        Box(
            // Prevent children being clickable from behind toolbar
            modifier = Modifier.clickable(indication = null, onClick = {}),
            children = {
                CollapsingToolbar(
                    scrollState = scrollState,
                    minHeight = minHeight,
                    maxHeight = maxHeight
                ) { collapsedFraction, height ->
                    toolbarContent(collapsedFraction, height)
                }
            })
    }
}
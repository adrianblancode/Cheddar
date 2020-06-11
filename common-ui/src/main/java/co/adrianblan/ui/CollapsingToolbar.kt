package co.adrianblan.ui

import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.*
import androidx.ui.foundation.Box
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.clickable
import androidx.ui.geometry.Rect
import androidx.ui.graphics.Outline
import androidx.ui.graphics.RectangleShape
import androidx.ui.graphics.Shape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.unit.*
import kotlin.math.min

@Composable
fun CollapsingToolbar(
    scroller: ScrollerPosition,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 128.dp,
    toolbarContent: @Composable() (collapsedFraction: Float, height: Dp) -> Unit
) {

    val totalCollapseDistance: Dp = maxHeight - minHeight

    with(DensityAmbient.current) {

        // 1f is fully collapsed
        val collapsedFraction: Float =
            min(scroller.value.px.toDp() / totalCollapseDistance, 1f)

        val height = minHeight + (maxHeight - minHeight) * (1f - collapsedFraction)

        val elevation = lerp(0.dp, 8.dp, collapsedFraction)

        val insets = InsetsAmbient.current
        val topInsets = insets.top.px.toDp()

        // Toolbar background
        Column {
            Surface(color = MaterialTheme.colors.primary.copy(alpha = overInsetAlpha)) {
                Box(
                    gravity = Alignment.BottomCenter,
                    paddingTop = topInsets,
                    modifier = Modifier.fillMaxWidth()
                        .preferredHeight(height + topInsets)
                ) {
                    toolbarContent(collapsedFraction, height)
                }
            }

            // Shape which starts at bottom of toolbar, and extends a few dp
            val toolbarShadowShape: Shape = remember {
                object : Shape {
                    override fun createOutline(size: PxSize, density: Density): Outline =
                        Outline.Rectangle(
                            Rect(
                                0f,
                                size.height.value,
                                size.width.value,
                                size.height.value + 12.dp.toPx().value
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
    scroller: ScrollerPosition,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 128.dp,
    toolbarContent: @Composable() (collapsedFraction: Float, height: Dp) -> Unit,
    bodyContent: @Composable() () -> Unit
) {
    Stack(modifier = Modifier.fillMaxHeight()) {
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
                    scroller = scroller,
                    minHeight = minHeight,
                    maxHeight = maxHeight
                ) { collapsedFraction, height ->
                    toolbarContent(collapsedFraction, height)
                }
            })
    }
}
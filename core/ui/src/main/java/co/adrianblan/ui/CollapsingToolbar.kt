package co.adrianblan.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

@Composable
fun CollapsingToolbar(
    scrollState: ScrollState,
    minHeight: Dp = 56.dp,
    maxHeight: Dp = 128.dp,
    toolbarContent: @Composable (collapsedFraction: Float, height: Dp) -> Unit
) {

    val colors = MaterialTheme.colors
    val density = LocalDensity.current
    val totalCollapseDistance: Dp = maxHeight - minHeight

    val collapsedFraction: Float by remember(scrollState, totalCollapseDistance, density) {
        derivedStateOf {
            // 1f is fully collapsed
            with(density) {
                minOf(scrollState.value.toDp() / totalCollapseDistance, 1f)
            }
        }
    }

    val height = minHeight + (maxHeight - minHeight) * (1f - collapsedFraction)
    val elevation = lerp(0.dp, 8.dp, collapsedFraction)

    val insets = LocalInsets.current
    val topInsets = with(density) { insets.top.toDp() }

    val overInsetPrimaryColor = remember(overInsetAlpha) {
        colors.primary.copy(alpha = overInsetAlpha)
    }

    // Toolbar background
    Column {
        Surface(color = overInsetPrimaryColor) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height + topInsets)
                    .padding(top = topInsets)
            ) {
                toolbarContent(collapsedFraction, height)
            }
        }

        // Shape which starts at bottom of toolbar, and extends a few dp
        val toolbarShadowShape: Shape = remember(density) {
            object : Shape {

                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density
                ): Outline =
                    Outline.Rectangle(
                        Rect(
                            0f,
                            size.height,
                            size.width,
                            size.height + with(density) { 12.dp.toPx() }
                        )
                    )
            }
        }
        // Elevation draws shadows underneath the shape, which is a problem if shape is transparent
        // We must clip the shadow to outside of the toolbar to not draw under it
        Surface(
            modifier = Modifier
                .shadow(shape = RectangleShape, elevation = elevation)
                .clip(toolbarShadowShape)
        ) {}
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
            modifier = Modifier
                .clickable(enabled = false) {}
                .fillMaxWidth(),
            content = {
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
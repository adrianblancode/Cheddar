package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.surface.Surface
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import kotlin.math.min

@Composable
fun CollapsingToolbar(
    scroller: ScrollerPosition,
    children: @Composable() (Float) -> Unit
) {
    val expandedHeight = 128.dp

    // 1f is fully collapsed
    val collapsed: Float =
        min(scroller.value.dp.value / expandedHeight.value, 1f)

    val elevation = lerp(0.dp, 12.dp, collapsed)

    Surface(elevation = elevation, modifier = LayoutWidth.Fill) {
        children(collapsed)
    }
}
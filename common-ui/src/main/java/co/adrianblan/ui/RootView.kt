package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.surface.Surface
import androidx.ui.unit.px
import co.adrianblan.ui.node.Node

data class RootViewState(
    val activeNode: Node<*>
)

@Composable
fun RootView(rootViewState: RootViewState) {
    Crossfade(rootViewState) {
        Stack {

            val insets = InsetsAmbient.current

            // We don't want to deal with the hassle of left-right insets, so just apply them to all screens
            with(DensityAmbient.current) {
                Container(
                    padding = EdgeInsets(
                        left = insets.left.px.toDp(),
                        right = insets.right.px.toDp()
                    )
                ) {
                    it.activeNode.render()
                }
            }

            NavigationBarScrim(modifier = LayoutGravity.BottomCenter)
        }
    }
}

// Compose doesn't seem to play nice with layout flags yet, so let's hack a nav bar scrim
@Composable
fun NavigationBarScrim(
    modifier: Modifier
) {
    val insets = InsetsAmbient.current

    with(DensityAmbient.current) {
        Surface(
            color = MaterialTheme.colors().background.copy(alpha = overInsetAlpha),
            modifier = modifier + LayoutHeight(insets.bottom.px.toDp()) + LayoutWidth.Fill
        ) {}
    }
}
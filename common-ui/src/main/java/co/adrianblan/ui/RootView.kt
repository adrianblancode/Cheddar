package co.adrianblan.ui

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.Modifier.Companion
import androidx.ui.foundation.Box
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
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
                Box(
                    modifier = Modifier.padding(
                        start = insets.left.px.toDp(),
                        end = insets.right.px.toDp()
                    )
                ) {
                    it.activeNode.render()
                }
            }

            NavigationBarScrim(modifier = Modifier.gravity(Alignment.BottomCenter))
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
            color = MaterialTheme.colors.background.copy(alpha = overInsetAlpha),
            modifier = modifier +
                    Modifier.fillMaxWidth() +
                    Modifier.preferredHeight(insets.bottom.px.toDp())
        ) {}
    }
}
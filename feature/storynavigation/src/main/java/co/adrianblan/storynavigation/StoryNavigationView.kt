package co.adrianblan.storynavigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import co.adrianblan.matryoshka.node.AnyNode


data class StoryNavigationViewState(
    val nodes: List<AnyNode>
)

@Composable
fun StoryNavigationView(viewState: StoryNavigationViewState) {
    Crossfade(viewState) {
        Box {
            it.nodes.forEach { node ->
                RenderNode(node)
            }
        }
    }
}

@Composable
private fun RenderNode(node: AnyNode) = node.Render()
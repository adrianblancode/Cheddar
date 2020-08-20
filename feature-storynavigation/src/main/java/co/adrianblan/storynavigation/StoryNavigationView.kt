package co.adrianblan.storynavigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.Stack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import co.adrianblan.matryoshka.node.AnyNode


data class StoryNavigationViewState(
    val nodes: List<AnyNode>
)

@Composable
fun StoryNavigationView(viewState: StoryNavigationViewState) {
    Crossfade(viewState) {
        Stack {
            it.nodes.forEach { node ->
                RenderNode(node)
            }
        }
    }
}

@Composable
private fun RenderNode(node: AnyNode) = node.render()
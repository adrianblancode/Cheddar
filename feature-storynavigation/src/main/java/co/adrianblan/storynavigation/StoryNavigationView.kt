package co.adrianblan.storynavigation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Stack
import androidx.compose.runtime.Composable
import co.adrianblan.matryoshka.node.AnyNode


data class StoryNavigationViewState(
    val activeNode: AnyNode
)

@Composable
fun StoryNavigationView(viewState: StoryNavigationViewState) {
    Crossfade(viewState) {
        Stack {
            it.activeNode.render()
        }
    }
}
package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.ui.StoryDetailView
import co.adrianblan.ui.collectAsState
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.NodeContext
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


class StoryDetailNode
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    @StoryDetailInternal private val listener: Listener,
    @StoryDetailInternal nodeContext: NodeContext
) : Node(nodeContext) {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    @Composable
    override fun render() =
        StoryDetailView(
            viewState = storyDetailInteractor.state.collectAsState().value,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
}
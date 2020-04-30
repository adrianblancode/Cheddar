package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.common.StateFlow
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject


class StoryDetailNode
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    @StoryDetailInternal private val listener: Listener,
    @StoryDetailInternal scope: CoroutineScope
) : Node<StoryDetailViewState>(scope) {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    override val state: StateFlow<StoryDetailViewState> =
        storyDetailInteractor.state

    @Composable
    override fun viewDef(state: StoryDetailViewState) =
        StoryDetailView(
            viewState = state,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
}
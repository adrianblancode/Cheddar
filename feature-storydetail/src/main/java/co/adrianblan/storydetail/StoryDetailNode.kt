package co.adrianblan.storydetail

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.observeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
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

    override val state: LiveData<StoryDetailViewState> =
        storyDetailInteractor.state

    @Composable
    override fun viewDef(state: StoryDetailViewState) =
        StoryDetailView(
            viewState = state,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
}
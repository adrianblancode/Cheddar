package co.adrianblan.storyfeed

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.observeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import javax.inject.Inject

class StoryFeedNode
@Inject constructor(
    private val storyFeedInteractor: StoryFeedInteractor,
    @StoryFeedInternal private val listener: Listener,
    @StoryFeedInternal scope: CoroutineScope
) : Node<StoryFeedViewState>(scope) {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    override val state: LiveData<StoryFeedViewState> =
        storyFeedInteractor.state

    @Composable
    override fun viewDef(state: StoryFeedViewState) =
        StoryFeedView(
            viewState = state,
            onStoryTypeClick = { storyFeedInteractor.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedInteractor.onPageEndReached() }
        )
}
package co.adrianblan.storyfeed

import co.adrianblan.common.StateFlow
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.cancel
import javax.inject.Inject

class StoryFeedNode
@Inject constructor(
    private val storyFeedInteractor: StoryFeedInteractor,
    @StoryFeedInternal private val listener: Listener
) : Node<StoryFeedViewState>() {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    override val viewStateFlow: StateFlow<StoryFeedViewState> =
        storyFeedInteractor.viewStateFlow

    override val composeView: (StoryFeedViewState) -> Unit = { viewState ->
        StoryFeedView(
            viewState = viewState,
            onStoryTypeClick = { storyFeedInteractor.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedInteractor.onPageEndReached() }
        )
    }

    override fun detach() =
        storyFeedInteractor.scope.cancel()
}
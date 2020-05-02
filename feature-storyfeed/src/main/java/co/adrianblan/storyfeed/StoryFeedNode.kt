package co.adrianblan.storyfeed

import androidx.compose.Composable
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storyfeed.ui.StoryFeedView
import co.adrianblan.ui.collectAsState
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class StoryFeedNode
@Inject constructor(
    private val storyFeedInteractor: StoryFeedInteractor,
    @StoryFeedInternal private val listener: Listener,
    @StoryFeedInternal scope: CoroutineScope
) : Node(scope) {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    @Composable
    override fun render() =
        StoryFeedView(
            viewState = storyFeedInteractor.state.collectAsState().value,
            onStoryTypeClick = { storyFeedInteractor.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedInteractor.onPageEndReached() }
        )
}
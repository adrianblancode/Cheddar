package co.adrianblan.storyfeed

import androidx.compose.Composable
import co.adrianblan.ui.Node
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import kotlinx.coroutines.cancel
import javax.inject.Inject

class StoryFeedNode
@Inject constructor(
    private val storyFeedInteractor: StoryFeedInteractor,
    @StoryFeedInternal private val listener: Listener
): Node {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    override val composeView = @Composable {
        StoryFeedScreen(
            viewState = storyFeedInteractor.viewState,
            onStoryTypeClick = { storyFeedInteractor.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedInteractor.onPageEndReached() }
        )
    }

    override fun detach() =
        storyFeedInteractor.scope.cancel()
}
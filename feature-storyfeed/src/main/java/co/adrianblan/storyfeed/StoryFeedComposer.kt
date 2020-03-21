package co.adrianblan.storyfeed

import androidx.compose.Composable
import co.adrianblan.ui.Composer
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.cancel
import javax.inject.Inject

class StoryFeedComposer
@Inject constructor(
    private val storyFeedInteractor: StoryFeedInteractor,
    @StoryFeedInternal private val listener: Listener
): Composer {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
    }

    override val composeView = @Composable {
        StoryFeedScreen(
            viewState = storyFeedInteractor.viewState,
            onStoryTypeClick = { storyType ->
                storyFeedInteractor.onStoryTypeChanged(storyType)
            },
            onStoryClick = {
                listener.onStoryClicked(it)
            },
            onPageEndReached = {
                storyFeedInteractor.onPageEndReached()
            }
        )
    }

    override fun detach() =
        storyFeedInteractor.scope.cancel()
}
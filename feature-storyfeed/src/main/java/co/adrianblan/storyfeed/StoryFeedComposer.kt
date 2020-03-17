package co.adrianblan.storyfeed

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

    override fun composeView() =
        StoryFeedScreen(
            viewState = storyFeedInteractor.viewState,
            onStoryClick = {
                listener.onStoryClicked(it)
            }
        )

    override fun detach() =
        storyFeedInteractor.scope.cancel()
}
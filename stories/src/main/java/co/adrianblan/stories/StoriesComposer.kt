package co.adrianblan.stories

import co.adrianblan.common.ui.Composer
import co.adrianblan.common.ui.StoriesScreen
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.cancel
import javax.inject.Inject

class StoriesComposer
@Inject constructor(
    private val storiesInteractor: StoriesInteractor,
    @StoriesInternal private val listener: Listener
): Composer {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
    }

    override fun composeView() =
        StoriesScreen(
            viewState = storiesInteractor.viewState,
            onStoryClick = { listener.onStoryClicked(it) }
        )

    override fun detach() =
        storiesInteractor.scope.cancel()
}
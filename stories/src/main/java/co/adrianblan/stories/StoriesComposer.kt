package co.adrianblan.stories

import co.adrianblan.common.ui.Composer
import co.adrianblan.common.ui.StoriesScreen
import co.adrianblan.hackernews.api.StoryId
import javax.inject.Inject

class StoriesComposer
@Inject constructor(
    private val storiesInteractor: StoriesInteractor,
    private val listener: Listener
): Composer {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
    }

    override fun composeView() =
        StoriesScreen(
            stateBlock = { storiesInteractor.viewState },
            onStoryClick = { listener.onStoryClicked(it) }
        )
}
package co.adrianblan.storydetail

import co.adrianblan.common.ui.Composer
import co.adrianblan.common.ui.StoryDetailScreen
import javax.inject.Inject


class StoryDetailComposer
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    private val listener: Listener
): Composer {

    interface Listener {
        fun onStoryDetailFinished()
    }

    override fun composeView() =
        StoryDetailScreen(
            stateBlock = { storyDetailInteractor.viewState },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
}
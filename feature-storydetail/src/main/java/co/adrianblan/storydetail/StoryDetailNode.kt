package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.cancel
import javax.inject.Inject


class StoryDetailNode
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    @StoryDetailInternal private val listener: Listener
): Node {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    override val composeView = @Composable {
        StoryDetailScreen(
            viewState = storyDetailInteractor.viewState,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
    }

    override fun detach() =
        storyDetailInteractor.scope.cancel()
}
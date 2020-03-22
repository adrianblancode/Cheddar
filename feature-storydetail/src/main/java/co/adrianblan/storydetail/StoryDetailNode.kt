package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.ui.Node
import kotlinx.coroutines.cancel
import javax.inject.Inject


class StoryDetailNode
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    @StoryDetailInternal private val listener: Listener
): Node {

    interface Listener {
        fun onStoryContentClicked(storyContentUrl: String)
        fun onStoryDetailFinished()
    }

    override val composeView = @Composable {
        StoryDetailScreen(
            viewState = storyDetailInteractor.viewState,
            onStoryContentClicked = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )
    }

    override fun detach() =
        storyDetailInteractor.scope.cancel()
}
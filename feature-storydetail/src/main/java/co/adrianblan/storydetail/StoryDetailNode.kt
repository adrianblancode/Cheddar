package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.common.StateFlow
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.ui.node.Node
import kotlinx.coroutines.cancel
import javax.inject.Inject


class StoryDetailNode
@Inject constructor(
    private val storyDetailInteractor: StoryDetailInteractor,
    @StoryDetailInternal private val listener: Listener
) : Node<StoryDetailViewState>() {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    override val viewStateFlow: StateFlow<StoryDetailViewState> =
        storyDetailInteractor.viewStateFlow

    override val composeView: @Composable() (StoryDetailViewState) -> Unit =
        { viewState ->
            StoryDetailView(
                viewState = viewState,
                onStoryContentClick = { listener.onStoryContentClicked(it) },
                onBackPressed = { listener.onStoryDetailFinished() }
            )
        }

    override fun detach() =
        storyDetailInteractor.scope.cancel()
}
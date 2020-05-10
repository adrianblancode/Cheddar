package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.common.collectAsStateFlow
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.ui.StoryDetailView
import co.adrianblan.ui.collectAsState
import co.adrianblan.matryoshka.Node
import co.adrianblan.matryoshka.NodeContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow


class StoryDetailNode
@AssistedInject constructor(
    @Assisted nodeContext: NodeContext,
    @Assisted private val listener: Listener,
    private val storyDetailPresenter: StoryDetailPresenter
) : Node(nodeContext) {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    private val state: StateFlow<StoryDetailViewState> =
        storyDetailPresenter.state
            .collectAsStateFlow(workScope)

    @Composable
    override fun render() =
        StoryDetailView(
            viewState = state.collectAsState().value,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )

    @AssistedInject.Factory
    interface Factory {
        fun create(
            nodeContext: NodeContext, listener: Listener
        ): StoryDetailNode
    }
}
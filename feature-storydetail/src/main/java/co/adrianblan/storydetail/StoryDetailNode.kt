package co.adrianblan.storydetail

import androidx.compose.Composable
import co.adrianblan.common.MutableStateFlow
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.ui.StoryDetailView
import co.adrianblan.ui.collectAsState
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.NodeContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class StoryDetailNode
@AssistedInject constructor(
    @Assisted nodeContext: NodeContext,
    @Assisted private val listener: Listener,
    private val storyDetailInteractor: StoryDetailInteractor
) : Node(nodeContext) {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onStoryDetailFinished()
    }

    private val state = MutableStateFlow<StoryDetailViewState>(storyDetailInteractor.state.value)

    init {
        scope.launch {
            storyDetailInteractor.state
                .collect {
                    state.offer(it)
                }
        }
    }

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
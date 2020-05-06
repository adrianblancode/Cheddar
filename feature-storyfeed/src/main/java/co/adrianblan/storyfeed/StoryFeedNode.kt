package co.adrianblan.storyfeed

import androidx.compose.Composable
import co.adrianblan.common.MutableStateFlow
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storyfeed.ui.StoryFeedView
import co.adrianblan.ui.collectAsState
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.NodeContext
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StoryFeedNode
@AssistedInject constructor(
    @Assisted private val listener: Listener,
    @Assisted nodeContext: NodeContext,
    private val storyFeedInteractor: StoryFeedInteractor
) : Node(nodeContext) {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    private val state = MutableStateFlow<StoryFeedViewState>(storyFeedInteractor.state.value)

    init {
        scope.launch {
            storyFeedInteractor.state
                .collect {
                    state.offer(it)
                }
        }
    }

    @Composable
    override fun render() =
        StoryFeedView(
            viewState = state.collectAsState().value,
            onStoryTypeClick = { storyFeedInteractor.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedInteractor.onPageEndReached() }
        )

    @AssistedInject.Factory
    interface Factory {
        fun create(
            nodeContext: NodeContext, listener: Listener
        ): StoryFeedNode
    }
}
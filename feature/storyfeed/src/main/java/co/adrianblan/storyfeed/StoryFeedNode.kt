package co.adrianblan.storyfeed

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.adrianblan.common.toStateFlow
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryUrl
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.storyfeed.ui.StoryFeedView
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow

class StoryFeedNode
@AssistedInject constructor(
    @Assisted private val listener: Listener,
    private val storyFeedPresenter: StoryFeedPresenter
) : Node() {

    interface Listener {
        fun onStoryClicked(storyId: StoryId)
        fun onStoryContentClicked(storyUrl: StoryUrl)
    }

    private val state: StateFlow<StoryFeedViewState> =
        storyFeedPresenter.state
            .toStateFlow(scope)

    @Composable
    override fun Render() =
        StoryFeedView(
            viewState = state.collectAsStateWithLifecycle().value,
            onStoryTypeClick = { storyFeedPresenter.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedPresenter.onPageEndReached() }
        )

    @AssistedFactory
    interface Factory {
        fun create(listener: Listener): StoryFeedNode
    }
}
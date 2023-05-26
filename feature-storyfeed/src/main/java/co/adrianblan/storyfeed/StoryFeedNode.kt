package co.adrianblan.storyfeed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.adrianblan.common.collectAsStateFlow
import co.adrianblan.common.toStateFlow
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
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
    override fun render() =
        StoryFeedView(
            viewState = state.collectAsState().value,
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
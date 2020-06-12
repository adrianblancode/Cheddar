package co.adrianblan.storyfeed

import androidx.compose.Composable
import androidx.compose.collectAsState
import co.adrianblan.common.collectAsStateFlow
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.Node
import co.adrianblan.storyfeed.ui.StoryFeedView
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
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
            .collectAsStateFlow(scope)

    @Composable
    override fun render() =
        StoryFeedView(
            viewState = state.collectAsState().value,
            onStoryTypeClick = { storyFeedPresenter.onStoryTypeChanged(it) },
            onStoryClick = { listener.onStoryClicked(it) },
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onPageEndReached = { storyFeedPresenter.onPageEndReached() }
        )

    @AssistedInject.Factory
    interface Factory {
        fun create(listener: Listener): StoryFeedNode
    }
}
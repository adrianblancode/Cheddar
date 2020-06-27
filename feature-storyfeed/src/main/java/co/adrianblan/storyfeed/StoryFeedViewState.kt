package co.adrianblan.storyfeed

import co.adrianblan.core.DecoratedStory
import co.adrianblan.domain.StoryType

data class StoryFeedViewState(
    val storyType: StoryType,
    val storyFeedState: StoryFeedState,
    val isLoadingMorePages: Boolean,
    val hasLoadedAllPages: Boolean
)

sealed class StoryFeedState {
    data class Success(val stories: List<DecoratedStory>) : StoryFeedState()
    object Loading : StoryFeedState()
    data class Error(val throwable: Throwable) : StoryFeedState()
}
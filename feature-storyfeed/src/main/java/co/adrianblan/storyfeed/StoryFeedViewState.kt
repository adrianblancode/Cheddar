package co.adrianblan.storyfeed

import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story

data class StoryFeedViewState(
    val storyType: StoryType,
    val storyFeedState: StoryFeedState,
    val isLoadingMorePages: Boolean,
    val hasLoadedAllPages: Boolean
)

sealed class StoryFeedState {
    data class Success(val stories: List<Story>) : StoryFeedState()
    object Loading : StoryFeedState()
    object Error : StoryFeedState()
}
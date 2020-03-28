package co.adrianblan.storyfeed

import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story
import co.adrianblan.webpreview.WebPreviewData

data class DecoratedStory(
    val story: Story,
    val webPreviewState: WebPreviewState?
)

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

sealed class WebPreviewState {
    data class Success(val webPreview: WebPreviewData) : WebPreviewState()
    object Loading : WebPreviewState()
    data class Error(val throwable: Throwable) : WebPreviewState()
}
package co.adrianblan.storyfeed

import androidx.compose.Model
import co.adrianblan.hackernews.api.Story

@Model
sealed class StoryFeedViewState {
    data class Success(val stories: List<Story>) : StoryFeedViewState()
    object Loading : StoryFeedViewState()
    object Error : StoryFeedViewState()
}
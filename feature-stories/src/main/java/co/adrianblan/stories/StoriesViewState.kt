package co.adrianblan.stories

import androidx.compose.Model
import co.adrianblan.hackernews.api.Story

@Model
sealed class StoriesViewState {
    data class Success(val stories: List<Story>) : StoriesViewState()
    object Loading : StoriesViewState()
    object Error : StoriesViewState()
}
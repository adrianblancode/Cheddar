package co.adrianblan.storyfeed

import androidx.compose.runtime.Immutable
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.StoryType
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class StoryFeedViewState(
    val storyType: StoryType,
    val storyFeedState: StoryFeedState
)

@Immutable
sealed class StoryFeedState {
    data class Success(val stories: ImmutableList<DecoratedStory>, val hasLoadedAllPages: Boolean) : StoryFeedState()
    object Loading : StoryFeedState()
    data class Error(val t: Throwable) : StoryFeedState()
}
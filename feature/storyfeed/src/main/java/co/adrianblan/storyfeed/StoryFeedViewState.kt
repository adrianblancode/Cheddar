package co.adrianblan.storyfeed

import androidx.compose.runtime.Immutable
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.StoryType
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class StoryFeedViewState(
    val storyType: StoryType,
    val storyFeedState: StoryFeedState,
    val hasLoadedAllPages: Boolean
)

@Immutable
sealed class StoryFeedState {
    data class Success(val stories: ImmutableList<DecoratedStory>) : StoryFeedState()
    object Loading : StoryFeedState()
    data class Error(val throwable: Throwable) : StoryFeedState()
}
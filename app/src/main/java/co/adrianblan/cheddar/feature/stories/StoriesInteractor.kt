package co.adrianblan.cheddar.feature.stories

import androidx.compose.Model
import co.adrianblan.common.ui.Interactor
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoriesInteractor
@Inject
constructor(
    // private val hackerNewsRepository: HackerNewsRepository
) : Interactor() {

    val storiesViewState = StoriesViewState(listOf(StoryId(1)))

    init {
        storiesViewState.stories = listOf(StoryId(1), StoryId(2))
        attachScope.launch(Dispatchers.Main) {
            delay(2000L)
            storiesViewState.stories = listOf(StoryId(1), StoryId(2), StoryId(3))
        }
    }
}

@Model
data class StoriesViewState(
    var stories: List<StoryId>
)
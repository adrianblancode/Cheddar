package co.adrianblan.cheddar.feature.stories

import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.ui.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.stories.StoriesViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StoriesInteractor
@Inject
constructor(
    private val hackerNewsRepository: HackerNewsRepository
) : Interactor() {

    val storiesViewState by lazy {
        MutableLiveData<StoriesViewState>(StoriesViewState.Loading)
    }

    init {
        attachScope.launch {
            storiesViewState.value =
                try {
                    val storyIds: List<StoryId> =
                        withContext(Dispatchers.IO) {
                            hackerNewsRepository.fetchTopStories()
                        }

                    val stories: List<Story> =
                        flow {
                            storyIds
                                .take(10)
                                .forEach { storyId ->
                                    val story = hackerNewsRepository.fetchStory(storyId)
                                    emit(story)
                                }
                        }.toList()

                    StoriesViewState.Success(stories)
                } catch (t: Throwable) {
                    Timber.e(t)
                    StoriesViewState.Error
                }
        }
    }
}
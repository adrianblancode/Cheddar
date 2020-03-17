package co.adrianblan.storyfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoryFeedInteractor
@Inject
constructor(
    private val hackerNewsRepository: HackerNewsRepository,
    override val dispatcherProvider: DispatcherProvider,
    @StoryFeedInternal override val parentScope: ParentScope
) : Interactor() {

    val viewState: LiveData<StoryFeedViewState> get() = _viewState

    private val _viewState by lazy {
        MutableLiveData<StoryFeedViewState>(
            StoryFeedViewState.Loading
        )
    }

    init {
        scope.launch {
            flow<StoryFeedViewState> {
                val storyIds: List<StoryId> = hackerNewsRepository.fetchTopStories()

                val stories: List<Story> =
                    flow {
                        storyIds
                            .take(20)
                            .forEach { storyId ->
                                val story = hackerNewsRepository.fetchStory(storyId)
                                emit(story)
                            }
                    }
                        .toList()

                emit(StoryFeedViewState.Success(stories))
            }
                .flowOn(dispatcherProvider.IO)
                .catch {
                    Timber.e(it)
                    emit(StoryFeedViewState.Error)
                }
                .collect {
                    _viewState.value = it
                }
        }
    }
}
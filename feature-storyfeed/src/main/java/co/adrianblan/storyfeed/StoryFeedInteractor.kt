package co.adrianblan.storyfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
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

    val initialStoryType: StoryType = StoryType.TOP

    private val _viewState by lazy {
        MutableLiveData<StoryFeedViewState>(
            StoryFeedViewState(initialStoryType, StoryFeedState.Loading)
        )
    }

    private val storyTypeChannel =
        ConflatedBroadcastChannel<StoryType>(initialStoryType)

    init {
        scope.launch {
            storyTypeChannel.asFlow()
                .flatMapConcat { storyType ->
                    flow {
                        emit(StoryFeedState.Loading)

                        val storyIds: List<StoryId> = hackerNewsRepository.fetchStories(storyType)

                        try {
                            val stories: List<Story> =
                                storyIds.take(12)
                                    .asFlow()
                                    .map { storyId ->
                                        hackerNewsRepository.fetchStory(storyId)
                                    }
                                    .toList()

                            emit(StoryFeedState.Success(stories))
                        } catch (t: Throwable) {
                            Timber.e(t)
                            emit(StoryFeedState.Error)
                        }
                    }
                        .map { storyFeedState ->
                            StoryFeedViewState(storyType, storyFeedState)
                        }
                }
                .flowOn(dispatcherProvider.IO)
                .collect { storyViewState ->
                    _viewState.value = storyViewState
                }
        }
    }

    fun onStoryTypeChanged(storyType: StoryType) {
        storyTypeChannel.offer(storyType)
    }
}
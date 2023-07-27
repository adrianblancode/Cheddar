package co.adrianblan.storyfeed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.WhileSubscribed
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoryFeedViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val hackerNewsRepository: HackerNewsRepository,
    private val storyPreviewUseCase: StoryPreviewUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val storyTypeFlow: StateFlow<StoryType> =
        savedStateHandle.getStateFlow(KEY_STORY_TYPE, StoryType.TOP)

    private val pageIndexFlow = MutableStateFlow<Int>(0)

    val viewState: StateFlow<StoryFeedViewState> =
        storyTypeFlow
            .flatMapLatest { storyType ->

                val cachedStoryIds: List<StoryId>? = hackerNewsRepository.cachedStoryIds(storyType)
                val storyIds = cachedStoryIds ?: hackerNewsRepository.fetchStoryIds(storyType)

                pageIndexFlow.observePages(
                    pageStoryIdsSource = { pageIndex: Int ->
                        storyIds.takePage(pageIndex, PAGE_SIZE)
                    },
                    storyFlowSource = { storyId: StoryId ->
                        storyPreviewUseCase.observeDecoratedStory(storyId)
                    }
                )
                    .map { stories ->
                        if (stories.isEmpty()) StoryFeedState.Loading
                        else StoryFeedState.Success(stories.toImmutableList())
                    }
                    .onStart {
                        // If the flow restarts, don't emit loading state
                        if (cachedStoryIds == null) emit(StoryFeedState.Loading)
                    }
                    .catch { t ->
                        Timber.e(t)
                        emit(StoryFeedState.Error(t))
                    }
                    .map { storyFeedState ->
                        val hasLoadedAllPages =
                            storyFeedState is StoryFeedState.Success
                                    && storyFeedState.stories.size == storyIds.size

                        StoryFeedViewState(
                            storyType = storyType,
                            storyFeedState = storyFeedState,
                            hasLoadedAllPages = hasLoadedAllPages
                        )
                    }
            }
            .flowOn(dispatcherProvider.IO)
            .stateIn(
                viewModelScope,
                WhileSubscribed,
                StoryFeedViewState(
                    storyType = storyTypeFlow.value,
                    storyFeedState = StoryFeedState.Loading,
                    hasLoadedAllPages = false
                )
            )

    internal fun onStoryTypeChanged(storyType: StoryType) {
        savedStateHandle[KEY_STORY_TYPE] = storyType
        pageIndexFlow.tryEmit(0)
    }

    internal fun onPageEndReached() {
        val state = viewState.value.storyFeedState
        if (state is StoryFeedState.Success) {
            val newPageIndex = state.stories.size / PAGE_SIZE
            pageIndexFlow.tryEmit(newPageIndex)
        }
    }

    companion object {
        private const val KEY_STORY_TYPE = "storyType"
        private const val PAGE_SIZE = 20
    }
}
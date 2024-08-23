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
import kotlinx.coroutines.flow.flow
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
    dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val storyTypeFlow: StateFlow<StoryType> =
        savedStateHandle.getStateFlow(KEY_STORY_TYPE, StoryType.TOP)

    private val pageIndexFlow = MutableStateFlow<Int>(0)

    val viewState: StateFlow<StoryFeedViewState> =
        storyTypeFlow
            .flatMapLatest { storyType ->

                flow {
                    emit(hackerNewsRepository.fetchStoryIds(storyType))
                }
                    .flatMapLatest { storyIds: List<StoryId> ->
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
                                else {
                                    StoryFeedState.Success(
                                        stories.toImmutableList(),
                                        hasLoadedAllPages = stories.size == storyIds.size
                                    )
                                }
                            }
                    }
                    .onStart {
                        // Don't emit loading state on resubscribe to existing flow
                        if (pageIndexFlow.value == 0) emit(StoryFeedState.Loading)
                    }
                    .catch { t ->
                        Timber.e(t)
                        emit(StoryFeedState.Error(t))
                    }
                    .map { storyFeedState ->
                        StoryFeedViewState(
                            storyType = storyType,
                            storyFeedState = storyFeedState
                        )
                    }
            }
            .flowOn(dispatcherProvider.IO)
            .stateIn(
                viewModelScope,
                WhileSubscribed,
                StoryFeedViewState(
                    storyType = storyTypeFlow.value,
                    storyFeedState = StoryFeedState.Loading
                )
            )

    internal fun onStoryTypeChanged(storyType: StoryType) {
        pageIndexFlow.tryEmit(0)
        savedStateHandle[KEY_STORY_TYPE] = storyType
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

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
import co.adrianblan.hackernews.api.dummy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.ensureActive
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

    private val initialStoryType: StoryType = StoryType.TOP

    private val _viewState by lazy {
        MutableLiveData<StoryFeedViewState>(
            StoryFeedViewState(
                storyType = initialStoryType,
                storyFeedState = StoryFeedState.Loading,
                isLoadingMorePages = true,
                hasLoadedAllPages = false
            )
        )
    }

    private val storyTypeChannel = ConflatedBroadcastChannel<StoryType>(initialStoryType)

    // Prevents UI from constantly requesting increasing pages, must only request next gated page
    private var currentPageIndexGate = 0

    private val pageIndexChannel = ConflatedBroadcastChannel<Int>(0)

    private val isLoadingMorePagesChannel = ConflatedBroadcastChannel<Boolean>(true)

    private val hasLoadedAllPagesChannel = ConflatedBroadcastChannel<Boolean>(false)

    private fun observePaginatedStories(storyIds: List<StoryId>): Flow<List<Story>> =
        pageIndexChannel.asFlow()
            .conflate()
            .distinctUntilChanged()
            .filter { it <= MAX_PAGE }
            .map { pageIndex ->

                isLoadingMorePagesChannel.offer(true)

                val offset = pageIndex * PAGE_SIZE
                val pageStoryIds = storyIds.drop(offset).take(PAGE_SIZE)

                if (pageStoryIds.isEmpty()) {
                    isLoadingMorePagesChannel.offer(false)
                    hasLoadedAllPagesChannel.offer(true)

                    emptyList()
                } else {
                    pageStoryIds.asFlow()
                        .map { storyId ->
                            hackerNewsRepository.fetchStory(storyId)
                        }
                        .toList()
                        .also {
                            currentPageIndexGate = pageIndex
                            isLoadingMorePagesChannel.offer(false)
                        }
                }
            }
            .filter { it.isNotEmpty() }
            .scanReduce { l1, l2 -> l1 + l2 }

    init {
        scope.launch {
            combine(
                storyTypeChannel.asFlow()
                    .distinctUntilChanged()
                    .conflate()
                    .flatMapLatest { storyType ->
                        channelFlow<StoryFeedState> {
                            offer(StoryFeedState.Loading)

                            val storyIds: List<StoryId> =
                                hackerNewsRepository.fetchStories(storyType)

                            observePaginatedStories(storyIds)
                                .catch { t ->
                                    if (t is CancellationException) throw t
                                    else {
                                        Timber.e(t)
                                        offer(StoryFeedState.Error)
                                    }
                                }
                                .collectLatest { stories ->
                                    ensureActive()
                                    offer(StoryFeedState.Success(stories))
                                }
                        }
                            .map { storyFeedState ->
                                storyType to storyFeedState
                            }
                    },
                isLoadingMorePagesChannel.asFlow()
                    .conflate()
                    .distinctUntilChanged(),
                hasLoadedAllPagesChannel.asFlow()
                    .conflate()
                    .distinctUntilChanged()
            ) { (storyType: StoryType, storyFeedState: StoryFeedState),
                isLoadingMorePages: Boolean,
                hasLoadedAllPages: Boolean ->

                StoryFeedViewState(
                    storyType = storyType,
                    storyFeedState = storyFeedState,
                    isLoadingMorePages = isLoadingMorePages,
                    hasLoadedAllPages = hasLoadedAllPages
                )
            }
                .distinctUntilChanged()
                .flowOn(dispatcherProvider.IO)
                .collectLatest { storyViewState ->
                    ensureActive()
                    _viewState.value = storyViewState
                }
        }
    }

    fun onStoryTypeChanged(storyType: StoryType) {
        storyTypeChannel.offer(storyType)
        isLoadingMorePagesChannel.offer(true)
        hasLoadedAllPagesChannel.offer(false)
        pageIndexChannel.offer(0)
    }

    fun onPageEndReached() {
        pageIndexChannel.offer(currentPageIndexGate + 1)
    }

    companion object {
        private const val PAGE_SIZE = 16
        private const val MAX_PAGE = 500 / PAGE_SIZE
    }
}
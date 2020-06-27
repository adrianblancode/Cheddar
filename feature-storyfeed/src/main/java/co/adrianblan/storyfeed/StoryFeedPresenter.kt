package co.adrianblan.storyfeed

import co.adrianblan.common.*
import co.adrianblan.core.DecoratedStory
import co.adrianblan.core.StoryPreviewUseCase
import co.adrianblan.domain.StoryId
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.domain.StoryType
import co.adrianblan.matryoshka.presenter.Presenter
import co.adrianblan.webpreview.WebPreviewRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class StoryFeedPresenter
@Inject
constructor(
    private val hackerNewsRepository: HackerNewsRepository,
    private val storyPreviewUseCase: StoryPreviewUseCase,
    override val dispatcherProvider: DispatcherProvider
) : Presenter<StoryFeedViewState> {

    private val initialStoryType = StoryType.TOP

    private val storyTypeChannel = ConflatedBroadcastChannel<StoryType>(initialStoryType)

    // Which pages we have already loaded
    // Prevents UI from constantly requesting increasing pages, must only request next gated page
    private var currentPageIndexGate = -1

    private val pageIndexChannel = ConflatedBroadcastChannel<Int>(0)

    private val isLoadingMorePagesChannel = ConflatedBroadcastChannel<Boolean>(true)

    private val hasLoadedAllPagesChannel = ConflatedBroadcastChannel<Boolean>(false)


    override val state: StateFlow<StoryFeedViewState> =
        combine(
            storyTypeChannel.asFlow()
                .distinctUntilChanged()
                .flatMapLatest { storyType ->
                    channelFlow<StoryFeedState> {
                        offer(StoryFeedState.Loading)

                        flow {
                            emit(hackerNewsRepository.fetchStories(storyType))
                        }
                            .flatMapLatest { storyIds ->
                                observePaginatedStories(storyIds)
                            }
                            .catch { t ->
                                Timber.e(t)
                                if (t is CancellationException) throw t
                                else offer(StoryFeedState.Error(t))
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
            .toStateFlow(
                StoryFeedViewState(
                    storyType = initialStoryType,
                    storyFeedState = StoryFeedState.Loading,
                    isLoadingMorePages = true,
                    hasLoadedAllPages = false
                )
            )

    // Takes a page, and observes the list of stories in the page
    private fun observePage(pageIndex: Int, storyIds: List<StoryId>): Flow<List<DecoratedStory>> {

        val offset = pageIndex * PAGE_SIZE
        val pageStoryIds = storyIds.drop(offset).take(PAGE_SIZE)

        return if (pageStoryIds.isEmpty()) flowOf(emptyList())
        else {
            combine(
                pageStoryIds
                    .map { storyId ->
                        storyPreviewUseCase.observeDecoratedStory(storyId)
                    }
            ) { decoratedStories ->
                decoratedStories.toList()
            }
        }
    }

    // Takes a list of story ids, and observes the full list of stories based on pagination
    private fun observePaginatedStories(storyIds: List<StoryId>): Flow<List<DecoratedStory>> =
        pageIndexChannel.asFlow()
            // Block duplicate page emissions
            .distinctUntilChanged()
            // Unordered emissions are ok, as we collect pages after
            .flatMapMerge { pageIndex ->

                isLoadingMorePagesChannel.offer(true)

                observePage(pageIndex, storyIds)
                    .onFirst { stories ->
                        isLoadingMorePagesChannel.offer(false)

                        if (stories.isEmpty()) hasLoadedAllPagesChannel.offer(true)
                        else currentPageIndexGate = pageIndex
                    }
                    .map { pageStories ->
                        pageIndex to pageStories
                    }
            }
            .scanReducePages()

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
        private const val PAGE_SIZE = 20
    }
}
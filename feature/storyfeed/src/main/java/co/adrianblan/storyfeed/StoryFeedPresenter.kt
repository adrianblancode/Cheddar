package co.adrianblan.storyfeed

import co.adrianblan.common.*
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.model.StoryId
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.model.StoryType
import co.adrianblan.matryoshka.presenter.Presenter
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


    override val state: InitialFlow<StoryFeedViewState> =
        combine(
            storyTypeChannel.asFlow()
                .distinctUntilChanged()
                .flatMapLatest { storyType ->
                    channelFlow<StoryFeedState> {
                        trySend(StoryFeedState.Loading)

                        flow {
                            emit(hackerNewsRepository.fetchStories(storyType))
                        }
                            .flatMapLatest { storyIds ->
                                observePaginatedStories(storyIds)
                            }
                            .catch { t ->
                                Timber.e(t)
                                if (t is CancellationException) throw t
                                else trySend(StoryFeedState.Error(t))
                            }
                            .collectLatest { stories ->
                                ensureActive()
                                trySend(StoryFeedState.Success(stories))
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
            .withInitialValue(
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

                isLoadingMorePagesChannel.trySend(true)

                observePage(pageIndex, storyIds)
                    .onFirst { stories ->
                        isLoadingMorePagesChannel.trySend(false)

                        if (stories.isEmpty()) hasLoadedAllPagesChannel.trySend(true)
                        else currentPageIndexGate = pageIndex
                    }
                    .map { pageStories ->
                        pageIndex to pageStories
                    }
            }
            .scanReducePages()

    fun onStoryTypeChanged(storyType: StoryType) {
        storyTypeChannel.trySend(storyType)
        isLoadingMorePagesChannel.trySend(true)
        hasLoadedAllPagesChannel.trySend(false)
        pageIndexChannel.trySend(0)
    }

    fun onPageEndReached() {
        pageIndexChannel.trySend(currentPageIndexGate + 1)
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
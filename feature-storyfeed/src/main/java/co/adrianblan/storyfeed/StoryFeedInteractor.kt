package co.adrianblan.storyfeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.common.onFirst
import co.adrianblan.ui.node.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.webpreview.WebPreviewRepository
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
    private val webPreviewRepository: WebPreviewRepository,
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

    // Observes a decorated story, it will first emit the story and then try to emit decorated data as well
    private fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
        flow {

            val story = hackerNewsRepository.fetchStory(storyId)
            val storyUrl = story.url

            if (storyUrl == null) emit(DecoratedStory(story, null))
            else {
                emit(DecoratedStory(story, WebPreviewState.Loading))

                try {
                    val webPreview = webPreviewRepository.fetchWebPreview(storyUrl.url)
                    emit(DecoratedStory(story, WebPreviewState.Success(webPreview)))
                } catch (t: Throwable) {
                    Timber.e(t)
                    emit(DecoratedStory(story, WebPreviewState.Error))
                }
            }
        }

    // Takes a page, and observes the list of stories in the page
    private fun observePage(pageIndex: Int, storyIds: List<StoryId>): Flow<List<DecoratedStory>> {

        val offset = pageIndex * PAGE_SIZE
        val pageStoryIds = storyIds.drop(offset).take(PAGE_SIZE)

        return if (pageStoryIds.isEmpty()) flowOf(emptyList())
        else {
            combine(
                pageStoryIds
                    .map { storyId ->
                        observeDecoratedStory(storyId)
                    }
            ) { decoratedStories ->
                decoratedStories.toList()
            }
        }
    }

    // Takes a list of story ids, and observes the full list of stories based on pagination
    private fun observePaginatedStories(storyIds: List<StoryId>): Flow<List<DecoratedStory>> =
        pageIndexChannel.asFlow()
            .conflate()
            .distinctUntilChanged()
            .filter { it <= MAX_PAGE }
            .flatMapConcat { pageIndex ->

                isLoadingMorePagesChannel.offer(true)

                observePage(pageIndex, storyIds)
                    .onFirst { stories ->
                        isLoadingMorePagesChannel.offer(false)

                        if (stories.isEmpty()) hasLoadedAllPagesChannel.offer(false)
                        else currentPageIndexGate = pageIndex
                    }
                    .map { pageStories ->
                        pageIndex to pageStories
                    }
            }
            .scanReducePages()

    /** Takes in a flow that returns pages of values, and emits a flow with the sorted latest emission per page */
    private fun <T> Flow<Pair<Int, List<T>>>.scanReducePages(): Flow<List<T>> = flow {

        val map = mutableMapOf<Int, List<T>>()

        collect { (pageIndex: Int, value: List<T>) ->
            map[pageIndex] = value

            val sortedPages: List<T> =
                map.entries
                    .sortedBy { it.key }
                    .map { it.value }
                    .flatten()

            emit(sortedPages)
        }
    }

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
        private const val PAGE_SIZE = 20
        private const val MAX_PAGE = 500 / PAGE_SIZE
    }
}
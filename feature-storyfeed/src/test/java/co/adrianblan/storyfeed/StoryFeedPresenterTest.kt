package co.adrianblan.storyfeed

import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.TestHackerNewsRepository
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.CommentId
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.test.CoroutineTestRule
import co.adrianblan.test.TestStateFlow
import co.adrianblan.test.delayAndThrow
import co.adrianblan.test.test
import co.adrianblan.webpreview.WebPreviewRepository
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class StoryFeedPresenterTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var scope: TestCoroutineScope
    private lateinit var storyFeedPresenter: StoryFeedPresenter

    @Before
    fun setUp() {
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)
        buildPresenter()
    }

    private fun buildPresenter(
        hackerNewsRepository: HackerNewsRepository =
            TestHackerNewsRepository(1000L)
    ) {

        val webPreviewRepository: WebPreviewRepository = mock {
            onBlocking { fetchWebPreview(any()) } doThrow (RuntimeException())
        }

        storyFeedPresenter = StoryFeedPresenter(
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            hackerNewsRepository = hackerNewsRepository,
            webPreviewRepository = webPreviewRepository
        )
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun testInitialState() {
        assertThat(
            storyFeedPresenter.state.value.storyFeedState,
            instanceOf(StoryFeedState.Loading::class.java)
        )
    }

    @Test
    fun testStoriesSuccess() {

        val flow: TestStateFlow<StoryFeedViewState> =
            storyFeedPresenter.state
                .test(scope)
        
        scope.advanceUntilIdle()
        
        assertThat(
            flow.value.storyFeedState,
            instanceOf(StoryFeedState.Success::class.java)
        )

        assert(scope.isActive)
    }

    @Test
    fun testStoriesError() {

        val evilDelay = 10000L

        val evilHackerNewsRepository = object : HackerNewsRepository {
            override suspend fun fetchStory(storyId: StoryId): Story =
                delayAndThrow(evilDelay)

            override suspend fun fetchStories(storyType: StoryType): List<StoryId> =
                delayAndThrow(evilDelay)

            override suspend fun fetchComment(commentId: CommentId): Comment =
                delayAndThrow(evilDelay)
        }

        buildPresenter(evilHackerNewsRepository)

        val flow: TestStateFlow<StoryFeedViewState> =
            storyFeedPresenter.state
                .test(scope)
        
        scope.advanceUntilIdle()

        assertThat(
            flow.value.storyFeedState,
            instanceOf(StoryFeedState.Error::class.java)
        )

        assert(scope.isActive)
    }

    @Test
    fun testPaginationOnce() {

        val flow: TestStateFlow<StoryFeedViewState> =
            storyFeedPresenter.state
                .test(scope)
        
        scope.advanceUntilIdle()

        assertThat(
            flow.value.storyFeedState,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val initialStories: Int =
            (flow.value.storyFeedState as StoryFeedState.Success).stories.size

        assert(initialStories > 0)

        storyFeedPresenter.onPageEndReached()

        assert(flow.value.isLoadingMorePages)

        scope.advanceUntilIdle()

        assertFalse(flow.value.isLoadingMorePages)

        assertThat(
            flow.value.storyFeedState as StoryFeedState.Success,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val nextStories: Int =
            (flow.value.storyFeedState as StoryFeedState.Success).stories.size

        assertEquals(initialStories * 2, nextStories)

        assert(scope.isActive)
    }

    @Test
    fun testPaginationDoubleIgnored() {

        val flow: TestStateFlow<StoryFeedViewState> =
            storyFeedPresenter.state
                .test(scope)
        
        scope.advanceUntilIdle()

        val initialStories: Int =
            (flow.value.storyFeedState as StoryFeedState.Success).stories.size

        assert(initialStories > 0)

        storyFeedPresenter.onPageEndReached()

        scope.advanceTimeBy(100L)

        storyFeedPresenter.onPageEndReached()

        scope.advanceUntilIdle()

        assertThat(
            flow.value.storyFeedState as StoryFeedState.Success,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val nextStories: Int =
            (flow.value.storyFeedState as StoryFeedState.Success).stories.size

        assertEquals(initialStories * 2, nextStories)

        assert(scope.isActive)
    }
}
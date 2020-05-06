package co.adrianblan.storyfeed

import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.TestHackerNewsRepository
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.CommentId
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.test.CoroutineTestRule
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


class StoryFeedInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var scope: TestCoroutineScope
    private lateinit var storyFeedInteractor: StoryFeedInteractor

    suspend fun delayAndThrow(delayTime: Long): Nothing =
        coroutineScope {
            delay(delayTime)
            throw RuntimeException()
        }

    @Before
    fun setUp() {
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)
        buildInteractor()
    }

    private fun buildInteractor(
        hackerNewsRepository: HackerNewsRepository =
            TestHackerNewsRepository(1000L)
    ) {

        val webPreviewRepository: WebPreviewRepository = mock {
            onBlocking { fetchWebPreview(any()) } doThrow (RuntimeException())
        }

        storyFeedInteractor = StoryFeedInteractor(
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
            storyFeedInteractor.state.value.storyFeedState,
            instanceOf(StoryFeedState.Loading::class.java)
        )
    }

    @Test
    fun testStoriesSuccess() {
        scope.advanceUntilIdle()

        assertThat(
            storyFeedInteractor.state.value.storyFeedState,
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

        buildInteractor(evilHackerNewsRepository)

        scope.advanceUntilIdle()

        assertThat(
            storyFeedInteractor.state.value.storyFeedState,
            instanceOf(StoryFeedState.Error::class.java)
        )

        assert(scope.isActive)
    }

    @Test
    fun testPaginationOnce() {
        scope.advanceUntilIdle()

        assertThat(
            storyFeedInteractor.state.value.storyFeedState,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val initialStories: Int =
            (storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success).stories.size

        assert(initialStories > 0)

        storyFeedInteractor.onPageEndReached()

        assert(storyFeedInteractor.state.value.isLoadingMorePages)

        scope.advanceUntilIdle()

        assertFalse(storyFeedInteractor.state.value.isLoadingMorePages)

        assertThat(
            storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val nextStories: Int =
            (storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success).stories.size

        assertEquals(initialStories * 2, nextStories)

        assert(scope.isActive)
    }

    @Test
    fun testPaginationDoubleIgnored() {
        scope.advanceUntilIdle()

        val initialStories: Int =
            (storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success).stories.size

        assert(initialStories > 0)

        storyFeedInteractor.onPageEndReached()

        scope.advanceTimeBy(100L)

        storyFeedInteractor.onPageEndReached()

        scope.advanceUntilIdle()

        assertThat(
            storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success,
            instanceOf(StoryFeedState.Success::class.java)
        )

        val nextStories: Int =
            (storyFeedInteractor.state.value.storyFeedState as StoryFeedState.Success).stories.size

        assertEquals(initialStories * 2, nextStories)

        assert(scope.isActive)
    }
}
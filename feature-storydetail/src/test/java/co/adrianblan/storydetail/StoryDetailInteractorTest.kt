package co.adrianblan.storydetail

import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.TestHackerNewsRepository
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.CommentId
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.test.CoroutineTestRule
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class StoryDetailInteractorTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var scope: TestCoroutineScope
    private lateinit var storyDetailInteractor: StoryDetailInteractor

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
        storyDetailInteractor = StoryDetailInteractor(
            storyId = StoryId(1),
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            scope = scope,
            hackerNewsRepository = hackerNewsRepository,
            webPreviewRepository = mock()
        )
    }

    @After
    fun tearDown() {
        scope.cancel()
    }

    @Test
    fun testInitialState() {
        assertThat(
            storyDetailInteractor.state.value,
            instanceOf(StoryDetailViewState.Loading::class.java)
        )
    }

    @Test
    fun testSuccessStory() {

        scope.advanceUntilIdle()

        assertThat(
            storyDetailInteractor.state.value,
            instanceOf(StoryDetailViewState.Success::class.java)
        )
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
            storyDetailInteractor.state.value,
            instanceOf(StoryDetailViewState.Error::class.java)
        )

        assert(scope.isActive)
    }
}
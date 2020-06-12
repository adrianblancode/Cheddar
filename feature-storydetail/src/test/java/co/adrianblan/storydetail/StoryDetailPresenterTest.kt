package co.adrianblan.storydetail

import co.adrianblan.domain.Comment
import co.adrianblan.domain.CommentId
import co.adrianblan.domain.Story
import co.adrianblan.domain.StoryId
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.domain.StoryType
import co.adrianblan.hackernews.TestHackerNewsRepository
import co.adrianblan.test.CoroutineTestRule
import co.adrianblan.test.TestStateFlow
import co.adrianblan.test.delayAndThrow
import co.adrianblan.test.test
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class StoryDetailPresenterTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var scope: TestCoroutineScope
    private lateinit var storyDetailPresenter: StoryDetailPresenter

    @Before
    fun setUp() {
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)
        buildPresenter()
    }

    private fun buildPresenter(
        hackerNewsRepository: HackerNewsRepository =
            TestHackerNewsRepository(1000L)
    ) {
        storyDetailPresenter = StoryDetailPresenter(
            storyId = StoryId(1),
            dispatcherProvider = coroutineRule.testDispatcherProvider,
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
            storyDetailPresenter.state.value,
            instanceOf(StoryDetailViewState.Loading::class.java)
        )
    }

    @Test
    fun testSuccessStory() {

        val flow: TestStateFlow<StoryDetailViewState> =
            storyDetailPresenter.state
                .test(scope)

        scope.advanceUntilIdle()

        assertThat(
            flow.value,
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

        buildPresenter(evilHackerNewsRepository)

        val flow: TestStateFlow<StoryDetailViewState> =
            storyDetailPresenter.state
                .test(scope)

        scope.advanceUntilIdle()

        assertThat(
            flow.value,
            instanceOf(StoryDetailViewState.Error::class.java)
        )

        assert(scope.isActive)
    }
}
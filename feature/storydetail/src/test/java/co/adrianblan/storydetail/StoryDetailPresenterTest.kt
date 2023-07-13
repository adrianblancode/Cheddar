package co.adrianblan.storydetail

import co.adrianblan.domain.DecoratedStory
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.model.*
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.FakeHackerNewsRepository
import co.adrianblan.testing.CoroutineTestRule
import co.adrianblan.testing.TestStateFlow
import co.adrianblan.testing.delayAndThrow
import co.adrianblan.testing.test
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
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

    object TestStoryPreviewUseCase: StoryPreviewUseCase {
        override fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
           flowOf(DecoratedStory(story = Story.placeholder, webPreviewState = null))
    }

    @Before
    fun setUp() {
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)
        buildPresenter()
    }

    private fun buildPresenter(
        hackerNewsRepository: HackerNewsRepository = FakeHackerNewsRepository(1000L)

    ) {
        storyDetailPresenter = StoryDetailPresenter(
            storyId = StoryId(1),
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            hackerNewsRepository = hackerNewsRepository,
            storyPreviewUseCase = TestStoryPreviewUseCase
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
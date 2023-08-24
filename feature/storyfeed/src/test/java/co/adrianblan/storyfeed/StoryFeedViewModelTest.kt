package co.adrianblan.storyfeed

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import co.adrianblan.common.AsyncResource
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.hackernews.FakeHackerNewsRepository
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.model.Comment
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.model.placeholderLink
import co.adrianblan.testing.CoroutineTestRule
import co.adrianblan.testing.delayAndThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds


class StoryFeedViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var storyFeedViewModel: StoryFeedViewModel

    object TestStoryPreviewUseCase : StoryPreviewUseCase {
        override fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
            flowOf(DecoratedStory(story = Story.placeholderLink, webPreviewState = null))
                .onEach { delay(1000L) }
    }

    @Before
    fun setUp() {
        buildViewModel()
    }

    private fun buildViewModel(
        hackerNewsRepository: HackerNewsRepository =
            FakeHackerNewsRepository(responseDelay = 1.seconds)
    ) {

        storyFeedViewModel = StoryFeedViewModel(
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            hackerNewsRepository = hackerNewsRepository,
            savedStateHandle = SavedStateHandle(),
            storyPreviewUseCase = TestStoryPreviewUseCase
        )
    }

    @Test
    fun testInitialState() {
        assertIs<StoryFeedState.Loading>(storyFeedViewModel.viewState.value.storyFeedState)
    }

    @Test
    fun testStoriesSuccess() = runTest {
        storyFeedViewModel.viewState
            .test {
                assertIs<StoryFeedState.Loading>(awaitItem().storyFeedState)
                assertIs<StoryFeedState.Success>(awaitItem().storyFeedState)
            }
    }

    @Test
    fun testStoriesError() {

        val evilDelay = 1.seconds

        val evilHackerNewsRepository = object : HackerNewsRepository {
            override suspend fun fetchStory(storyId: StoryId): Story =
                delayAndThrow(evilDelay)

            override suspend fun fetchStoryIds(storyType: StoryType): List<StoryId> =
                delayAndThrow(evilDelay)

            override suspend fun fetchComment(commentId: CommentId): Comment =
                delayAndThrow(evilDelay)
        }

        buildViewModel(evilHackerNewsRepository)

        runTest {
            storyFeedViewModel.viewState
                .test(timeout = 30.seconds) {
                    assertIs<StoryFeedState.Loading>(awaitItem().storyFeedState)
                    assertIs<StoryFeedState.Error>(awaitItem().storyFeedState)
                    cancelAndIgnoreRemainingEvents()
                }
        }
    }

    @Test
    fun testPaginationOnce() = runTest {
            storyFeedViewModel.viewState
                .test {
                    assertIs<StoryFeedState.Loading>(awaitItem().storyFeedState)
                    val initial: StoryFeedState = awaitItem().storyFeedState
                    assertIs<StoryFeedState.Success>(initial)
                    assert(initial.stories.size > 0)

                    storyFeedViewModel.onPageEndReached()

                    val next: StoryFeedState = awaitItem().storyFeedState
                    assertIs<StoryFeedState.Success>(next)
                    assertEquals(initial.stories.size * 2, next.stories.size)

                    assert(isActive)
                    cancelAndIgnoreRemainingEvents()
                }
    }

    @Test
    fun testPaginationDoubleIgnored() = runTest {
        storyFeedViewModel.viewState
            .test {
                assertIs<StoryFeedState.Loading>(awaitItem().storyFeedState)
                val initial: StoryFeedState = awaitItem().storyFeedState
                assertIs<StoryFeedState.Success>(initial)
                assert(initial.stories.size > 0)

                storyFeedViewModel.onPageEndReached()
                storyFeedViewModel.onPageEndReached()

                val next: StoryFeedState = awaitItem().storyFeedState
                assertIs<StoryFeedState.Success>(next)
                assertEquals(initial.stories.size * 2, next.stories.size)

                assert(isActive)
                cancelAndIgnoreRemainingEvents()
            }
    }
}
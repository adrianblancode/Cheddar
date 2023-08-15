package co.adrianblan.storydetail

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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds


class StoryDetailViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var storyDetailViewModel: StoryDetailViewModel

    object TestStoryPreviewUseCase : StoryPreviewUseCase {
        override fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
            flowOf(DecoratedStory(story = Story.placeholderLink, webPreviewState = null))
    }

    @Before
    fun setUp() {
        buildViewModel()
    }

    private fun buildViewModel(
        hackerNewsRepository: HackerNewsRepository = FakeHackerNewsRepository(responseDelay = 1.seconds)
    ) {
        storyDetailViewModel = StoryDetailViewModel(
            StoryDetailArgs(StoryId(1)).toSavedStateHandle(),
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            hackerNewsRepository = hackerNewsRepository,
            storyPreviewUseCase = TestStoryPreviewUseCase
        )
    }

    @Test
    fun testInitialState() = runTest {
        assertIs<StoryDetailViewState.Loading>(storyDetailViewModel.viewState.value)
    }

    @Test
    fun testEmptySuccessStory() = runTest {
        buildViewModel(
            FakeHackerNewsRepository(
                story = Story.placeholderLink.copy(kids = persistentListOf()),
                responseDelay = 1.seconds
            )
        )
        storyDetailViewModel.viewState
            .test {
                assertIs<StoryDetailViewState.Loading>(this.awaitItem())
                var item = this.awaitItem()
                assertIs<StoryDetailViewState.Success>(item)
                assertIs<StoryDetailCommentsState.Loading>(item.commentsState)
                item = this.awaitItem()
                assertIs<StoryDetailViewState.Success>(item)
                assertIs<StoryDetailCommentsState.Empty>(item.commentsState)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun testSuccessStory() = runTest {

        val numComments = 10

        buildViewModel(
            FakeHackerNewsRepository(
                story = Story.placeholderLink.copy(kids = List(numComments) { CommentId(it.toLong()) }.toImmutableList()),
                responseDelay = 1.seconds
            )
        )
        storyDetailViewModel.viewState
            .test {
                assertIs<StoryDetailViewState.Loading>(this.awaitItem())

                var item = this.awaitItem()
                assertIs<StoryDetailViewState.Success>(item)
                assertIs<StoryDetailCommentsState.Loading>(item.commentsState)
                item = this.awaitItem()
                assertIs<StoryDetailViewState.Success>(item)
                val commentsState = item.commentsState
                assertIs<StoryDetailCommentsState.Success>(commentsState)
                assertEquals(numComments, commentsState.comments.size)
                cancelAndIgnoreRemainingEvents()
            }
    }

    @Test
    fun testStoriesError() {

        val evilDelay = 1.seconds

        val evilHackerNewsRepository = object : HackerNewsRepository {
            override suspend fun fetchStory(storyId: StoryId): Story =
                delayAndThrow(evilDelay)

            override fun storyIdsResource(storyType: StoryType): AsyncResource<List<StoryId>> =
                AsyncResource(null) {
                    delayAndThrow(evilDelay)
                }

            override suspend fun fetchStoryIds(storyType: StoryType): List<StoryId> =
                delayAndThrow(evilDelay)

            override suspend fun fetchComment(commentId: CommentId): Comment =
                delayAndThrow(evilDelay)
        }

        buildViewModel(evilHackerNewsRepository)

        runTest {
            storyDetailViewModel.viewState
                .test {
                    assertIs<StoryDetailViewState.Loading>(awaitItem())
                    assertIs<StoryDetailViewState.Error>(awaitItem())
                }
            assert(this.isActive)
        }
    }
}
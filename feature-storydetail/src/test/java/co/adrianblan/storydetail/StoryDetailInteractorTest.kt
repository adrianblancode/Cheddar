package co.adrianblan.storydetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.adrianblan.common.ParentScope
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.test.CoroutineTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class StoryDetailInteractorTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var hackerNewsRepository: HackerNewsRepository
    private lateinit var scope: TestCoroutineScope
    private var storyDetailInteractor: StoryDetailInteractor? = null

    @Before
    fun setUp() {
        hackerNewsRepository = mock {
            onBlocking { fetchStory(any()) } doReturn Story.dummy
        }
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)

        storyDetailInteractor = StoryDetailInteractor(
            storyId = StoryId(1),
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            parentScope = ParentScope.of(scope),
            hackerNewsRepository = hackerNewsRepository
        )
    }

    // TODO add more tests

    @Test
    fun testSuccessStory() {

        scope.advanceUntilIdle()

        assertThat(
            storyDetailInteractor!!.state.value,
            instanceOf(StoryDetailViewState.Success::class.java)
        )
    }
}
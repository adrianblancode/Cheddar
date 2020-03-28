package co.adrianblan.storyfeed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.adrianblan.common.ParentScope
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.test.CoroutineTestRule
import co.adrianblan.webpreview.WebPreviewRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.doThrow
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException


class StoryFeedInteractorTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var hackerNewsRepository: HackerNewsRepository
    private lateinit var webPreviewRepository: WebPreviewRepository
    private lateinit var scope: TestCoroutineScope
    private var storyFeedInteractor: StoryFeedInteractor? = null

    @Before
    fun setUp() {
        hackerNewsRepository = mock {
            onBlocking { fetchStory(any()) } doReturn Story.dummy
            onBlocking { fetchStories(any()) } doReturn List(3) { index -> StoryId(index.toLong()) }
        }
        webPreviewRepository = mock {
            onBlocking { fetchWebPreview(any()) } doThrow(RuntimeException())
        }
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)

        storyFeedInteractor = StoryFeedInteractor(
            dispatcherProvider = coroutineRule.testDispatcherProvider,
            scope = scope,
            hackerNewsRepository = hackerNewsRepository,
            webPreviewRepository = webPreviewRepository
        )
    }

    // TODO add more tests

    @Test
    fun testSuccessStory() {
        scope.advanceUntilIdle()

        assertThat(
            storyFeedInteractor!!.state.value?.storyFeedState,
            instanceOf(StoryFeedState.Success::class.java)
        )
    }
}
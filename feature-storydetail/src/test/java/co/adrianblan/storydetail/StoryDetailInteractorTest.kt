package co.adrianblan.storydetail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.adrianblan.common.CoroutineTestRule
import co.adrianblan.common.TestDispatcherProvider
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.StoryId
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StoryDetailInteractorTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private lateinit var storyDetailInteractor: StoryDetailInteractor

    @Before
    fun setUp() {

        val hackerNewsRepository = mock<HackerNewsRepository>()

        storyDetailInteractor = StoryDetailInteractor(
            storyId = StoryId(1),
            dispatcherProvider = TestDispatcherProvider(),
            parentScope = mock(),
            hackerNewsRepository = hackerNewsRepository
        )
    }

    // TODO write tests

}
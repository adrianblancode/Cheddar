package co.adrianblan.cheddar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class RootNodeTest {

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    private val storyFeedNodeBuilder: StoryFeedNodeBuilder =
        mock {
            whenever(
                it.build(any(), any())
            ).thenReturn(mock())
        }

    private val storyDetailNodeBuilder: StoryDetailNodeBuilder =
        mock {
            whenever(
                it.build(any(), any(), any())
            ).thenReturn(mock())
        }

    private lateinit var rootNode: RootNode

    @Before
    fun setUp() {
        rootNode = RootNode(
            storyFeedNodeBuilder,
            storyDetailNodeBuilder,
            customTabsLauncher = mock(),
            parentScope = mock()
        )
    }

    @Test
    fun testInitialState() {
        assert(rootNode.viewState.value is StoryFeedNode)
    }

    // TODO make test for double push
    @Test
    fun testNavigateToStoryDetail() {
        assert(rootNode.viewState.value is StoryFeedNode)
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assert(rootNode.viewState.value is StoryDetailNode)
    }

    @Test
    fun testStoryDetailFinished() {
        assert(rootNode.viewState.value is StoryFeedNode)
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assert(rootNode.viewState.value is StoryDetailNode)
        rootNode.onStoryDetailFinished()
        assert(rootNode.viewState.value is StoryFeedNode)
    }

    @Test
    fun testNavigateBack() {
        assert(rootNode.viewState.value is StoryFeedNode)
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assert(rootNode.viewState.value is StoryDetailNode)
        rootNode.onBackPressed()
        assert(rootNode.viewState.value is StoryFeedNode)
    }
}

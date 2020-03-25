package co.adrianblan.cheddar

import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals

class RootNodeTest {

    private val storyFeedNodeBuilder: StoryFeedNodeBuilder = mock {
        whenever(
            it.build(any(), any())
        ).thenReturn(mock())
    }

    private val storyDetailNodeBuilder: StoryDetailNodeBuilder = mock {
        whenever(
            it.build(any(), any(), any())
        ).thenReturn(mock())
    }

    private var rootNode: RootNode = mock {
        RootNode(
            storyFeedNodeBuilder,
            storyDetailNodeBuilder,
            customTabsLauncher = mock(),
            parentScope = mock()
        )
    }

    // TODO re-enable @Test when compose works better with testing
    // We have a @Model in Router which throws if used outside an active frame
    fun testInitialState() {
        assertEquals(1, rootNode.nodes.size)
        assertEquals(StoryFeedNode::class, rootNode.nodes.lastOrNull()?.let { it::class })
    }
}

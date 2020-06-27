package co.adrianblan.storynavigation

import android.os.Parcelable
import co.adrianblan.domain.StoryId
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore
import co.adrianblan.matryoshka.node.nodeFactory
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.matryoshka.root.createTestNode
import co.adrianblan.storydetail.StoryDetailNodeProvider
import co.adrianblan.storyfeed.StoryFeedNodeProvider
import co.adrianblan.test.CoroutineTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StoryNavigationTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private val storyFeedNodeProvider: StoryFeedNodeProvider =
        mock {
            whenever(it.factory(any()))
                .thenReturn(
                    object : NodeFactory<StoryFeedNode> {
                        override fun create(savedState: Parcelable?, nodeStore: NodeStore) =
                            StoryFeedNode(
                                listener = mock(),
                                storyFeedPresenter = mock()
                            )
                    }
                )
        }

    private val storyDetailNodeProvider: StoryDetailNodeProvider =
        mock {
            whenever(it.factory(any(), any()))
                .thenReturn(
                    object : NodeFactory<StoryDetailNode> {
                        override fun create(savedState: Parcelable?, nodeStore: NodeStore) =
                            StoryDetailNode(
                                listener = mock(),
                                storyDetailPresenter = mock()
                            )
                    }
                )
        }

    private lateinit var scope: TestCoroutineScope
    private lateinit var rootNode: StoryNavigationNode

    @Before
    fun setUp() {
        scope = TestCoroutineScope(SupervisorJob() + coroutineRule.testDispatcher)

        rootNode = createTestNode(scope,
            nodeFactory { savedState, nodeStore ->
                StoryNavigationNode(
                    savedState = savedState,
                    nodeStore = nodeStore,
                    storyFeedNodeProvider = storyFeedNodeProvider,
                    storyDetailNodeProvider = storyDetailNodeProvider,
                    customTabsLauncher = mock()
                )
            }
        )
    }

    @Test
    fun testInitialState() {
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
    }

    // TODO make test for double push
    @Test
    fun testNavigateToStoryDetail() {
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryDetailNode::class.java))
    }

    @Test
    fun testStoryDetailFinished() {
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryDetailNode::class.java))
        rootNode.onStoryDetailFinished()
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
    }

    @Test
    fun testNavigateBack() {
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
        val storyId = StoryId(1)
        rootNode.onStoryClicked(storyId)
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryDetailNode::class.java))
        rootNode.onBackPressed()
        assertThat(rootNode.state.value.activeNode, instanceOf(StoryFeedNode::class.java))
    }
}

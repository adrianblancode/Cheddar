package co.adrianblan.storynavigation

import androidx.compose.Composable
import co.adrianblan.common.*
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import co.adrianblan.ui.collectAsState
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.NodeContext
import co.adrianblan.ui.node.StackRouter
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class StoryNavigationNode
@Inject constructor(
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val customTabsLauncher: CustomTabsLauncher,
    @StoryNavigationInternal nodeContext: NodeContext
) : Node(nodeContext), StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val storyFeedNode: StoryFeedNode =
        createChild { childContext ->
            storyFeedNodeBuilder
                .build(
                    listener = this,
                    nodeContext = childContext
                )
        }

    private val router = StackRouter(listOf(storyFeedNode))

    val state: StateFlow<StoryNavigationViewState> =
        router.state
        .mapStateFlow { StoryNavigationViewState(it.last()) }

    @Composable
    override fun render() =
        StoryNavigationView(state.collectAsState().value)

    override fun onStoryClicked(storyId: StoryId) {

        // Prevent duplicate push
        if (state.value.activeNode is StoryDetailNode) {
            router.pop()
        }

        router.push(
            createChild { childContext ->
                storyDetailNodeBuilder
                    .build(
                        storyId = storyId,
                        listener = this,
                        nodeContext = childContext
                    )
            }
        )
    }

    override fun onStoryContentClicked(storyUrl: StoryUrl) {
        customTabsLauncher.launchUrl(storyUrl.url)
    }

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override fun onBackPressed(): Boolean =
        router.onBackPressed()
}
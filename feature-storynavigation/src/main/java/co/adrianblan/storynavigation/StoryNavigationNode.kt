package co.adrianblan.storynavigation

import androidx.compose.Composable
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.StateFlow
import co.adrianblan.common.mapStateFlow
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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class StoryNavigationNode
@AssistedInject constructor(
    @Assisted nodeContext: NodeContext,
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val customTabsLauncher: CustomTabsLauncher
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

    @AssistedInject.Factory
    interface Factory {
        fun create(nodeContext: NodeContext): StoryNavigationNode
    }
}
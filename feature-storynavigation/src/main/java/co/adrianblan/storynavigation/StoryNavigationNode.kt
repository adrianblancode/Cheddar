package co.adrianblan.storynavigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.map
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.matryoshka.router.StackRouter
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class StoryNavigationState {
    object StoryNavigationFeed : StoryNavigationState()
    data class StoryNavigationDetail(val storyId: StoryId) : StoryNavigationState()
}

class StoryNavigationNode
@Inject constructor(
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val customTabsLauncher: CustomTabsLauncher
) : Node(), StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val router =
        object : StackRouter<StoryNavigationState>(
            nodeStore = nodeStore,
            initialState = StoryNavigationState.StoryNavigationFeed
        ) {
            override fun StoryNavigationState.createNode(): AnyNode =
                when (this) {
                    is StoryNavigationState.StoryNavigationFeed ->
                        storyFeedNodeBuilder
                            .build(listener = this@StoryNavigationNode)
                    is StoryNavigationState.StoryNavigationDetail ->
                        storyDetailNodeBuilder
                            .build(
                                storyId = storyId,
                                listener = this@StoryNavigationNode
                            )
                }
        }

    val state: StateFlow<StoryNavigationViewState> =
        router.state
            .map(scope) { StoryNavigationViewState(it.map { it.node }) }

    override fun onStoryClicked(storyId: StoryId) {
        // Prevent double push
        if (router.activeNode.key !is StoryNavigationState.StoryNavigationDetail) {
            router.push(StoryNavigationState.StoryNavigationDetail(storyId))
        }
    }

    override fun onStoryContentClicked(storyUrl: StoryUrl) {
        customTabsLauncher.launchUrl(storyUrl.url)
    }

    override fun onCommentUrlClicked(url: Uri) {
        customTabsLauncher.launchUrl(url.toString())
    }

    @Composable
    override fun render() =
        StoryNavigationView(state.collectAsState().value)

    override fun onStoryDetailFinished() {
        onBackPressed()
    }

    override fun onBackPressed(): Boolean =
        router.onBackPressed()
}
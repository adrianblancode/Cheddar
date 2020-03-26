package co.adrianblan.cheddar

import androidx.compose.Composable
import co.adrianblan.cheddar.di.RootInternal
import co.adrianblan.cheddar.extensions.CustomTabsLauncher
import co.adrianblan.common.ParentScope
import co.adrianblan.common.StateFlow
import co.adrianblan.common.asStateFlow
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import co.adrianblan.ui.RootView
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.StackRouter
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class RootNode
@Inject constructor(
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val customTabsLauncher: CustomTabsLauncher,
    @RootInternal private val parentScope: ParentScope
) : Node<Node<*>>(), StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val _viewStateFlow = ConflatedBroadcastChannel<Node<*>>()

    override val viewStateFlow: StateFlow<Node<*>> =
        _viewStateFlow.asStateFlow()

    private val router by lazy {
        StackRouter(listOf(storyFeedNode)) {
            _viewStateFlow.offer(nodes.last())
        }
    }

    private val storyFeedNode: StoryFeedNode by lazy {
        storyFeedNodeBuilder
            .build(
                listener = this,
                parentScope = parentScope
            )
    }

    override val composeView: @Composable() (Node<*>) -> Unit = { node ->
        RootView(node)
    }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(
            storyDetailNodeBuilder
                .build(
                    storyId = storyId,
                    listener = this,
                    parentScope = parentScope
                )
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

    override fun detach() {}
}
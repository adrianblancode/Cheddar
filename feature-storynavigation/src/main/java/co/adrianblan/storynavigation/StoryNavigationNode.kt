package co.adrianblan.storynavigation

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.StateFlow
import co.adrianblan.common.asParentScope
import co.adrianblan.common.asStateFlow
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import co.adrianblan.ui.RootView
import co.adrianblan.ui.RootViewState
import co.adrianblan.ui.node.Node
import co.adrianblan.ui.node.StackRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import javax.inject.Inject

class StoryNavigationNode
@Inject constructor(
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val customTabsLauncher: CustomTabsLauncher,
    @StoryNavigationInternal scope: CoroutineScope
) : Node<RootViewState>(scope), StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val _state = ConflatedBroadcastChannel<RootViewState>()
    override val state: StateFlow<RootViewState> = _state.asStateFlow()

    private val storyFeedNode: StoryFeedNode =
        storyFeedNodeBuilder
            .build(
                listener = this,
                parentScope = scope.asParentScope()
            )

    private val router = StackRouter(listOf(storyFeedNode)) { nodes ->
        _state.offer(RootViewState(nodes.last()))
    }

    @Composable
    override fun viewDef(state: RootViewState) = RootView(state)

    override fun onStoryClicked(storyId: StoryId) {

        // Prevent duplicate push
        if (router.nodes.last() is StoryDetailNode) {
            router.pop()
        }

        router.push(
            storyDetailNodeBuilder
                .build(
                    storyId = storyId,
                    listener = this,
                    parentScope = scope.asParentScope()
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
}
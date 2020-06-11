package co.adrianblan.storynavigation

import androidx.compose.Composable
import androidx.compose.collectAsState
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.mapStateFlow
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.matryoshka.*
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
                                storyId = this.storyId,
                                listener = this@StoryNavigationNode
                            )
                }
        }

    val state: StateFlow<StoryNavigationViewState> =
        router.state
            .mapStateFlow { StoryNavigationViewState(it.last().node) }

    override fun onStoryClicked(storyId: StoryId) {
        router.push(StoryNavigationState.StoryNavigationDetail(storyId))
    }

    override fun onStoryContentClicked(storyUrl: StoryUrl) {
        customTabsLauncher.launchUrl(storyUrl.url)
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
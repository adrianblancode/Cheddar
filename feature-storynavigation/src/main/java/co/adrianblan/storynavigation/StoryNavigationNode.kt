package co.adrianblan.storynavigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.net.toUri
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.mapStateFlow
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.matryoshka.router.StackRouter
import co.adrianblan.storycontent.StoryContentNodeBuilder
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeBuilder
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeBuilder
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class StoryNavigationState {
    object StoryNavigationFeed : StoryNavigationState()
    data class StoryNavigationDetail(val storyId: StoryId) : StoryNavigationState()
    data class StoryNavigationContent(val url: Uri) : StoryNavigationState()
}

class StoryNavigationNode
@Inject constructor(
    private val storyFeedNodeBuilder: StoryFeedNodeBuilder,
    private val storyDetailNodeBuilder: StoryDetailNodeBuilder,
    private val storyContentNodeBuilder: StoryContentNodeBuilder,
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
                    is StoryNavigationState.StoryNavigationContent ->
                        storyContentNodeBuilder
                            .build(url = url)
                }
        }

    val state: StateFlow<StoryNavigationViewState> =
        router.state
            .mapStateFlow { StoryNavigationViewState(it.map { it.node }) }

    override fun onStoryClicked(storyId: StoryId) {
        // Prevent double push
        if (router.activeNode.value.key !is StoryNavigationState.StoryNavigationDetail) {
            router.push(StoryNavigationState.StoryNavigationDetail(storyId))
        }
    }

    override fun onStoryContentClicked(storyUrl: StoryUrl) {
        if (router.activeNode.value.key !is StoryNavigationState.StoryNavigationContent) {
            router.push(StoryNavigationState.StoryNavigationContent(storyUrl.url.toUri()))
        }
    }

    override fun onCommentUrlClicked(url: Uri) {
        if (router.activeNode.value.key !is StoryNavigationState.StoryNavigationContent) {
            router.push(StoryNavigationState.StoryNavigationContent(url))
        }
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
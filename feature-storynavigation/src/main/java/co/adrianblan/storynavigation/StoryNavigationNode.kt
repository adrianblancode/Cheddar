package co.adrianblan.storynavigation

import android.net.Uri
import android.os.Parcelable
import androidx.compose.Composable
import androidx.compose.collectAsState
import co.adrianblan.common.CustomTabsLauncher
import co.adrianblan.common.mapStateFlow
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.node.AnyNode
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.matryoshka.node.NodeFactory
import co.adrianblan.matryoshka.node.NodeStore
import co.adrianblan.matryoshka.router.StackRouter
import co.adrianblan.storydetail.StoryDetailNode
import co.adrianblan.storydetail.StoryDetailNodeProvider
import co.adrianblan.storyfeed.StoryFeedNode
import co.adrianblan.storyfeed.StoryFeedNodeProvider
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.StateFlow

sealed class StoryNavigationState : Parcelable {
    @Parcelize
    object StoryNavigationFeed : StoryNavigationState()

    @Parcelize
    data class StoryNavigationDetail(val storyId: StoryId) : StoryNavigationState()
}

class StoryNavigationNode
@AssistedInject constructor(
    @Assisted savedState: Parcelable?,
    @Assisted nodeStore: NodeStore,
    private val storyFeedNodeProvider: StoryFeedNodeProvider,
    private val storyDetailNodeProvider: StoryDetailNodeProvider,
    private val customTabsLauncher: CustomTabsLauncher
) : Node(), StoryFeedNode.Listener, StoryDetailNode.Listener {

    private val router =
        object : StackRouter<StoryNavigationState>(
            nodeStore = nodeStore,
            initialState = (savedState as SavedState?)?.state
                ?: listOf(StoryNavigationState.StoryNavigationFeed)
        ) {
            override fun StoryNavigationState.nodeFactory(): NodeFactory<AnyNode> =
                when (this) {
                    is StoryNavigationState.StoryNavigationFeed ->
                        storyFeedNodeProvider.factory(listener = this@StoryNavigationNode)
                    is StoryNavigationState.StoryNavigationDetail ->
                        storyDetailNodeProvider.factory(
                            storyId = storyId,
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

    override fun saveState() = SavedState(router.state.value.map { it.key })

    @Parcelize
    data class SavedState(
        val state: List<StoryNavigationState>
    ) : Parcelable

    @AssistedInject.Factory
    interface Factory {
        fun create(
            savedState: Parcelable?,
            nodeStore: NodeStore
        ): StoryNavigationNode
    }
}
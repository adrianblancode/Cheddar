package co.adrianblan.storydetail

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.adrianblan.common.toStateFlow
import co.adrianblan.model.StoryUrl
import co.adrianblan.matryoshka.node.Node
import co.adrianblan.storydetail.ui.StoryDetailView
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow


class StoryDetailNode
@AssistedInject constructor(
    @Assisted private val listener: Listener,
    private val storyDetailPresenter: StoryDetailPresenter
) : Node() {

    interface Listener {
        fun onStoryContentClicked(storyUrl: StoryUrl)
        fun onCommentUrlClicked(url: Uri)
        fun onStoryDetailFinished()
    }

    private val state: StateFlow<StoryDetailViewState> =
        storyDetailPresenter.state
            .toStateFlow(scope)

    @Composable
    override fun Render() =
        StoryDetailView(
            viewState = state.collectAsStateWithLifecycle().value,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onCommentUrlClicked = { listener.onCommentUrlClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )

    @AssistedFactory
    interface Factory {
        fun create(
            listener: Listener
        ): StoryDetailNode
    }
}
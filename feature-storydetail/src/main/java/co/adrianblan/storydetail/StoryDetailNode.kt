package co.adrianblan.storydetail

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import co.adrianblan.common.collectAsStateFlow
import co.adrianblan.domain.StoryUrl
import co.adrianblan.storydetail.ui.StoryDetailView
import co.adrianblan.matryoshka.node.Node
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
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
            .collectAsStateFlow(scope)

    @Composable
    override fun render() =
        StoryDetailView(
            viewState = state.collectAsState().value,
            onStoryContentClick = { listener.onStoryContentClicked(it) },
            onCommentUrlClicked = { listener.onCommentUrlClicked(it) },
            onBackPressed = { listener.onStoryDetailFinished() }
        )

    @AssistedInject.Factory
    interface Factory {
        fun create(
            listener: Listener
        ): StoryDetailNode
    }
}
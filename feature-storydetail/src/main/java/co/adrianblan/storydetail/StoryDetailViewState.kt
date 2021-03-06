package co.adrianblan.storydetail

import androidx.compose.runtime.Immutable
import co.adrianblan.core.WebPreviewState
import co.adrianblan.domain.Comment
import co.adrianblan.domain.Story
import co.adrianblan.webpreview.WebPreviewData

@Immutable
sealed class StoryDetailViewState {
    data class Success(
        val story: Story,
        val webPreviewState: WebPreviewState?,
        val commentsState: StoryDetailCommentsState
    ) : StoryDetailViewState()

    object Loading : StoryDetailViewState()
    data class Error(val throwable: Throwable) : StoryDetailViewState()
}

// A comment in the comment tree that has been flattened into a list
@Immutable
data class FlatComment(
    val comment: Comment,
    val depthIndex: Int
)

@Immutable
sealed class StoryDetailCommentsState {
    data class Success(val comments: List<FlatComment>) : StoryDetailCommentsState()
    object Empty : StoryDetailCommentsState()
    object Loading : StoryDetailCommentsState()
    object Error : StoryDetailCommentsState()
}
package co.adrianblan.storydetail

import androidx.compose.runtime.Immutable
import co.adrianblan.model.Comment
import co.adrianblan.model.Story
import co.adrianblan.model.WebPreviewState
import kotlinx.collections.immutable.ImmutableList

@Immutable
sealed class StoryDetailViewState {
    data class Success(
        val story: Story,
        val webPreviewState: WebPreviewState?,
        val commentsState: StoryDetailCommentsState
    ) : StoryDetailViewState()

    data object Loading : StoryDetailViewState()
    data class Error(val t: Throwable) : StoryDetailViewState()
}

enum class CommentCollapsedState {
    COLLAPSED, PARENT_COLLAPSED;
}

// A comment in the comment tree that has been flattened into a list
@Immutable
data class FlatComment(
    val comment: Comment,
    val numChildren: Int,
    val depthIndex: Int,
    val collapsedState: CommentCollapsedState?
)

@Immutable
sealed class StoryDetailCommentsState {
    data class Success(val comments: ImmutableList<FlatComment>) : StoryDetailCommentsState()
    data object Empty : StoryDetailCommentsState()
    data object Loading : StoryDetailCommentsState()
    data object Error : StoryDetailCommentsState()
}
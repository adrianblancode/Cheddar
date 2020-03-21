package co.adrianblan.storydetail

import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story

sealed class StoryDetailViewState {
    data class Success(
        val story: Story,
        val items: List<StoryDetailItem>
    ) : StoryDetailViewState()

    object Loading : StoryDetailViewState()
    object Error : StoryDetailViewState()
}

sealed class StoryDetailItem {
    data class CommentItem(val comment: Comment) : StoryDetailItem()
    object CommentsEmptyItem : StoryDetailItem()
    object CommentsLoadingItem : StoryDetailItem()
    object CommentsErrorItem : StoryDetailItem()
}
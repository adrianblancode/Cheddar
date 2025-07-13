package co.adrianblan.storydetail.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryUrl
import co.adrianblan.storydetail.CommentCollapsedState
import co.adrianblan.storydetail.R
import co.adrianblan.storydetail.StoryDetailCommentsState
import co.adrianblan.storydetail.StoryDetailViewModel
import co.adrianblan.storydetail.StoryDetailViewState
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.ErrorView
import co.adrianblan.ui.LoadingSpinner
import co.adrianblan.ui.ToolbarNestedScrollConnection

private val toolbarMinHeight = 56.dp
private val toolbarMaxHeight = 156.dp

@Composable
internal fun StoryDetailRoute(
    onStoryContentClick: (StoryUrl) -> Unit,
    onCommentUrlClick: (Uri) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: StoryDetailViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    StoryDetailScreen(
        viewState = viewState,
        onStoryContentClick = onStoryContentClick,
        onCommentClick = { commentId -> viewModel.onCommentClick(commentId) },
        onCommentUrlClick = onCommentUrlClick,
        onBackPressed = onBackPressed
    )
}

@Composable
internal fun StoryDetailScreen(
    viewState: StoryDetailViewState,
    onStoryContentClick: (StoryUrl) -> Unit,
    onCommentClick: (CommentId) -> Unit,
    onCommentUrlClick: (Uri) -> Unit,
    onBackPressed: () -> Unit
) {

    val nestedScrollHeightPx = with(LocalDensity.current) {
        (toolbarMaxHeight - toolbarMinHeight).toPx()
    }
    val nestedScrollConnection = remember {
        ToolbarNestedScrollConnection(nestedScrollHeightPx)
    }

    val toolbar: @Composable () -> Unit = {
        StoryDetailToolbar(
            viewState = viewState,
            collapseProgress = { nestedScrollConnection.progress() },
            onStoryContentClick = onStoryContentClick,
            onBackPressed = onBackPressed
        )
    }

    val commentsLoaded = (viewState as? StoryDetailViewState.Success)
        ?.commentsState is StoryDetailCommentsState.Success

    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {

        when (viewState) {
            is StoryDetailViewState.Success -> {
                when (viewState.commentsState) {
                    is StoryDetailCommentsState.Success -> {
                        CommentsSuccessBody(
                            story = viewState.story,
                            commentsState = viewState.commentsState,
                            header = {
                                stickyHeader {
                                    toolbar()
                                }
                            },
                            onCommentClick = onCommentClick,
                            onCommentUrlClick = onCommentUrlClick,
                            modifier = Modifier.nestedScroll(nestedScrollConnection)
                        )
                    }

                    is StoryDetailCommentsState.Empty -> CommentsEmptyView()
                    is StoryDetailCommentsState.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())
                    is StoryDetailCommentsState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
                }
            }

            is StoryDetailViewState.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())

            is StoryDetailViewState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
        }

        if (!commentsLoaded) {
            toolbar()
        }
    }

}

@Composable
private fun CommentsSuccessBody(
    story: Story,
    commentsState: StoryDetailCommentsState.Success,
    header: LazyListScope.() -> Unit,
    onCommentClick: (CommentId) -> Unit,
    onCommentUrlClick: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier.fillMaxSize()) {

        header()

        if (story.text != null) {
            item {
                CommentItem(
                    text = story.text,
                    by = story.by,
                    storyAuthor = story.by,
                    onCommentUrlClick = onCommentUrlClick
                )
            }
        }

        items(
            items = commentsState.comments,
            key = { it.comment.id.id },
            contentType = { "comment" },
            itemContent = { comment ->
                AnimatedContent(
                    targetState = comment.collapsedState,
                    label = "comment_collapsed_state",
                    modifier = Modifier
                        .clickable { onCommentClick(comment.comment.id) }
                        .commentDepthIndicator(comment.depthIndex)
                ) { collapsedState ->
                    when (collapsedState) {
                        CommentCollapsedState.COLLAPSED ->
                            CollapsedCommentItem(numChildren = comment.numChildren)

                        CommentCollapsedState.PARENT_COLLAPSED -> {}
                        else -> {
                            CommentItem(
                                comment = comment,
                                storyAuthor = story.by,
                                onCommentUrlClick = onCommentUrlClick
                            )
                        }
                    }
                }
            }
        )

        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun CommentsEmptyView() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.comments_empty),
                style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center)
            )
        }
    }
}
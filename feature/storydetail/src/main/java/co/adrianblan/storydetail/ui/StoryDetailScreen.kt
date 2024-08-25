package co.adrianblan.storydetail.ui

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.adrianblan.model.CommentId
import co.adrianblan.model.StoryUrl
import co.adrianblan.storydetail.CommentCollapsedState
import co.adrianblan.storydetail.R
import co.adrianblan.storydetail.StoryDetailCommentsState
import co.adrianblan.storydetail.StoryDetailViewModel
import co.adrianblan.storydetail.StoryDetailViewState
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.CollapsingScaffold
import co.adrianblan.ui.ErrorView
import co.adrianblan.ui.LoadingVisual

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

    val scrollState = rememberScrollState()

    CollapsingScaffold(
        scrollState = scrollState,
        minHeight = toolbarMinHeight,
        maxHeight = toolbarMaxHeight,
        toolbarContent = { collapsedFraction ->
            StoryDetailToolbar(
                viewState = viewState,
                collapsedFraction = collapsedFraction,
                onStoryContentClick = onStoryContentClick,
                onBackPressed = onBackPressed
            )
        },
        bodyContent = {

            when (viewState) {
                is StoryDetailViewState.Success -> CommentsSuccessBody(
                    viewState = viewState,
                    scrollState = scrollState,
                    onCommentClick = onCommentClick,
                    onCommentUrlClick = onCommentUrlClick
                )

                is StoryDetailViewState.Loading -> LoadingVisual(modifier = Modifier.fillMaxSize())
                is StoryDetailViewState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
            }
        }
    )
}

@Composable
private fun CommentsSuccessBody(
    viewState: StoryDetailViewState.Success,
    scrollState: ScrollState,
    onCommentClick: (CommentId) -> Unit,
    onCommentUrlClick: (Uri) -> Unit
) {
    val story = viewState.story

    when (viewState.commentsState) {
        is StoryDetailCommentsState.Success ->
            // TODO change to LazyColumn once scroll state is fixed
            Column(modifier = Modifier.verticalScroll(scrollState)) {

                Spacer(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(toolbarMaxHeight)
                )

                if (viewState.story.text != null) {
                    CommentItem(
                        text = story.text,
                        by = story.by,
                        storyAuthor = story.by,
                        onCommentUrlClick = onCommentUrlClick
                    )
                }

                viewState.commentsState.comments
                    .forEach { comment ->
                        key(comment.comment.id) {

                            AnimatedContent(
                                targetState = comment.collapsedState,
                                label = "comment_collapsed_state",
                                modifier = Modifier
                                    .clickable { onCommentClick(comment.comment.id)  }
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
                    }

                Spacer(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .height(8.dp)
                )
            }

        is StoryDetailCommentsState.Empty -> CommentsEmptyView()
        is StoryDetailCommentsState.Loading -> LoadingVisual(modifier = Modifier.fillMaxSize())
        is StoryDetailCommentsState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
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
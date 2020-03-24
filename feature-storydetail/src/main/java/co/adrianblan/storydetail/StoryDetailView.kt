package co.adrianblan.storydetail

import android.text.Html
import androidx.compose.Composable
import androidx.compose.remember
import androidx.lifecycle.LiveData
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.material.surface.Surface
import androidx.ui.res.colorResource
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.*
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.ui.*
import co.adrianblan.ui.InsetsAmbient

private const val toolbarMaxHeightDp = 128

@Composable
fun StoryDetailScreen(
    viewState: LiveData<StoryDetailViewState>,
    onStoryContentClick: (StoryUrl) -> Unit,
    onBackPressed: () -> Unit
) {
    StoryDetailView(
        viewState = observe(viewState),
        onStoryContentClick = onStoryContentClick,
        onBackPressed = onBackPressed
    )
}

@Composable
fun StoryDetailView(
    viewState: StoryDetailViewState,
    onStoryContentClick: (StoryUrl) -> Unit,
    onBackPressed: () -> Unit
) {

    val scroller = ScrollerPosition()
    val insets = InsetsAmbient.current

    Scaffold(
        bodyContent = {
            CollapsingScaffold(
                scroller = scroller,
                maxHeight = toolbarMaxHeightDp.dp,
                toolbarContent = { collapsedFraction, height ->
                    StoryDetailToolbar(
                        storyTitle = (viewState as? StoryDetailViewState.Success)?.story?.title.orEmpty(),
                        collapsedFraction = collapsedFraction,
                        height = height,
                        onBackPressed = onBackPressed
                    )
                },
                bodyContent = {
                    when (viewState) {
                        is StoryDetailViewState.Success ->

                            when (viewState.commentState) {
                                is StoryDetailCommentsState.Success ->
                                    VerticalScroller(scroller) {
                                        Column {
                                            with(DensityAmbient.current) {
                                                val topInsets = insets.top.px.toDp()
                                                Spacer(modifier = LayoutHeight(toolbarMaxHeightDp.dp + topInsets))

                                                viewState.commentState.comments
                                                    .map { comment ->
                                                        CommentItem(comment)
                                                    }

                                                Spacer(modifier = LayoutHeight(insets.bottom.px.toDp() + 8.dp + 56.dp))
                                            }
                                        }
                                    }
                                is StoryDetailCommentsState.Empty ->
                                    CommentsEmptyView()
                                is StoryDetailCommentsState.Loading ->
                                    LoadingView()
                                is StoryDetailCommentsState.Error ->
                                    ErrorView()
                            }

                        is StoryDetailViewState.Loading -> LoadingView()
                        is StoryDetailViewState.Error -> ErrorView()
                    }
                }
            )
        },
        floatingActionButton = {
            val url = (viewState as? StoryDetailViewState.Success)?.story?.url
            if (url != null) {
                with(DensityAmbient.current) {
                    Container(padding = EdgeInsets(bottom = insets.bottom.px.toDp())) {
                        FloatingActionButton(
                            color = MaterialTheme.colors().secondary,
                            onClick = { onStoryContentClick(url) }
                        ) {
                            VectorImage(
                                vector = Icons.Default.ArrowForward,
                                tint = MaterialTheme.colors().surface
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun StoryDetailToolbar(
    storyTitle: String,
    collapsedFraction: Float,
    height: Dp,
    onBackPressed: () -> Unit
) {
    Stack {
        Container(padding = EdgeInsets(4.dp)) {
            VectorImageButton(
                vector = Icons.Default.ArrowBack,
                tint = MaterialTheme.colors().onBackground,
                modifier = LayoutGravity.TopLeft + LayoutSize(48.dp, 48.dp)
            ) {
                onBackPressed()
            }
        }

        val titleCollapsedLeftOffset =
            remember(collapsedFraction) { lerp(0.dp, 48.dp, collapsedFraction) }
        val titleCollapsedTopOffset =
            remember(collapsedFraction) { lerp(48.dp, 0.dp, collapsedFraction) }
        val titleMaxLines = remember(collapsedFraction) { if (collapsedFraction > 0.1f) 1 else 3 }

        Container(
            alignment = Alignment.BottomLeft,
            modifier = LayoutHeight(height)
        ) {
            Text(
                text = storyTitle,
                style = MaterialTheme.typography().h6,
                modifier = LayoutGravity.CenterLeft +
                        LayoutPadding(
                            left = 16.dp + titleCollapsedLeftOffset,
                            right = 16.dp,
                            top = titleCollapsedTopOffset
                        ) +
                        LayoutWidth.Fill +
                        LayoutHeight(height) +
                        LayoutAlign.CenterLeft,
                overflow = TextOverflow.Ellipsis,
                maxLines = titleMaxLines
            )
        }
    }
}

@Composable
fun CommentItem(commentItem: FlatComment) {

    val comment = commentItem.comment
    val depthIndex = commentItem.depthIndex

    Container(
        padding = EdgeInsets(
            left = 16.dp,
            right = 16.dp,
            top = 8.dp,
            bottom = 6.dp
        )
    ) {
        Row(modifier = LayoutHeight.Fill) {

            // TODO fill all height
            repeat(depthIndex) {
                Surface(
                    color = Color.Transparent,
                    modifier = LayoutHeight.Fill +
                            LayoutPadding(right = 12.dp) +
                            LayoutWidth(1.dp)
                ) {}
            }

            Column(
                arrangement = Arrangement.Begin,
                modifier = LayoutWidth.Fill + LayoutFlexible(1f)
            ) {

                // Comments can be deleted
                val isDeleted = comment.by == null

                if (isDeleted) {
                    Spacer(LayoutHeight(12.dp))
                    Text(
                        text = stringResource(R.string.comment_deleted_title),
                        style = MaterialTheme.typography().subtitle2
                    )
                    Spacer(LayoutHeight(16.dp))
                } else {
                    Text(
                        text = comment.by.orEmpty(),
                        style = MaterialTheme.typography().subtitle2
                    )
                    Spacer(LayoutHeight(2.dp))
                    Text(
                        text = Html.fromHtml(comment.text.orEmpty()).toString().trimEnd(),
                        style = MaterialTheme.typography().body2
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StoryDetailPreview() {
    AppTheme {
        val viewState =
            StoryDetailViewState.Success(
                story = Story.dummy,
                commentState = StoryDetailCommentsState.Success(
                    listOf(
                        FlatComment(Comment.dummy, 0),
                        FlatComment(Comment.dummy, 1),
                        FlatComment(Comment.dummy, 2),
                        FlatComment(Comment.dummy, 0)
                    )
                )
            )
        StoryDetailView(
            viewState = viewState,
            onStoryContentClick = {},
            onBackPressed = {}
        )
    }
}

@Preview
@Composable
fun CommentsEmptyView() {
    Container(
        expanded = true,
        padding = EdgeInsets(32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.comments_empty),
            style = MaterialTheme.typography().h6.copy(textAlign = TextAlign.Center),
            modifier = LayoutAlign.Center
        )
    }
}
package co.adrianblan.storydetail

import android.text.Html
import androidx.compose.Composable
import androidx.compose.key
import androidx.compose.remember
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Text
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.material.ripple.Ripple
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

private const val toolbarMinHeightDp = 56
private const val toolbarMaxHeightDp = 148


@Composable
fun StoryDetailView(
    viewState: StoryDetailViewState,
    onStoryContentClick: (StoryUrl) -> Unit,
    onBackPressed: () -> Unit
) {

    val scroller = ScrollerPosition()
    val insets = InsetsAmbient.current


    CollapsingScaffold(
        scroller = scroller,
        minHeight = toolbarMinHeightDp.dp,
        maxHeight = toolbarMaxHeightDp.dp,
        toolbarContent = { collapsedFraction, height ->
            StoryDetailToolbar(
                viewState = viewState,
                collapsedFraction = collapsedFraction,
                height = height,
                onStoryContentClick = onStoryContentClick,
                onBackPressed = onBackPressed
            )
        },
        bodyContent = {
            when (viewState) {
                is StoryDetailViewState.Success -> {

                    val story = viewState.story

                    when (viewState.commentsState) {
                        is StoryDetailCommentsState.Success ->
                            VerticalScroller(scroller) {
                                Column {
                                    with(DensityAmbient.current) {
                                        val topInsets = insets.top.px.toDp()
                                        Spacer(modifier = LayoutHeight(toolbarMaxHeightDp.dp + topInsets))

                                        if (viewState.story.text != null) {
                                            CommentItem(
                                                text = story.text,
                                                by = story.by,
                                                depthIndex = 0,
                                                storyAuthor = story.by
                                            )
                                        }

                                        viewState.commentsState.comments
                                            .map { comment ->
                                                key(comment.comment.id) {
                                                    CommentItem(
                                                        comment = comment,
                                                        storyAuthor = story.by
                                                    )
                                                }
                                            }

                                        Spacer(modifier = LayoutHeight(insets.bottom.px.toDp() + 8.dp))
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
                }

                is StoryDetailViewState.Loading -> LoadingView()
                is StoryDetailViewState.Error -> ErrorView()
            }
        }
    )
}

@Composable
fun StoryDetailToolbar(
    viewState: StoryDetailViewState,
    collapsedFraction: Float,
    height: Dp,
    onStoryContentClick: (StoryUrl) -> Unit,
    onBackPressed: () -> Unit
) {

    Stack {
        Container(padding = EdgeInsets(4.dp)) {
            Ripple(bounded = false) {
                Clickable(onClick = onBackPressed) {
                    Container(modifier = LayoutGravity.TopLeft + LayoutSize(48.dp, 48.dp)) {
                        VectorImage(
                            vector = Icons.Default.ArrowBack,
                            tint = MaterialTheme.colors().onBackground
                        )
                    }
                }
            }
        }

        val titleCollapsedLeftOffset =
            remember(collapsedFraction) { lerp(0.dp, 48.dp, collapsedFraction) }
        val titleCollapsedTopOffset =
            remember(collapsedFraction) { lerp(48.dp, 0.dp, collapsedFraction) }

        val titleFontSize: TextUnit =
            lerp(
                MaterialTheme.typography().h6.fontSize,
                MaterialTheme.typography().subtitle1.fontSize,
                collapsedFraction
            )

        val titleMaxLines = remember(collapsedFraction) { if (collapsedFraction >= 0.85f) 1 else 3 }
        val imageSize = remember(collapsedFraction) { lerp(80.dp, 40.dp, collapsedFraction) }

        when (viewState) {
            is StoryDetailViewState.Loading ->
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    modifier = LayoutPadding(16.dp) + LayoutPadding(top = titleCollapsedTopOffset)
                ) {
                    Column {
                        Container(expanded = true, modifier = LayoutHeight(20.dp)) {
                            ShimmerView()
                        }
                        Spacer(modifier = LayoutHeight(6.dp))
                        Container(expanded = true, modifier = LayoutHeight(20.dp)) {
                            ShimmerView()
                        }
                    }
                }
            is StoryDetailViewState.Success -> {

                val story: Story = viewState.story
                val webPreviewState: WebPreviewState? = viewState.webPreviewState

                val titleRightOffset =
                    if (story.url != null) imageSize + 12.dp
                    else 0.dp

                Text(
                    text = story.title,
                    style = MaterialTheme.typography().h6.copy(
                        fontSize = titleFontSize,
                        color = MaterialTheme.colors().onBackground
                    ),
                    modifier = LayoutPadding(
                        left = 16.dp + titleCollapsedLeftOffset,
                        right = 16.dp + titleRightOffset,
                        bottom = 8.dp,
                        top = 8.dp + titleCollapsedTopOffset
                    ) +
                            LayoutHeight(height) +
                            LayoutWidth.Fill +
                            LayoutAlign.CenterLeft +
                            LayoutGravity.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = titleMaxLines
                )

                if (story.url != null) {
                    Box(modifier = LayoutGravity.BottomRight) {
                        StoryDetailImage(
                            story = story,
                            webPreviewState = webPreviewState,
                            imageSize = imageSize,
                            onStoryContentClick = onStoryContentClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryDetailImage(
    story: Story,
    webPreviewState: WebPreviewState?,
    imageSize: Dp,
    onStoryContentClick: (StoryUrl) -> Unit
) {

    val clickListener: () -> Unit =
        remember(story) {
            { story.url?.let { onStoryContentClick(it) } }
        }

    Ripple(bounded = false) {
        Clickable(onClick = clickListener) {
            Stack(
                modifier = LayoutPadding(
                    top = 8.dp,
                    right = 16.dp,
                    bottom = 8.dp
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    modifier = LayoutWidth(imageSize) + LayoutHeight(imageSize)
                ) {
                    Stack {
                        Surface(
                            color = colorResource(R.color.contentMuted),
                            modifier = LayoutWidth.Fill + LayoutHeight.Fill
                        ) {}

                        when (webPreviewState) {
                            is WebPreviewState.Loading -> {
                                Container(expanded = true) {
                                    ShimmerView()
                                }
                            }
                            is WebPreviewState.Success -> {
                                val webPreview = webPreviewState.webPreview

                                val imageUrl =
                                    webPreview.imageUrl ?: webPreview.iconUrl
                                    ?: webPreview.favIconUrl

                                UrlImage(imageUrl)
                            }
                            is WebPreviewState.Error -> {
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: FlatComment, storyAuthor: String?) {
    CommentItem(
        text = comment.comment.text,
        by = comment.comment.by,
        depthIndex = comment.depthIndex,
        storyAuthor = storyAuthor
    )
}

@Composable
fun CommentItem(
    text: String?,
    by: String?,
    depthIndex: Int,
    storyAuthor: String?
) {

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
                val isDeleted = by == null

                if (isDeleted) {
                    Spacer(LayoutHeight(12.dp))
                    Text(
                        text = stringResource(R.string.comment_deleted_title),
                        style = MaterialTheme.typography().subtitle2
                    )
                    Spacer(LayoutHeight(16.dp))
                } else {

                    val isStoryAuthor = by == storyAuthor

                    val authorColor: Color =
                        if (isStoryAuthor) MaterialTheme.colors().secondary
                        else MaterialTheme.colors().onBackground

                    val authorSuffix =
                        if (isStoryAuthor) " [op]"
                        else ""

                    Text(
                        text = by.orEmpty() + authorSuffix,
                        style = MaterialTheme.typography().subtitle2.copy(color = authorColor)
                    )
                    Spacer(LayoutHeight(2.dp))
                    Text(
                        text = Html.fromHtml(text.orEmpty()).toString().trimEnd(),
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
                webPreviewState = null,
                commentsState = StoryDetailCommentsState.Success(
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
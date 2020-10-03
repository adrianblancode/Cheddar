package co.adrianblan.storydetail.ui

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.ui.tooling.preview.Preview
import co.adrianblan.common.urlSiteName
import co.adrianblan.core.WebPreviewState
import co.adrianblan.core.ui.LinkIcon
import co.adrianblan.domain.Comment
import co.adrianblan.domain.Story
import co.adrianblan.domain.StoryUrl
import co.adrianblan.domain.placeholder
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R
import co.adrianblan.storydetail.StoryDetailCommentsState
import co.adrianblan.storydetail.StoryDetailViewState
import co.adrianblan.ui.*
import co.adrianblan.ui.utils.lerp
import co.adrianblan.webpreview.WebPreviewData
import kotlin.math.roundToInt

private const val toolbarMinHeightDp = 56
private const val toolbarMaxHeightDp = 148


@Composable
fun StoryDetailView(
    viewState: StoryDetailViewState,
    onStoryContentClick: (StoryUrl) -> Unit,
    onCommentUrlClicked: (Uri) -> Unit,
    onBackPressed: () -> Unit
) {

    val scrollState = rememberScrollState()

    CollapsingScaffold(
        scrollState = scrollState,
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
                            ScrollableColumn(scrollState = scrollState) {
                                Column {

                                    val topInsets =
                                        with(DensityAmbient.current) {
                                            InsetsAmbient.current.top.toDp()
                                        }

                                    Spacer(
                                        modifier = Modifier.preferredHeight(
                                            toolbarMaxHeightDp.dp + topInsets
                                        )
                                    )

                                    if (viewState.story.text != null) {
                                        CommentItem(
                                            text = story.text,
                                            by = story.by,
                                            depthIndex = 0,
                                            storyAuthor = story.by,
                                            onCommentUrlClicked = onCommentUrlClicked
                                        )
                                    }

                                    viewState.commentsState.comments
                                        .map { comment ->
                                            key(comment.comment.id) {
                                                CommentItem(
                                                    comment = comment,
                                                    storyAuthor = story.by,
                                                    onCommentUrlClicked = onCommentUrlClicked
                                                )
                                            }
                                        }

                                    with(DensityAmbient.current) {
                                        Spacer(modifier = Modifier.preferredHeight(InsetsAmbient.current.bottom.toDp() + 8.dp))
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
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier.padding(4.dp)
        ) {
            Icon(
                asset = Icons.Default.ArrowBack,
                tint = MaterialTheme.colors.onBackground
            )
        }

        val titleCollapsedLeftOffset =
            remember(collapsedFraction) { lerp(0.dp, 48.dp, collapsedFraction) }
        val titleCollapsedTopOffset =
            remember(collapsedFraction) { lerp(48.dp, 0.dp, collapsedFraction) }

        val titleFontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize

        val titleMaxLines =
            remember(collapsedFraction) { lerp(3f, 1f, collapsedFraction).roundToInt() }
        val imageSize = remember(collapsedFraction) { lerp(80.dp, 40.dp, collapsedFraction) }

        when (viewState) {
            is StoryDetailViewState.Loading ->
                Surface(
                    shape = RoundedCornerShape(2.dp),
                    modifier = Modifier.padding(16.dp)
                        .padding(top = titleCollapsedTopOffset)
                ) {
                    Column {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .preferredHeight(20.dp)
                        ) {
                            ShimmerView()
                        }
                        Spacer(modifier = Modifier.preferredHeight(6.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .preferredHeight(20.dp)
                        ) {
                            ShimmerView()
                        }
                    }
                }
            is StoryDetailViewState.Success -> {

                val story: Story = viewState.story
                val webPreviewState: WebPreviewState? = viewState.webPreviewState
                val webPreview: WebPreviewData? = (webPreviewState as? WebPreviewState.Success)
                    ?.webPreview

                val titleRightOffset =
                    if (story.url != null) imageSize + 12.dp
                    else 0.dp

                Column(
                    modifier = Modifier.padding(
                        start = 16.dp + titleCollapsedLeftOffset,
                        end = 12.dp + titleRightOffset,
                        bottom = 8.dp,
                        top = 8.dp + titleCollapsedTopOffset
                    )
                        .fillMaxWidth()
                        .preferredHeight(height)
                        .gravity(Alignment.CenterStart)
                        .wrapContentHeight(Alignment.CenterVertically)
                ) {

                    Text(
                        text = story.title,
                        style = MaterialTheme.typography.h6.copy(
                            fontSize = titleFontSize,
                            color = MaterialTheme.colors.onBackground
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = titleMaxLines
                    )

                    val siteName: String? = webPreview?.siteName ?: story.url?.url?.urlSiteName()

                    if (siteName != null) {
                        Text(
                            text = siteName,
                            style = MaterialTheme.typography.subtitle2.copy(
                                color = MaterialTheme.colors.onPrimary.copy(alpha = textSecondaryAlpha)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (story.url != null) {
                    Box(modifier = Modifier.gravity(Alignment.BottomEnd)) {
                        StoryDetailImage(
                            story = story,
                            webPreviewState = webPreviewState,
                            imageSize = imageSize,
                            onStoryContentClick = onStoryContentClick
                        )
                    }
                }
            }
            is StoryDetailViewState.Error -> {
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

    Box(
        modifier = Modifier.clickable(
            onClick = { story.url?.let { onStoryContentClick(it) } }
        ).padding(
            start = 8.dp,
            top = 8.dp,
            end = 16.dp,
            bottom = 8.dp
        )
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.preferredSize(imageSize)
        ) {
            Stack {
                Surface(
                    color = colorResource(R.color.contentMuted),
                    modifier = Modifier.fillMaxSize()
                ) {}

                when (webPreviewState) {
                    is WebPreviewState.Loading -> {
                        Stack(modifier = Modifier.fillMaxSize()) {
                            ShimmerView()
                            LinkIcon()
                        }
                    }
                    is WebPreviewState.Success -> {
                        val webPreview = webPreviewState.webPreview

                        val imageUrl =
                            webPreview.imageUrl ?: webPreview.iconUrl
                            ?: webPreview.favIconUrl

                        UrlImage(imageUrl) { LinkIcon() }
                    }
                    is WebPreviewState.Error -> {
                        LinkIcon()
                    }
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
                story = Story.placeholder,
                webPreviewState = null,
                commentsState = StoryDetailCommentsState.Success(
                    listOf(
                        FlatComment(Comment.placeholder, 0),
                        FlatComment(Comment.placeholder, 1),
                        FlatComment(Comment.placeholder, 2),
                        FlatComment(Comment.placeholder, 0)
                    )
                )
            )
        StoryDetailView(
            viewState = viewState,
            onStoryContentClick = {},
            onCommentUrlClicked = {},
            onBackPressed = {}
        )
    }
}

@Preview
@Composable
fun CommentsEmptyView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        padding = 32.dp,
        gravity = ContentGravity.Center
    ) {
        Text(
            text = stringResource(id = R.string.comments_empty),
            style = MaterialTheme.typography.h6.copy(textAlign = TextAlign.Center)
        )
    }
}
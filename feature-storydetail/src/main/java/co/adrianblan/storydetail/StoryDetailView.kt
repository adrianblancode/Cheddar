package co.adrianblan.storydetail

import android.text.Html
import androidx.compose.Composable
import androidx.compose.remember
import androidx.lifecycle.LiveData
import androidx.ui.core.Alignment
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Text
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import androidx.ui.unit.px
import co.adrianblan.common.lerp
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.ui.*
import co.adrianblan.ui.InsetsAmbient

@Composable
fun StoryDetailScreen(
    viewState: LiveData<StoryDetailViewState>,
    onStoryContentClicked: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    StoryDetailView(
        observe(viewState),
        onStoryContentClicked,
        onBackPressed
    )
}

@Composable
fun StoryDetailView(
    viewState: StoryDetailViewState,
    onStoryContentClicked: (String) -> Unit,
    onBackPressed: () -> Unit
) {

    val scroller = ScrollerPosition()
    val insets = InsetsAmbient.current

    Scaffold(
        topAppBar = {
            StoryDetailToolbar(
                storyTitle = (viewState as? StoryDetailViewState.Success)?.story?.title.orEmpty(),
                scroller = scroller,
                onBackPressed = onBackPressed
            )
        },
        bodyContent = {
            when (viewState) {
                is StoryDetailViewState.Loading -> LoadingView()
                is StoryDetailViewState.Success ->
                    VerticalScroller(scroller) {
                        Column {
                            viewState.items.map { item ->
                                when (item) {
                                    is StoryDetailItem.CommentItem ->
                                        CommentItem(item.comment)
                                    is StoryDetailItem.CommentLoadingItem ->
                                        LoadingView()
                                    is StoryDetailItem.CommentErrorItem ->
                                        ErrorView()
                                }
                            }

                            with (DensityAmbient.current) {
                                Spacer(modifier = LayoutHeight(insets.bottom.px.toDp()))
                            }
                        }
                    }
                is StoryDetailViewState.Error -> ErrorView()
            }
        },
        floatingActionButton = {
            val url = (viewState as? StoryDetailViewState.Success)?.story?.url
            if (url != null) {
                with(DensityAmbient.current) {
                    Container(padding = EdgeInsets(bottom = insets.bottom.px.toDp())) {
                        FloatingActionButton(
                            onClick = { onStoryContentClicked(url) }
                        ) {
                            VectorImage(
                                vector = Icons.Default.ArrowForward,
                                tint = MaterialTheme.colors().secondary
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
    scroller: ScrollerPosition,
    onBackPressed: () -> Unit
) {
    val maxHeight = 140.dp

    CollapsingToolbar(
        scroller = scroller,
        maxHeight = maxHeight
    ) { collapsedFraction, height ->

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

            val titleCollapsedLeftOffset = remember(collapsedFraction) { lerp(0.dp, 48.dp, collapsedFraction) }
            val titleCollapsedTopOffset = remember(collapsedFraction) { lerp(48.dp, 0.dp, collapsedFraction) }
            val titleMaxLines = remember(collapsedFraction) { if (collapsedFraction > 0.1f) 1 else 3 }

            Container(
                alignment = Alignment.BottomLeft,
                modifier = LayoutHeight.Min(height)
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
}

@Composable
fun CommentItem(comment: Comment) {
    Container(padding = EdgeInsets(left = 16.dp, right = 16.dp, top = 8.dp, bottom = 6.dp)) {
        Column(arrangement = Arrangement.Begin, modifier = LayoutWidth.Fill) {
            Text(
                text = comment.by.orEmpty(),
                style = MaterialTheme.typography().h6
            )
            Text(
                text = Html.fromHtml(comment.text.orEmpty()).toString().trimEnd(),
                style = MaterialTheme.typography().body1
            )
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
                items = listOf(
                    StoryDetailItem.CommentItem(Comment.dummy),
                    StoryDetailItem.CommentItem(Comment.dummy),
                    StoryDetailItem.CommentItem(Comment.dummy)
                )
            )
        StoryDetailView(
            viewState,
            onStoryContentClicked = {},
            onBackPressed = {}
        )
    }
}
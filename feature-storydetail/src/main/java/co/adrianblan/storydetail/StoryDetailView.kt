package co.adrianblan.storydetail

import android.text.Html
import androidx.compose.Composable
import androidx.lifecycle.LiveData
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.ui.*
import co.adrianblan.ui.R

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
                        }
                    }
                is StoryDetailViewState.Error -> ErrorView()
            }
        },
        floatingActionButton = {
            val url = (viewState as? StoryDetailViewState.Success)?.story?.url
            if (url != null) {
                FloatingActionButton(
                    onClick = { onStoryContentClicked(url) }
                ) {
                    VectorImage(Icons.Default.NavigateNext, tint = MaterialTheme.colors().secondary)
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
    CollapsingToolbar(scroller) { collapsed ->

        val minHeight = androidx.ui.unit.lerp(140.dp, 56.dp, collapsed)

        Container(
            padding = EdgeInsets(left = 16.dp, right = 16.dp, top = 16.dp, bottom = 12.dp)
        ) {
            Stack {
                VectorImageButton(
                    Icons.Default.ArrowBack,
                    tint = MaterialTheme.colors().onPrimary,
                    modifier = LayoutGravity.TopLeft
                ) {
                    onBackPressed()
                }

                Container(
                    modifier = LayoutGravity.BottomLeft,
                    constraints = DpConstraints(minHeight = minHeight)
                ) {
                    Text(
                        text = storyTitle,
                        style = MaterialTheme.typography().h6,
                        modifier = LayoutWidth.Fill + LayoutGravity.BottomLeft
                    )
                }
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
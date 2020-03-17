package co.adrianblan.storyfeed

import android.text.Html
import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowDropDown
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.lerp
import androidx.ui.unit.px
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.ui.*
import co.adrianblan.ui.InsetsAmbient


@Composable
fun StoryFeedScreen(
    viewState: LiveData<StoryFeedViewState>,
    onStoryClick: (StoryId) -> Unit
) {
    StoryFeedView(
        observe(viewState), onStoryClick
    )
}

@Composable
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit
) {
    val scroller = ScrollerPosition()

    Scaffold(
        topAppBar = {
            StoryFeedToolbar(scroller)
        },
        bodyContent = {
            when (viewState) {
                is StoryFeedViewState.Loading -> LoadingView()
                is StoryFeedViewState.Success ->
                    // TODO change to AdapterList
                    VerticalScroller(scrollerPosition = scroller) {
                        Column {
                            viewState.stories.map { story ->
                                key(story.id) {
                                    StoryFeedItem(story, onStoryClick)
                                }
                            }

                            val insets = InsetsAmbient.current
                            with (DensityAmbient.current) {
                                Spacer(modifier = LayoutHeight(insets.bottom.px.toDp()))
                            }
                        }
                    }
                is StoryFeedViewState.Error -> ErrorView()
            }
        }
    )
}

@Composable
fun StoryFeedToolbar(scroller: ScrollerPosition) {
    CollapsingToolbar(scroller) { collapsed ->

        val headerTextSize =
            lerp(
                MaterialTheme.typography().h4.fontSize,
                MaterialTheme.typography().h6.fontSize,
                collapsed
            )

        val height = lerp(100.dp, 56.dp, collapsed)

        Container(
            padding = EdgeInsets(12.dp),
            alignment = Alignment.BottomLeft,
            constraints = DpConstraints(minHeight = height)
        ) {

            val showStoryTypePopup = state { false }

            // Seems like if statement does not automatically recompose on state change
            Recompose { recompose ->
                StoryFeedHeader(headerTextSize = headerTextSize) {
                    showStoryTypePopup.value = true
                    recompose()
                }

                if (showStoryTypePopup.value) {
                    StoryTypePopup {
                        showStoryTypePopup.value = false
                        recompose()
                    }
                }
            }
        }
    }
}

@Composable
fun StoryFeedHeader(
    headerTextSize: TextUnit = MaterialTheme.typography().h6.fontSize,
    onClick: () -> Unit
) {
    Box(shape = RoundedCornerShape(4.dp)) {
        Ripple(true) {
            Clickable(onClick = onClick) {
                Container(padding = EdgeInsets(4.dp)) {
                    Row {
                        Text(
                            text = stringResource(R.string.stories_top_title),
                            style = MaterialTheme.typography().h4.copy(fontSize = headerTextSize)
                        )
                        VectorImage(
                            vector = Icons.Default.ArrowDropDown,
                            tint = MaterialTheme.colors().onBackground,
                            modifier = LayoutGravity.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryTypePopup(onDismiss: () -> Unit) {
    DropdownPopup(popupProperties = PopupProperties(true, onDismissRequest = onDismiss)) {
        Container(
            padding = EdgeInsets(20.dp),
            constraints = DpConstraints(maxWidth = 200.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colors().background,
                elevation = 4.dp
            ) {
                Column {
                    val storyTypes = remember { StoryType.values() }

                    storyTypes
                        .map { storyType ->
                            StoryTypePopupItem(storyType) {
                                onDismiss()
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun StoryTypePopupItem(storyType: StoryType, onClick: (StoryType) -> Unit) {
    Ripple(bounded = true) {
        Clickable(onClick = { onClick(storyType) }) {
            Container(
                padding = EdgeInsets(8.dp),
                modifier = LayoutWidth.Fill,
                alignment = Alignment.BottomLeft
            ) {
                Text(
                    text = stringResource(storyType.titleStringResource()),
                    style = MaterialTheme.typography().h6
                )
            }
        }
    }
}

private fun StoryType.titleStringResource(): Int =
    when (this) {
        StoryType.TOP -> R.string.stories_top_title
        StoryType.BEST -> R.string.stories_best_title
        StoryType.NEW -> R.string.stories_new_title
        StoryType.ASK -> R.string.stories_ask_title
        StoryType.SHOW -> R.string.stories_show_title
        StoryType.JOB -> R.string.stories_job_title
    }

@Composable
fun StoryFeedItem(story: Story, onStoryClick: (StoryId) -> Unit) {
    Ripple(bounded = true) {
        Clickable(onClick = { onStoryClick(story.id) }) {
            Container(
                padding = EdgeInsets(left = 16.dp, right = 16.dp, top = 16.dp, bottom = 12.dp)
            ) {
                Column(
                    arrangement = Arrangement.Begin,
                    modifier = LayoutWidth.Fill
                ) {
                    Text(
                        text = story.title,
                        style = MaterialTheme.typography().h6
                    )
                    story.text
                        .takeIf { !it.isNullOrEmpty() }
                        ?.let { text ->
                            Text(
                                text = Html.fromHtml(text).toString()
                                    .replace("\n\n", " "),
                                style = MaterialTheme.typography().body1,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                }
            }
        }
    }
}

@Preview
@Composable
fun StoryFeedPreview() {
    AppTheme {
        val viewState =
            StoryFeedViewState.Success(listOf(Story.dummy))
        StoryFeedView(viewState) {}
    }
}
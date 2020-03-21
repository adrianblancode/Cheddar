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
import androidx.ui.unit.*
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.ui.*
import co.adrianblan.ui.InsetsAmbient

private const val toolbarMinHeightDp = 56
private const val toolbarMaxHeightDp = 128

@Composable
fun StoryFeedScreen(
    viewState: LiveData<StoryFeedViewState>,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit
) {
    StoryFeedView(
        viewState = observe(viewState),
        onStoryTypeClick = onStoryTypeClick,
        onStoryClick = onStoryClick
    )
}

@Composable
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit
) {
    val scroller = ScrollerPosition()

    with(DensityAmbient.current) {
        onCommit(viewState.storyType) {
            val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

            // If story type is changed, collapse
            val scrollReset: Px = min(scroller.value.px, collapseDistance.toPx())
            scroller.scrollTo(scrollReset.value)
        }
    }

    CollapsingScaffold(
        scroller = scroller,
        maxHeight = toolbarMaxHeightDp.dp,
        toolbarContent = { collapseFraction, height ->
            StoryFeedToolbar(
                collapsedFraction = collapseFraction,
                height = height,
                storyType = viewState.storyType,
                onStoryTypeClick = onStoryTypeClick
            )
        },
        bodyContent = {
            when (val storyFeedState: StoryFeedState = viewState.storyFeedState) {
                is StoryFeedState.Loading -> LoadingView()
                is StoryFeedState.Success ->
                    // TODO change to AdapterList
                    VerticalScroller(scrollerPosition = scroller) {
                        Column {

                            with(DensityAmbient.current) {
                                val insets = InsetsAmbient.current
                                val topInsets = insets.top.px.toDp()

                                Spacer(modifier = LayoutHeight(toolbarMaxHeightDp.dp + topInsets))

                                storyFeedState.stories.map { story ->
                                    key(story.id) {
                                        StoryFeedItem(story, onStoryClick)
                                    }
                                }

                                Spacer(modifier = LayoutHeight(insets.bottom.px.toDp()))
                            }
                        }
                    }
                is StoryFeedState.Error -> ErrorView()
            }
        }
    )
}

@Composable
fun StoryFeedToolbar(
    collapsedFraction: Float,
    height: Dp,
    storyType: StoryType,
    onStoryTypeClick: (StoryType) -> Unit
) {

    Container(
        padding = EdgeInsets(12.dp),
        alignment = Alignment.BottomLeft,
        modifier = LayoutHeight(height)
    ) {

        val headerTextSize =
            lerp(
                MaterialTheme.typography().h4.fontSize,
                MaterialTheme.typography().h6.fontSize,
                collapsedFraction
            )

        val showStoryTypePopup = state { false }

        // Seems like if statement does not automatically recompose on state change
        Recompose { recompose ->
            StoryFeedHeader(
                headerTextSize = headerTextSize,
                storyType = storyType
            ) {
                showStoryTypePopup.value = true
                recompose()
            }

            if (showStoryTypePopup.value) {
                StoryTypePopup(
                    onStoryTypeClick = { storyType ->
                        onStoryTypeClick(storyType)
                        showStoryTypePopup.value = false
                        recompose()
                    },
                    onDismiss = {
                        showStoryTypePopup.value = false
                        recompose()
                    }
                )
            }
        }
    }
}

@Composable
fun StoryFeedHeader(
    storyType: StoryType,
    headerTextSize: TextUnit = MaterialTheme.typography().h6.fontSize,
    onClick: () -> Unit
) {
    Ripple(true, radius = 4.dp) {
        Clickable(onClick = onClick) {
            Container(padding = EdgeInsets(4.dp)) {
                Row {

                    val title = remember(storyType) {
                        stringResource(storyType.titleStringResource())
                    }
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography().h4
                            .copy(
                                fontSize = headerTextSize,
                                color = MaterialTheme.colors().onPrimary
                            )
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

@Composable
fun StoryTypePopup(
    onStoryTypeClick: (StoryType) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownPopup(popupProperties = PopupProperties(true, onDismissRequest = onDismiss)) {
        // Popups require reapplication of the app theme
        AppTheme {
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
                                    onStoryTypeClick(storyType)
                                    onDismiss()
                                }
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
        val viewState = StoryFeedViewState(
            StoryType.TOP,
            StoryFeedState.Success(listOf(Story.dummy))
        )

        StoryFeedView(
            viewState,
            onStoryTypeClick = {},
            onStoryClick = {}
        )
    }
}
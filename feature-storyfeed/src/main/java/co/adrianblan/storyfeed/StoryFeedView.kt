package co.adrianblan.storyfeed

import android.text.Html
import androidx.compose.*
import androidx.lifecycle.LiveData
import androidx.ui.core.*
import androidx.ui.foundation.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowDropDown
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
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

internal fun StoryType.titleStringResource(): Int =
    when (this) {
        StoryType.TOP -> R.string.stories_top_title
        StoryType.BEST -> R.string.stories_best_title
        StoryType.NEW -> R.string.stories_new_title
        StoryType.ASK -> R.string.stories_ask_title
        StoryType.SHOW -> R.string.stories_show_title
        StoryType.JOB -> R.string.stories_job_title
    }

@Composable
fun StoryFeedScreen(
    viewState: LiveData<StoryFeedViewState>,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onPageEndReached: () -> Unit
) {
    StoryFeedView(
        viewState = observe(viewState),
        onStoryTypeClick = onStoryTypeClick,
        onStoryClick = onStoryClick,
        onPageEndReached = onPageEndReached
    )
}

@Composable
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onPageEndReached: () -> Unit
) {
    val scroller = ScrollerPosition()

    with(DensityAmbient.current) {
        onCommit(viewState.storyType) {

            val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

            // If story type is changed, revert scroll but retain toolbar collapse state
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
            StoryFeedBodyContent(
                scroller = scroller,
                viewState = viewState,
                onStoryClick = onStoryClick,
                onPageEndReached = onPageEndReached
            )
        }
    )
}

@Composable
fun StoryFeedBodyContent(
    scroller: ScrollerPosition,
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit,
    onPageEndReached: () -> Unit
) {

    when (viewState.storyFeedState) {
        is StoryFeedState.Loading -> LoadingView()
        is StoryFeedState.Success -> {
            StoryFeedSuccessContentBody(
                scroller = scroller,
                viewState = viewState,
                onStoryClick = onStoryClick,
                onPageEndReached = onPageEndReached
            )
        }
        is StoryFeedState.Error -> ErrorView()
    }
}

@Composable
fun StoryFeedSuccessContentBody(
    scroller: ScrollerPosition,
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit,
    onPageEndReached: () -> Unit
) {
    val storyFeedState = viewState.storyFeedState as StoryFeedState.Success

    val scrollEndZone: Px =  with(DensityAmbient.current) { 120.dp.toPx() }

    /*
    Observe {
        val isScrolledToEnd: Boolean =
            scroller.value > scroller.maxPosition - scrollEndZone.value

        onCommit(isScrolledToEnd) {
            if (isScrolledToEnd) {
                onPageEndReached()
            }
        }
    }
     */

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

                when {
                    viewState.isLoadingMorePages -> LoadingMoreStoriesView()
                    viewState.hasLoadedAllPages -> NoMoreStoriesView()
                    !viewState.hasLoadedAllPages ->
                        Button(
                            modifier = LayoutWidth.Fill + LayoutHeight.Min(72.dp),
                            onClick = onPageEndReached
                        ) {
                            Text(stringResource(R.string.stories_load_more_stories))
                        }
                }

                Spacer(modifier = LayoutHeight(insets.bottom.px.toDp() + 8.dp))
            }
        }
    }
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
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent
    ) {
        Ripple(true) {
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
}

@Composable
fun StoryFeedItem(
    story: Story,
    onStoryClick: (StoryId) -> Unit
) {
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
                        style = MaterialTheme.typography().subtitle1
                    )
                    story.text
                        .takeIf { !it.isNullOrEmpty() }
                        ?.let { text ->
                            Text(
                                text = Html.fromHtml(text).toString()
                                    .replace("\n\n", " "),
                                style = MaterialTheme.typography().body2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                }
            }
        }
    }
}

@Composable
private fun LoadingMoreStoriesView() {
    Container(padding = EdgeInsets(8.dp), modifier = LayoutWidth.Fill) {
        Text(
            text = stringResource(id = R.string.loading_title),
            style = MaterialTheme.typography().subtitle1
                .copy(
                    color = MaterialTheme.colors().onPrimary.copy(alpha = textSecondaryAlpha),
                    textAlign = TextAlign.Center
                ),
            modifier = LayoutAlign.Center
        )
    }
}

@Composable
private fun NoMoreStoriesView() {
    Container(
        expanded = true,
        padding = EdgeInsets(32.dp),
        modifier = LayoutHeight(180.dp)
    ) {
        Text(
            text = stringResource(id = R.string.stories_no_more_stories),
            style = MaterialTheme.typography().subtitle2
                .copy(
                    color = MaterialTheme.colors().onPrimary.copy(alpha = textSecondaryAlpha),
                    textAlign = TextAlign.Center
                ),
            modifier = LayoutAlign.Center
        )
    }
}

@Preview
@Composable
fun StoryFeedPreview() {
    AppTheme {
        val viewState = StoryFeedViewState(
            StoryType.TOP,
            StoryFeedState.Success(listOf(Story.dummy)),
            isLoadingMorePages = true,
            hasLoadedAllPages = false
        )

        StoryFeedView(
            viewState,
            onStoryTypeClick = {},
            onStoryClick = {},
            onPageEndReached = {}
        )
    }
}
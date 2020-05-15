package co.adrianblan.storyfeed.ui

import androidx.compose.*
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextAlign
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Px
import androidx.ui.unit.dp
import androidx.ui.unit.min
import androidx.ui.unit.px
import co.adrianblan.hackernews.StoryType
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.storyfeed.*
import co.adrianblan.storyfeed.R
import co.adrianblan.ui.*
import timber.log.Timber

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
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {
    val scroller = ScrollerPosition()
    val densityAmbient = DensityAmbient.current

    onCommit(viewState.storyType) {

        val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

        // If story type is changed, revert scroll but retain toolbar collapse state
        val scrollReset: Px =
            with(densityAmbient) {
                min(scroller.value.px, collapseDistance.toPx())
            }

        scroller.scrollTo(scrollReset.value)
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
                onStoryContentClick = onStoryContentClick,
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
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    when (viewState.storyFeedState) {
        is StoryFeedState.Loading -> LoadingView()
        is StoryFeedState.Success -> {
            StoryFeedSuccessContentBody(
                scroller = scroller,
                viewState = viewState,
                onStoryClick = onStoryClick,
                onStoryContentClick = onStoryContentClick,
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
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {
    val storyFeedState = viewState.storyFeedState as StoryFeedState.Success

    val scrollEndZone: Px = with(DensityAmbient.current) {
        400.dp.toPx()
    }

    Observe {
        val isScrolledToEnd: Boolean =
            scroller.value > scroller.maxPosition - scrollEndZone.value

        onCommit(isScrolledToEnd) {
            if (isScrolledToEnd) {

                // Stop scroll fling
                scroller.scrollTo(scroller.value)

                onPageEndReached()
            }
        }
    }

    // TODO change to AdapterList
    VerticalScroller(scrollerPosition = scroller) {

        Column {

            with(DensityAmbient.current) {
                val insets = InsetsAmbient.current
                val topInsets = insets.top.px.toDp()

                Spacer(modifier = Modifier.preferredHeight(toolbarMaxHeightDp.dp + topInsets))

                storyFeedState.stories.map { story ->
                    key(story.story.id) {
                        StoryFeedItem(
                            decoratedStory = story,
                            onStoryClick = onStoryClick,
                            onStoryContentClick = onStoryContentClick
                        )
                    }
                }

                when {
                    viewState.isLoadingMorePages -> LoadingMoreStoriesView()
                    !viewState.hasLoadedAllPages -> LoadMoreStoriesButton(
                        onPageEndReached = onPageEndReached
                    )
                    viewState.hasLoadedAllPages -> NoMoreStoriesView()
                }

                Spacer(modifier = Modifier.preferredHeight(insets.bottom.px.toDp() + 8.dp))
            }
        }
    }
}

@Composable
private fun LoadingMoreStoriesView() {
    Box(
        padding = 8.dp,
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        LoadingView(
            textStyle = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun LoadMoreStoriesButton(
    onPageEndReached: () -> Unit
) {
    Box(
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Button(onClick = onPageEndReached) {
            Text(
                stringResource(R.string.stories_load_more_stories),
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
                )
            )
        }
    }
}

@Composable
private fun NoMoreStoriesView() {
    Box(
        gravity = ContentGravity.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.stories_no_more_stories),
            style = MaterialTheme.typography.subtitle2
                .copy(
                    color = MaterialTheme.colors.onPrimary.copy(alpha = textSecondaryAlpha),
                    textAlign = TextAlign.Center
                ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 8.dp
            )
        )
    }
}

@Preview
@Composable
fun StoryFeedPreview() {
    AppTheme {
        val viewState = StoryFeedViewState(
            StoryType.TOP,
            StoryFeedState.Success(List(10) {
                DecoratedStory(
                    Story.dummy,
                    WebPreviewState.Loading
                )
            }),
            isLoadingMorePages = true,
            hasLoadedAllPages = false
        )

        StoryFeedView(
            viewState,
            onStoryTypeClick = {},
            onStoryClick = {},
            onStoryContentClick = {},
            onPageEndReached = {}
        )
    }
}
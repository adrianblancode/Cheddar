package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.onCommit
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import co.adrianblan.core.DecoratedStory
import co.adrianblan.core.WebPreviewState
import co.adrianblan.domain.*
import co.adrianblan.storyfeed.R
import co.adrianblan.storyfeed.StoryFeedState
import co.adrianblan.storyfeed.StoryFeedViewState
import co.adrianblan.ui.*
import kotlin.math.min

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

// TODO fixme
private var initialScrollPosition = 0f

@Composable
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    val scrollState = rememberScrollState(initialScrollPosition)
    val densityAmbient = DensityAmbient.current

    // Prevent resetting scroll state on first commit
    val lastStoryType = savedInstanceState { viewState.storyType }

    onCommit(viewState.storyType) {

        if (viewState.storyType == lastStoryType.value) return@onCommit

        val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

        // If story type is changed, reset scroll but retain toolbar collapse state
        val scrollReset: Float =
            with(densityAmbient) {
                min(scrollState.value, collapseDistance.toPx())
            }

        scrollState.scrollTo(scrollReset)

        lastStoryType.value = viewState.storyType
    }

    onDispose {
        initialScrollPosition = scrollState.value
    }

    CollapsingScaffold(
        scrollState = scrollState,
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
                scrollState = scrollState,
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
    scrollState: ScrollState,
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    when (viewState.storyFeedState) {
        is StoryFeedState.Loading -> LoadingView()
        is StoryFeedState.Success -> {
            StoryFeedSuccessContentBody(
                scrollState = scrollState,
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
    scrollState: ScrollState,
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {
    val storyFeedState = viewState.storyFeedState as StoryFeedState.Success

    val scrollEndZone = with(DensityAmbient.current) {
        400.dp.toPx()
    }

    Observe {
        val isScrolledToEnd: Boolean =
            scrollState.value > scrollState.maxValue - scrollEndZone

        onCommit(isScrolledToEnd) {
            if (isScrolledToEnd) {

                // Stop scroll fling
                scrollState.scrollTo(scrollState.value)

                onPageEndReached()
            }
        }
    }

    // TODO change to AdapterList
    ScrollableColumn(scrollState = scrollState) {

        Column {

            val insets = InsetsAmbient.current

            val topInsets = with(DensityAmbient.current) { insets.top.toDp() }
            val bottomInsets = with(DensityAmbient.current) { insets.bottom.toDp() }

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


            Spacer(modifier = Modifier.preferredHeight(bottomInsets + 8.dp))
        }
    }
}

@Composable
private fun LoadingMoreStoriesView() {
    Box(
        alignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
            .padding(8.dp)
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
        alignment = Alignment.Center,
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
        alignment = Alignment.Center,
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
                    Story.placeholder,
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

package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.model.StoryUrl
import co.adrianblan.model.WebPreviewState
import co.adrianblan.model.placeholderLink
import co.adrianblan.storyfeed.R
import co.adrianblan.storyfeed.StoryFeedState
import co.adrianblan.storyfeed.StoryFeedViewModel
import co.adrianblan.storyfeed.StoryFeedViewState
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.CollapsingScaffold
import co.adrianblan.ui.ErrorView
import co.adrianblan.ui.LoadingText
import co.adrianblan.ui.LoadingVisual
import co.adrianblan.ui.textSecondaryAlpha
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.math.min
import co.adrianblan.ui.R as UiR

private const val toolbarMinHeightDp = 60
private const val toolbarMaxHeightDp = 128

internal fun StoryType.titleStringResource(): Int =
    when (this) {
        StoryType.TOP -> UiR.string.stories_top_title
        StoryType.BEST -> UiR.string.stories_best_title
        StoryType.NEW -> UiR.string.stories_new_title
        StoryType.ASK -> UiR.string.stories_ask_title
        StoryType.SHOW -> UiR.string.stories_show_title
        StoryType.JOB -> UiR.string.stories_job_title
    }

@Composable
internal fun StoryFeedRoute(
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    viewModel: StoryFeedViewModel = hiltViewModel(),
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    StoryFeedScreen(
        viewState,
        onStoryTypeClick = { storyType -> viewModel.onStoryTypeChanged(storyType) },
        onStoryClick = onStoryClick,
        onStoryContentClick = onStoryContentClick,
        onPageEndReached = { viewModel.onPageEndReached() }
    )
}

@Composable
internal fun StoryFeedScreen(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    val localDensity = LocalDensity.current
    val scrollState = rememberScrollState()

    var lastStoryType: StoryType by remember { mutableStateOf(viewState.storyType) }

    // Scroll to top on story type change
    LaunchedEffect(viewState.storyType) {

        // If navigating back, skip
        if (lastStoryType == viewState.storyType) return@LaunchedEffect
        lastStoryType = viewState.storyType

        val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

        // Scroll to top, but retain toolbar collapse state
        val scrollReset: Int = with(localDensity) {
            min(scrollState.value, collapseDistance.roundToPx())
        }

        scrollState.scrollTo(scrollReset)
    }

    CollapsingScaffold(
        scrollState = scrollState,
        minHeight = toolbarMinHeightDp.dp,
        maxHeight = toolbarMaxHeightDp.dp,
        toolbarContent = { collapseFraction ->
            StoryFeedToolbar(
                collapsedFraction = collapseFraction,
                storyType = viewState.storyType,
                onStoryTypeClick = onStoryTypeClick
            )
        },
        bodyContent = {
            BodyContent(
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
private fun BodyContent(
    scrollState: ScrollState,
    viewState: StoryFeedViewState,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    when (viewState.storyFeedState) {
        is StoryFeedState.Loading -> LoadingVisual(modifier = Modifier.fillMaxSize())
        is StoryFeedState.Success -> {
            SuccessBody(
                scrollState = scrollState,
                storyFeedState = viewState.storyFeedState,
                onStoryClick = onStoryClick,
                onStoryContentClick = onStoryContentClick,
                onPageEndReached = onPageEndReached
            )
        }

        is StoryFeedState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SuccessBody(
    scrollState: ScrollState,
    storyFeedState: StoryFeedState.Success,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    val scrollEndZone = with(LocalDensity.current) {
        400.dp.toPx()
    }

    val isScrolledToEnd: Boolean by remember(scrollState.value, scrollState.maxValue) {
        derivedStateOf {
            scrollState.value > (scrollState.maxValue - scrollEndZone)
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd) {
            onPageEndReached()
        }
    }

    // TODO change to LazyColumn once scroll state is fixed
    Column(modifier = Modifier.verticalScroll(scrollState)) {

        Spacer(
            modifier = Modifier
                .statusBarsPadding()
                .height(toolbarMaxHeightDp.dp)
        )

        storyFeedState.stories.forEach { story ->
            key(story.story.id) {
                StoryFeedItem(
                    decoratedStory = story,
                    onStoryClick = onStoryClick,
                    onStoryContentClick = onStoryContentClick
                )
            }
        }

        if (storyFeedState.hasLoadedAllPages) NoMoreStoriesView()
        else LoadingMoreStoriesView()

        Spacer(
            modifier = Modifier
                .navigationBarsPadding()
                .height(8.dp)
        )
    }
}

@Composable
private fun LoadingMoreStoriesView() {

    LoadingText(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 12.dp
            )
            .fillMaxWidth()
    )
}

@Composable
private fun NoMoreStoriesView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 12.dp
            )
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.stories_no_more_stories),
            style = MaterialTheme.typography.titleMedium
                .copy(
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = textSecondaryAlpha),
                    textAlign = TextAlign.Center
                ),
        )
    }
}

@Preview
@Composable
private fun StoryFeedPreview() {
    AppTheme {
        val viewState = StoryFeedViewState(
            StoryType.TOP,
            StoryFeedState.Success(
                List(10) {
                    DecoratedStory(
                        Story.placeholderLink,
                        WebPreviewState.Loading
                    )
                }.toImmutableList(),
                hasLoadedAllPages = false
            )
        )

        StoryFeedScreen(
            viewState,
            onStoryTypeClick = {},
            onStoryClick = {},
            onStoryContentClick = {},
            onPageEndReached = {}
        )
    }
}

@Preview
@Composable
private fun LoadingMoreStoriesPreview() {
    AppTheme {
        LoadingMoreStoriesView()
    }
}

@Preview
@Composable
private fun NoMoreStoriesPreview() {
    AppTheme {
        NoMoreStoriesView()
    }
}
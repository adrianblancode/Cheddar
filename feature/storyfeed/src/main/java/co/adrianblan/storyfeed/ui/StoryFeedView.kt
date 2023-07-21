package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import co.adrianblan.domain.WebPreviewState
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.model.StoryUrl
import co.adrianblan.model.placeholder
import co.adrianblan.storyfeed.R
import co.adrianblan.storyfeed.StoryFeedState
import co.adrianblan.storyfeed.StoryFeedViewModel
import co.adrianblan.storyfeed.StoryFeedViewState
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.CollapsingScaffold
import co.adrianblan.ui.ErrorView
import co.adrianblan.ui.LoadingView
import co.adrianblan.ui.LocalInsets
import co.adrianblan.ui.textSecondaryAlpha
import kotlinx.coroutines.launch
import kotlin.math.min
import co.adrianblan.ui.R as UiR

private const val toolbarMinHeightDp = 56
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

// TODO fixme
private var initialScrollPosition = 0

@Composable
fun StoryFeedViewWrapper(
    viewModel: StoryFeedViewModel = hiltViewModel(),
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit
) {

    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    StoryFeedView(
        viewState,
        onStoryTypeClick = { storyType -> viewModel.onStoryTypeChanged(storyType) },
        onStoryClick = onStoryClick,
        onStoryContentClick = onStoryContentClick,
        onPageEndReached = { viewModel.onPageEndReached() }
    )
}

@Composable
fun StoryFeedView(
    viewState: StoryFeedViewState,
    onStoryTypeClick: (StoryType) -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit
) {

    val scrollState = rememberScrollState(initialScrollPosition)
    val localDensity = LocalDensity.current

    // Prevent resetting scroll state on first commit
    var lastStoryType by remember { mutableStateOf(viewState.storyType) }

    val scope = rememberCoroutineScope()

    DisposableEffect(viewState.storyType) {

        if (viewState.storyType == lastStoryType) return@DisposableEffect onDispose {}

        val collapseDistance = (toolbarMaxHeightDp - toolbarMinHeightDp).dp

        // If story type is changed, reset scroll but retain toolbar collapse state
        val scrollReset: Int =
            with(localDensity) {
                min(scrollState.value, collapseDistance.roundToPx())
            }

        scope.launch { scrollState.scrollTo(scrollReset) }

        lastStoryType = viewState.storyType

        onDispose { initialScrollPosition = scrollState.value }
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

    // TODO change to LazyColumn
    Column(modifier = Modifier.verticalScroll(scrollState)) {

        val insets = LocalInsets.current

        val topInsets = with(LocalDensity.current) { insets.top.toDp() }
        val bottomInsets = with(LocalDensity.current) { insets.bottom.toDp() }

        Spacer(modifier = Modifier.height(toolbarMaxHeightDp.dp + topInsets))

        storyFeedState.stories.forEach { story ->
            key(story.story.id) {
                StoryFeedItem(
                    decoratedStory = story,
                    onStoryClick = onStoryClick,
                    onStoryContentClick = onStoryContentClick
                )
            }
        }

        if (viewState.hasLoadedAllPages) NoMoreStoriesView()
        else LoadingMoreStoriesView()

        Spacer(modifier = Modifier.height(bottomInsets + 8.dp))
    }
}

@Composable
private fun LoadingMoreStoriesView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        LoadingView(
            textStyle = MaterialTheme.typography.subtitle1
        )
    }
}

@Composable
private fun NoMoreStoriesView() {
    Box(
        contentAlignment = Alignment.Center,
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

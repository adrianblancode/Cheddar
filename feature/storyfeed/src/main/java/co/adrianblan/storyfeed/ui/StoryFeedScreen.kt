package co.adrianblan.storyfeed.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import co.adrianblan.ui.ErrorView
import co.adrianblan.ui.LoadingSpinner
import co.adrianblan.ui.LoadingText
import co.adrianblan.ui.ToolbarNestedScrollConnection
import co.adrianblan.ui.textSecondaryAlpha
import kotlinx.collections.immutable.toImmutableList
import co.adrianblan.ui.R as UiR

private val toolbarMinHeight = 60.dp
private val toolbarMaxHeight = 128.dp

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

    val collapseDistance = with(localDensity) {
        remember(localDensity) {
            (toolbarMaxHeight - toolbarMinHeight).roundToPx()
        }
    }

    val lazyListState = rememberLazyListState()

    val nestedScrollHeightPx = with(LocalDensity.current) {
        (toolbarMaxHeight - toolbarMinHeight).toPx()
    }
    val nestedScrollConnection = remember(nestedScrollHeightPx) {
        ToolbarNestedScrollConnection(nestedScrollHeightPx)
    }

    var lastStoryType: StoryType by remember { mutableStateOf(viewState.storyType) }

    // Scroll to top on story type change
    LaunchedEffect(viewState.storyType) {

        // If navigating back, skip
        if (lastStoryType == viewState.storyType) return@LaunchedEffect
        lastStoryType = viewState.storyType

        lazyListState.scrollToItem(0)
    }

    val toolbar: @Composable () -> Unit = {
        StoryFeedToolbar(
            collapseProgress = { nestedScrollConnection.progress() },
            storyType = viewState.storyType,
            onStoryTypeClick = onStoryTypeClick
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (viewState.storyFeedState) {
            is StoryFeedState.Loading -> LoadingSpinner(modifier = Modifier.fillMaxSize())
            is StoryFeedState.Success -> {
                SuccessBody(
                    storyFeedState = viewState.storyFeedState,
                    lazyListState = lazyListState,
                    toolbar = {
                        stickyHeader {
                            toolbar()
                        }
                    },
                    onStoryClick = onStoryClick,
                    onStoryContentClick = onStoryContentClick,
                    onPageEndReached = onPageEndReached,
                    modifier = Modifier.nestedScroll(nestedScrollConnection)
                )
            }

            is StoryFeedState.Error -> ErrorView(modifier = Modifier.fillMaxSize())
        }

        if (viewState.storyFeedState !is StoryFeedState.Success) {
            toolbar()
        }
    }
}

@Composable
private fun SuccessBody(
    storyFeedState: StoryFeedState.Success,
    lazyListState: LazyListState,
    toolbar: LazyListScope.() -> Unit,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit,
    onPageEndReached: () -> Unit,
    modifier: Modifier = Modifier
) {

    val scrolledToEndKey = "scrolledToEnd"
    val isScrolledToEnd: Boolean by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo
                .any { it.key == scrolledToEndKey }
        }
    }

    LaunchedEffect(isScrolledToEnd) {
        if (isScrolledToEnd) {
            onPageEndReached()
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        toolbar()

        items(
            items = storyFeedState.stories,
            key = { it.story.id.id },
            contentType = { "story" },
            itemContent = { story ->
                StoryFeedItem(
                    decoratedStory = story,
                    onStoryClick = onStoryClick,
                    onStoryContentClick = onStoryContentClick
                )
            }
        )

        item(key = scrolledToEndKey) {
            if (storyFeedState.hasLoadedAllPages) NoMoreStoriesView()
            else LoadingMoreStoriesView()
        }

        item {
            Spacer(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(8.dp)
            )
        }
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
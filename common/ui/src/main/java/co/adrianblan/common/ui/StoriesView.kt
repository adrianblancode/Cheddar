package co.adrianblan.common.ui

import android.text.Html
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.lifecycle.LiveData
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.stringResource
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy

sealed class StoriesViewState {
    data class Success(val stories: List<Story>) : StoriesViewState()
    object Loading : StoriesViewState()
    object Error : StoriesViewState()
}

// TODO remove block later
@Composable
fun StoriesScreen(
    viewState: LiveData<StoriesViewState>,
    onStoryClick: (StoryId) -> Unit
) {
    StoriesView(+observe(viewState), onStoryClick)
}

@Composable
fun StoriesView(
    viewState: StoriesViewState,
    onStoryClick: (StoryId) -> Unit
) {
    FlexColumn {
        inflexible {
            TopAppBar(
                title = {
                    Text(
                        text = +stringResource(R.string.app_name),
                        style = (+MaterialTheme.typography()).h6
                    )
                }
            )
        }
        expanded(1f) {
            when (viewState) {
                is StoriesViewState.Loading -> LoadingView()
                is StoriesViewState.Success ->
                    VerticalScroller {
                        Column {
                            viewState.stories.map { story ->
                                StoryItem(story, onStoryClick)
                            }
                        }
                    }
                is StoriesViewState.Error -> ErrorView()
            }
        }
    }
}

@Composable
fun StoryItem(story: Story, onStoryClick: (StoryId) -> Unit) {
    Ripple(bounded = true) {
        Clickable(onClick = { onStoryClick(story.id) }) {
            Padding(left = 16.dp, right = 16.dp, top = 16.dp, bottom = 12.dp) {
                Column(arrangement = Arrangement.Begin, modifier = ExpandedWidth) {
                    Text(
                        text = story.title,
                        style = (+MaterialTheme.typography()).h6
                    )
                    story.text
                        .takeIf { !it.isNullOrEmpty() }
                        ?.let { text ->
                            Text(
                                text = Html.fromHtml(text).toString()
                                    .replace("\n\n", " "),
                                style = (+MaterialTheme.typography()).body1,
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
fun StoriesPreview() {
    AppTheme {
        val viewState = StoriesViewState.Success(listOf(Story.dummy))
        StoriesView(viewState) {}
    }
}
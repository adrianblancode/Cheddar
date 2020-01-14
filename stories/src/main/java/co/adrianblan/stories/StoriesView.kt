package co.adrianblan.stories

import android.app.Activity
import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.lifecycle.LiveData
import androidx.ui.core.Alignment
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.DrawImage
import androidx.ui.foundation.SimpleImage
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.FloatingActionButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.material.ripple.Ripple
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import co.adrianblan.common.ui.AppTheme
import co.adrianblan.common.ui.ErrorView
import co.adrianblan.common.ui.LoadingView
import co.adrianblan.common.ui.observe
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.dummy

sealed class StoriesViewState {
    data class Success(val stories: List<Story>) : StoriesViewState()
    object Loading : StoriesViewState()
    object Error : StoriesViewState()
}

// TODO remove block later
fun Activity.setupView(stateBlock: () -> LiveData<StoriesViewState>) {
    setContent {
        AppTheme {
            val viewState = +observe(stateBlock())
            StoriesView(viewState)
        }
    }
}

@Composable
fun StoriesView(viewState: StoriesViewState) {
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
                                StoryItem(story)
                            }
                        }
                    }
                is StoriesViewState.Error -> ErrorView()
            }
        }
    }
}

@Composable
fun StoryItem(story: Story) {
    Ripple(bounded = true) {
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
                            text = text,
                            style = (+MaterialTheme.typography()).body1
                        )
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
        StoriesView(viewState)
    }
}
package co.adrianblan.common.ui

import android.text.Html
import androidx.compose.Composable
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.LiveData
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.foundation.ScrollerPosition
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TopAppBar
import androidx.ui.res.imageResource
import androidx.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.dummy

sealed class StoryDetailViewState {
    data class Success(val story: Story) : StoryDetailViewState()
    object Loading : StoryDetailViewState()
    object Error : StoryDetailViewState()
}

// TODO remove block later
@Composable
fun StoryDetailScreen(
    stateBlock: () -> LiveData<StoryDetailViewState>,
    onBackPressed: () -> Unit
) {
    val viewState = +observe(stateBlock())
    StoryDetailView(viewState, onBackPressed)
}


@Composable
fun StoryDetailView(viewState: StoryDetailViewState, onBackPressed: () -> Unit) {
    FlexColumn {
        inflexible {
            TopAppBar(
                navigationIcon = {
                  VectorImageButton(id = R.drawable.ic_back, onClick = onBackPressed)
                },
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
                is StoryDetailViewState.Loading -> LoadingView()
                is StoryDetailViewState.Success -> StoryDetailHeader(viewState.story)
                is StoryDetailViewState.Error -> ErrorView()
            }
        }
    }
}

@Composable
fun StoryDetailHeader(story: Story) {
    VerticalScroller {
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
                            text = Html.fromHtml(text).toString(),
                            style = (+MaterialTheme.typography()).body1
                        )
                    }
            }
        }
    }
}

@Preview
@Composable
fun StoryDetailPreview() {
    val viewState = StoryDetailViewState.Success(Story.dummy)
    StoryDetailView(viewState) {}
}
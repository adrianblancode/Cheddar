package co.adrianblan.storyfeed.ui

import android.text.Html
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.adrianblan.common.urlSiteName
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryUrl
import co.adrianblan.model.WebPreviewData
import co.adrianblan.model.WebPreviewState
import co.adrianblan.model.placeholder
import co.adrianblan.model.placeholderLink
import co.adrianblan.model.placeholderPost
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.ShimmerView
import co.adrianblan.ui.StoryImage
import co.adrianblan.ui.textSecondaryAlpha

@Composable
fun StoryFeedItem(
    decoratedStory: DecoratedStory,
    onStoryClick: (StoryId) -> Unit,
    onStoryContentClick: (StoryUrl) -> Unit
) {
    val story: Story = decoratedStory.story
    val storyUrl: StoryUrl? = story.url

    val webPreviewState: WebPreviewState? = decoratedStory.webPreviewState

    val storyClick: () -> Unit = remember {
        { onStoryClick(story.id) }
    }

    val storyContentClick: () -> Unit = remember {
        { story.url?.let { onStoryContentClick(it) } }
    }

    Row {

        // If there is a story image, we must share the space
        val rightPadding: Dp =
            if (story.url != null) 10.dp
            else 16.dp

        Box(
            modifier = Modifier
                .graphicsLayer(shape = RoundedCornerShape(4.dp), clip = true)
                .weight(1f)
                .heightIn(min = 110.dp)
                .clickable(onClick = storyClick)
                .padding(
                    start = 16.dp,
                    end = rightPadding,
                    top = 16.dp,
                    bottom = 12.dp
                )
        ) {

            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = story.title,
                    style = MaterialTheme.typography.titleMedium
                )
                StoryFeedItemDescription(
                    story = story,
                    webPreviewState = webPreviewState
                )
            }
        }

        if (storyUrl != null && webPreviewState != null) {
            StoryImage(
                webPreviewState = webPreviewState,
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        end = 16.dp,
                        top = (16 + 1).dp,
                        bottom = 14.dp
                    )
                    .size(80.dp),
                onClick = storyContentClick
            )
        }
    }
}

@Composable
fun StoryFeedItemDescription(
    story: Story,
    webPreviewState: WebPreviewState?
) {

    val storyUrl: StoryUrl? = story.url

    Box {
        // Only show shimmer if we are loading preview for an url
        if (storyUrl != null && webPreviewState is WebPreviewState.Loading) {
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Row(modifier = Modifier.padding(top = 1.dp)) {
                    Text(
                        text = storyUrl.urlSiteName(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            // Shorter spacer
                            .padding(end = 6.dp)
                            .align(Alignment.CenterVertically)
                    )
                    ShimmerView(
                        Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                ShimmerView(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                        .height(16.dp)
                )
            }
        } else {
            val webPreview =
                (webPreviewState as? WebPreviewState.Success)?.webPreview

            val siteName: String? =
                webPreview?.siteName ?: story.url?.urlSiteName()

            val description: String? =
                story.text
                    .takeIf { !it.isNullOrEmpty() }
                    ?.let {
                        Html.fromHtml(it).toString()
                            .replace("\n\n", " ")
                    }
                    ?: webPreview?.description

            val subtitle = rememberStoryDescriptionAnnotatedString(siteName, description)

            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = textSecondaryAlpha)
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


// Concatenates a subtitle string from the site name and description
@Composable
internal fun rememberStoryDescriptionAnnotatedString(
    siteName: String?,
    description: String?,
    emphTextStyle: TextStyle = MaterialTheme.typography.labelLarge,
    emphTextColor: Color = MaterialTheme.colorScheme.onPrimary
): AnnotatedString {

    val annotatedString = remember(siteName, description) {

        val stringBuilder = AnnotatedString.Builder()

        if (siteName != null) {
            stringBuilder.pushStyle(
                SpanStyle(
                    fontWeight = emphTextStyle.fontWeight,
                    fontFamily = emphTextStyle.fontFamily,
                    fontStyle = emphTextStyle.fontStyle,
                    fontSize = emphTextStyle.fontSize,
                    color = emphTextColor
                )
            )

            stringBuilder.append(siteName)
            if (description != null) stringBuilder.append(" - ")

            stringBuilder.pop()
        }

        if (description != null) {
            stringBuilder.append(description)
        }

        stringBuilder.toAnnotatedString()
    }

    return annotatedString
}

@Preview
@Composable
private fun StoryFeedItemPostPreview() {
    AppTheme {
        StoryFeedItem(
            decoratedStory = DecoratedStory(
                story = Story.placeholderPost,
                webPreviewState = WebPreviewState.Loading
            ),
            onStoryClick = {},
            onStoryContentClick = {}
        )
    }
}

@Preview
@Composable
private fun StoryFeedItemLinkPreview() {
    AppTheme {
        StoryFeedItem(
            decoratedStory = DecoratedStory(
                story = Story.placeholderLink,
                webPreviewState = WebPreviewState.Success(WebPreviewData.placeholder)
            ),
            onStoryClick = {},
            onStoryContentClick = {}
        )
    }
}

@Preview
@Composable
private fun StoryFeedItemLoadingPreview() {
    AppTheme {
        StoryFeedItem(
            decoratedStory = DecoratedStory(
                story = Story.placeholderLink,
                webPreviewState = WebPreviewState.Loading
            ),
            onStoryClick = {},
            onStoryContentClick = {}
        )
    }
}
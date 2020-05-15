package co.adrianblan.storyfeed.ui

import android.text.Html
import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.material.ripple.ripple
import androidx.ui.res.colorResource
import androidx.ui.text.AnnotatedString
import androidx.ui.text.SpanStyle
import androidx.ui.text.length
import androidx.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import co.adrianblan.common.urlSiteName
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import co.adrianblan.hackernews.api.StoryUrl
import co.adrianblan.hackernews.api.dummy
import co.adrianblan.storyfeed.DecoratedStory
import co.adrianblan.storyfeed.R
import co.adrianblan.storyfeed.WebPreviewState
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.ShimmerView
import co.adrianblan.ui.UrlImage
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

    val storyClick: () -> Unit =
        { onStoryClick(story.id) }

    val storyContentClick: () -> Unit =
        { story.url?.let { onStoryContentClick(it) } }

    Row {
        Surface(
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .preferredHeightIn(minHeight = 110.dp)
        ) {
            Clickable(onClick = storyClick, modifier = Modifier.ripple(bounded = true)) {

                // If there is a story image, we must share the space
                val rightPadding: Dp =
                    if (story.url != null) 10.dp
                    else 16.dp

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    paddingStart = 16.dp,
                    paddingEnd = rightPadding,
                    paddingTop = 16.dp,
                    paddingBottom = 12.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.subtitle1
                        )
                        StoryFeedItemDescription(
                            story = story,
                            webPreviewState = webPreviewState
                        )
                    }
                }
            }
        }

        if (storyUrl != null && webPreviewState != null) {
            StoryFeedItemImage(
                storyUrl = storyUrl,
                webPreviewState = webPreviewState,
                onClick = storyContentClick
            )
        }
    }
}

// Concatenates a subtitle string from the site name and description
@Composable
internal fun buildSubtitleString(
    siteName: String?,
    description: String?
): AnnotatedString {
    val stringBuilder = AnnotatedString.Builder()

    if (siteName != null) {
        val emphStyle = MaterialTheme.typography.subtitle2

        stringBuilder.pushStyle(
            SpanStyle(
                fontWeight = emphStyle.fontWeight,
                fontFamily = emphStyle.fontFamily,
                fontStyle = emphStyle.fontStyle,
                color = MaterialTheme.colors.onPrimary
            )
        )

        stringBuilder.append(siteName)
        if (description != null) stringBuilder.append(" - ")

        stringBuilder.pop()
    }

    if (description != null) {
        stringBuilder.append(description)
    }

    return stringBuilder.toAnnotatedString()
}

@Composable
fun StoryFeedItemDescription(
    story: Story,
    webPreviewState: WebPreviewState?
) {

    val storyUrl: StoryUrl? = story.url

    // Only show shimmer if we are loading preview for an url
    if (storyUrl != null && webPreviewState is WebPreviewState.Loading) {
        Spacer(modifier = Modifier.preferredHeight(8.dp))

        Surface(shape = RoundedCornerShape(2.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(16.dp)
            ) {
                ShimmerView()
            }
        }
        Spacer(modifier = Modifier.preferredHeight(6.dp))
        Surface(shape = RoundedCornerShape(2.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .preferredHeight(16.dp)
            ) {
                ShimmerView()
            }
        }
    } else {

        val webPreview =
            (webPreviewState as? WebPreviewState.Success)?.webPreview

        val siteName: String? =
            webPreview?.siteName ?: story.url?.url?.urlSiteName()

        val description: String? =
            story.text
                .takeIf { !it.isNullOrEmpty() }
                ?.let {
                    Html.fromHtml(it).toString()
                        .replace("\n\n", " ")
                }
                ?: webPreview?.description

        val subtitle =
            buildSubtitleString(siteName, description)

        if (subtitle.length > 0) {
            Spacer(modifier = Modifier.preferredHeight(3.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.body2.copy(
                    color = MaterialTheme.colors.onPrimary.copy(alpha = textSecondaryAlpha)
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StoryFeedItemImage(
    storyUrl: StoryUrl,
    webPreviewState: WebPreviewState?,
    onClick: () -> Unit
) {

    Surface(shape = RoundedCornerShape(3.dp), modifier = Modifier.fillMaxHeight()) {
        Clickable(onClick = onClick, modifier = Modifier.ripple(bounded = true)) {
            Box(
                paddingStart = 10.dp,
                paddingEnd = 16.dp,
                paddingTop = (16 + 1).dp,
                paddingBottom = 14.dp
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.preferredSize(80.dp)
                ) {
                    Stack {
                        Surface(
                            color = colorResource(R.color.contentMuted),
                            modifier = Modifier.fillMaxSize()
                        ) {}

                        when (webPreviewState) {
                            is WebPreviewState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    ShimmerView()
                                }
                            }
                            is WebPreviewState.Success -> {
                                val webPreview = webPreviewState.webPreview

                                val imageUrl =
                                    webPreview.imageUrl ?: webPreview.iconUrl
                                    ?: webPreview.favIconUrl

                                UrlImage(imageUrl)
                            }
                            is WebPreviewState.Error -> {
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun StoryFeedItemPreview() {
    AppTheme {
        StoryFeedItem(
            decoratedStory = DecoratedStory(
                Story.dummy,
                webPreviewState = WebPreviewState.Loading
            ),
            onStoryClick = {},
            onStoryContentClick = {}
        )
    }
}
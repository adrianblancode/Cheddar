package co.adrianblan.storyfeed.ui

import android.text.Html
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.length
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import co.adrianblan.common.urlSiteName
import co.adrianblan.core.DecoratedStory
import co.adrianblan.core.WebPreviewState
import co.adrianblan.core.ui.LinkIcon
import co.adrianblan.domain.Story
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.domain.placeholder
import co.adrianblan.storyfeed.R
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
                .preferredHeightIn(min = 110.dp)
        ) {
            Box(
                modifier = Modifier.clickable(onClick = storyClick)
            ) {

                // If there is a story image, we must share the space
                val rightPadding: Dp =
                    if (story.url != null) 10.dp
                    else 16.dp

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = rightPadding,
                            top = 16.dp,
                            bottom = 12.dp
                        )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
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

    Surface(
        shape = RoundedCornerShape(3.dp),
        modifier = Modifier.fillMaxHeight()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(
                start = 10.dp,
                end = 16.dp,
                top = (16 + 1).dp,
                bottom = 14.dp
            )
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.preferredSize(80.dp)
            ) {
                Box {
                    Surface(
                        color = colorResource(R.color.contentMuted),
                        modifier = Modifier.fillMaxSize()
                    ) {}

                    when (webPreviewState) {
                        is WebPreviewState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ShimmerView()
                                LinkIcon()
                            }
                        }
                        is WebPreviewState.Success -> {
                            val webPreview = webPreviewState.webPreview

                            val imageUrl =
                                webPreview.imageUrl ?: webPreview.iconUrl
                                ?: webPreview.favIconUrl

                            UrlImage(imageUrl) {
                                LinkIcon()
                            }
                        }
                        is WebPreviewState.Error -> {
                            LinkIcon()
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
                Story.placeholder,
                webPreviewState = WebPreviewState.Loading
            ),
            onStoryClick = {},
            onStoryContentClick = {}
        )
    }
}
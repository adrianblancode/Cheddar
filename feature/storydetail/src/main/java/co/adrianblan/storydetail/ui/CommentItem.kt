package co.adrianblan.storydetail.ui

import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.net.toUri
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R
import co.adrianblan.ui.AppTheme
import co.adrianblan.ui.utils.lerp


@Composable
fun CommentItem(
    comment: FlatComment,
    storyAuthor: String?,
    onCommentUrlClick: (Uri) -> Unit
) {
    CommentItem(
        text = comment.comment.text,
        by = comment.comment.by,
        storyAuthor = storyAuthor,
        onCommentUrlClick = onCommentUrlClick
    )
}

@Composable
fun CommentItem(
    text: String?,
    by: String?,
    storyAuthor: String?,
    onCommentUrlClick: (Uri) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = 8.dp
            )
    ) {

        Column(verticalArrangement = Arrangement.Top) {

            // Comments can be deleted
            val isDeleted = by == null

            if (isDeleted) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.comment_deleted_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(Modifier.height(8.dp))
            } else {

                val isStoryAuthor = by == storyAuthor

                val authorColor: Color =
                    if (isStoryAuthor) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onBackground

                val authorSuffix =
                    if (isStoryAuthor) " [op]"
                    else ""

                Text(
                    text = by.orEmpty() + authorSuffix,
                    style = MaterialTheme.typography.labelLarge.copy(color = authorColor)
                )

                Spacer(Modifier.height(4.dp))

                val body: Spanned = Html.fromHtml(text.orEmpty())

                Text(
                    text = body.formatCommentText(
                        urlColor = MaterialTheme.colorScheme.secondary,
                        onClick = onCommentUrlClick
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
internal fun CollapsedCommentItem(numChildren: Int) {
    Text(
        text = pluralStringResource(id = R.plurals.comment_collapsed, numChildren + 1, numChildren + 1),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = 8.dp
            )
    )
}

@Composable
internal fun Modifier.commentDepthIndicator(depthIndex: Int): Modifier {

    val colors = MaterialTheme.colorScheme
    val depthIndicatorWidth = 10.dp

    return remember(depthIndex) {
        drawBehind {
            val parentSize = this.size

            if (parentSize.height == 0f) return@drawBehind

            val strokeWidthPx = 1.5.dp.toPx()
            var xOffset = 10.dp.toPx()
            val yPadding = 6.dp.toPx()

            // Fade out very small depth indicators between [16dp, 32dp]
            val alphaFraction = ((parentSize.height - 16.dp.toPx()) / 16.dp.toPx())
            val alpha = alphaFraction.coerceIn(0f, 1f)

            repeat(depthIndex) { i ->

                val o1 = Offset(xOffset, yPadding)
                val o2 = Offset(
                    xOffset,
                    parentSize.height - yPadding
                )

                drawLine(
                    start = o1,
                    end = o2,
                    color = colors.outline,
                    alpha = alpha,
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round
                )
                xOffset += depthIndicatorWidth.toPx()
            }
        }
            .padding(
                start = 16.dp + depthIndex * depthIndicatorWidth,
                end = 16.dp,
            )
    }
}

private data class CommentUrlInfo(
    val url: Uri,
    val startIndex: Int,
    val endIndex: Int
)

// Accepts parsed html from Html.fromHtml
private fun Spanned.commentUrlInfo(): List<CommentUrlInfo> {

    val sb = SpannableStringBuilder(this)

    val urlSpans: List<URLSpan> =
        sb.getSpans(0, this.length, URLSpan::class.java)
            .toList()

    return urlSpans.map { span ->
        CommentUrlInfo(
            url = span.url.toUri(),
            startIndex = sb.getSpanStart(span),
            endIndex = sb.getSpanEnd(span)
        )
    }
}

// Accepts parsed html from Html.fromHtml
private fun Spanned.formatCommentText(
    urlColor: Color,
    onClick: (Uri) -> Unit
): AnnotatedString {

    val asb = AnnotatedString.Builder()
    asb.append(this.toString().trimEnd())

    val urls: List<CommentUrlInfo> = this.commentUrlInfo()

    urls.forEach { url ->
        asb.addLink(
            LinkAnnotation.Url(
                url = url.url.toString(),
                styles = TextLinkStyles(SpanStyle(color = urlColor)),
                linkInteractionListener = { link ->
                    if (link is LinkAnnotation.Url) {
                        onClick(Uri.parse(link.url))
                    }
                }
            ),
            url.startIndex,
            url.endIndex
        )
    }

    return asb.reduceParagraphSpacing().toAnnotatedString()
}

// Comments have a lot of whitespace between paragraphs, let's reduce it
private fun AnnotatedString.Builder.reduceParagraphSpacing(): AnnotatedString.Builder {

    Regex.fromLiteral("\n\n")
        .findAll(this.toAnnotatedString())
        .forEach { match ->
            this.addStyle(
                SpanStyle(fontSize = 6.sp),
                match.range.first,
                match.range.last + 1
            )
        }

    return this
}

@Preview
@Composable
private fun CommentItemPreview() {
    AppTheme(true) {
        CommentItem(
            text = "<p>Test title<p>Test also title",
            by = "Test source",
            storyAuthor = "Test author",
            onCommentUrlClick = {}
        )
    }
}

@Preview
@Composable
private fun CommentItemWithDepthIndicatorPreview() {
    AppTheme(true) {
        Box(modifier = Modifier.commentDepthIndicator(depthIndex = 3)) {
            CommentItem(
                text = "<p>Test title<p>Test also title",
                by = "Test source",
                storyAuthor = "Test author",
                onCommentUrlClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun CollapsedCommentItemPreview() {
    AppTheme(true) {
        CollapsedCommentItem(numChildren = 3)
    }
}

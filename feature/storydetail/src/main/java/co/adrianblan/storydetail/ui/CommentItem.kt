package co.adrianblan.storydetail.ui

import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.core.net.toUri
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R
import co.adrianblan.ui.AppTheme


@Composable
fun CommentItem(
    comment: FlatComment,
    storyAuthor: String?,
    onCommentUrlClick: (Uri) -> Unit
) {
    CommentItem(
        text = comment.comment.text,
        by = comment.comment.by,
        depthIndex = comment.depthIndex,
        storyAuthor = storyAuthor,
        onCommentUrlClick = onCommentUrlClick
    )
}

@Composable
fun CommentItem(
    text: String?,
    by: String?,
    depthIndex: Int,
    storyAuthor: String?,
    onCommentUrlClick: (Uri) -> Unit
) {

    val depthIndicatorWidth = 10.dp

    val strokeWidthPx = with(LocalDensity.current) {
        1.dp.toPx()
    }

    val depthIndicatorColor = colorResource(id = co.adrianblan.ui.R.color.divider)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {

                val parentSize = this.size
                var xOffset = 16.dp.toPx()
                val yPadding = 6.dp.toPx()

                repeat(depthIndex) { i ->

                    val o1 = Offset(xOffset, yPadding)
                    val o2 = Offset(
                        xOffset,
                        parentSize.height - yPadding
                    )

                    drawLine(
                        start = o1,
                        end = o2,
                        color = depthIndicatorColor,
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                    xOffset += depthIndicatorWidth.toPx()
                }
            }
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
    ) {

        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(start = depthIndex * depthIndicatorWidth)
        ) {

            // Comments can be deleted
            val isDeleted = by == null

            if (isDeleted) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.comment_deleted_title),
                    style = MaterialTheme.typography.labelLarge
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

                val urlInfo: List<CommentUrlInfo> = body.commentUrlInfo()

                ClickableText(
                    text = body.formatCommentText(urlColor = MaterialTheme.colorScheme.secondary),
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    onClick = { index ->
                        urlInfo
                            .firstOrNull { url ->
                                index >= url.startIndex && index < url.endIndex
                            }
                            ?.let { url ->
                                onCommentUrlClick(url.url)
                            }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun CommentItemPreview() {
    AppTheme(false) {
        Surface {
            CommentItem(
                text = "<p>Test title<p>Test also title",
                by = "Test source",
                depthIndex = 3,
                storyAuthor = "Test author",
                onCommentUrlClick = {}
            )
        }
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
private fun Spanned.formatCommentText(urlColor: Color): AnnotatedString {

    val asb = AnnotatedString.Builder()
    asb.append(this.toString().trimEnd())

    val urls: List<CommentUrlInfo> = this.commentUrlInfo()

    urls.forEach { url ->
        asb.addStyle(
            SpanStyle(color = urlColor),
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
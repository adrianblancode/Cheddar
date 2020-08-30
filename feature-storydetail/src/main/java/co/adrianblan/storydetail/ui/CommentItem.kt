package co.adrianblan.storydetail.ui

import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import androidx.compose.foundation.Box
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.core.net.toUri
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R


@Composable
fun CommentItem(
    comment: FlatComment,
    storyAuthor: String?,
    onCommentUrlClicked: (Uri) -> Unit
) {
    CommentItem(
        text = comment.comment.text,
        by = comment.comment.by,
        depthIndex = comment.depthIndex,
        storyAuthor = storyAuthor,
        onCommentUrlClicked = onCommentUrlClicked
    )
}

@Composable
fun CommentItem(
    text: String?,
    by: String?,
    depthIndex: Int,
    storyAuthor: String?,
    onCommentUrlClicked: (Uri) -> Unit
) {

    val depthIndicatorWidth = 10.dp

    val strokeWidthPx = with(DensityAmbient.current) {
        1.dp.toPx()
    }

    val depthIndicatorColor = colorResource(id = R.color.divider)

    Box(
        paddingStart = 16.dp + depthIndex * depthIndicatorWidth,
        paddingEnd = 16.dp,
        paddingTop = 8.dp,
        paddingBottom = 8.dp,
        modifier = Modifier.fillMaxWidth()
            .drawBehind {

                val parentSize = this.size
                var depthOffset = 16.dp.toPx()

                repeat(depthIndex) {
                    val o1 = Offset(depthOffset, 0f)
                    val o2 = Offset(
                        depthOffset,
                        parentSize.height
                    )

                    drawLine(
                        start = o1,
                        end = o2,
                        color = depthIndicatorColor,
                        strokeWidth = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                    depthOffset += depthIndicatorWidth.toPx()
                }
            }
    ) {

        Column(
            verticalArrangement = Arrangement.Top
        ) {

            // Comments can be deleted
            val isDeleted = by == null

            if (isDeleted) {
                Spacer(Modifier.preferredHeight(8.dp))
                Text(
                    text = stringResource(R.string.comment_deleted_title),
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(Modifier.preferredHeight(12.dp))
            } else {

                val isStoryAuthor = by == storyAuthor

                val authorColor: Color =
                    if (isStoryAuthor) MaterialTheme.colors.secondary
                    else MaterialTheme.colors.onBackground

                val authorSuffix =
                    if (isStoryAuthor) " [op]"
                    else ""

                Text(
                    text = by.orEmpty() + authorSuffix,
                    style = MaterialTheme.typography.subtitle2.copy(color = authorColor)
                )

                Spacer(Modifier.preferredHeight(4.dp))


                val body: Spanned = Html.fromHtml(text.orEmpty())

                val urlInfo: List<CommentUrlInfo> = body.commentUrlInfo()

                ClickableText(
                    text = body.formatCommentText(urlColor = MaterialTheme.colors.secondary),
                    style = MaterialTheme.typography.body2,
                    onClick = { index ->
                        urlInfo
                            .firstOrNull { url ->
                                index >= url.startIndex && index < url.endIndex
                            }
                            ?.let { url ->
                                onCommentUrlClicked(url.url)
                            }
                    }
                )
            }
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
        .findAll(this.toString())
        .forEach { match ->
            this.addStyle(
                SpanStyle(fontSize = TextUnit.Sp(6)),
                match.range.first,
                match.range.last + 1
            )
        }

    return this
}
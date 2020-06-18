package co.adrianblan.storydetail.ui

import android.net.Uri
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.URLSpan
import android.util.Patterns
import androidx.compose.Composable
import androidx.core.net.toUri
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.drawBehind
import androidx.ui.foundation.Box
import androidx.ui.foundation.ClickableText
import androidx.ui.foundation.Text
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.StrokeCap
import androidx.ui.graphics.drawscope.Stroke
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.colorResource
import androidx.ui.res.stringResource
import androidx.ui.text.AnnotatedString
import androidx.ui.text.SpanStyle
import androidx.ui.unit.Px
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import androidx.ui.unit.times
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R
import java.util.regex.Matcher


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

    val strokeWidthPx: Px = with(DensityAmbient.current) {
        1f.dp.toPx()
    }

    val depthIndicatorColor = colorResource(id = R.color.contentMuted)

    val depthIndicatorStroke = Stroke(
        width = strokeWidthPx.value,
        cap = StrokeCap.round
    )

    Box(
        paddingStart = 16.dp + depthIndex * depthIndicatorWidth,
        paddingEnd = 16.dp,
        paddingTop = 8.dp,
        paddingBottom = 8.dp,
        modifier = Modifier.fillMaxWidth()
            .drawBehind {

                val parentSize = this.size
                var depthOffset: Px = 16.dp.toPx()

                repeat(depthIndex) {
                    val o1 = Offset(depthOffset.value, 0f)
                    val o2 = Offset(
                        depthOffset.value,
                        parentSize.height
                    )

                    drawLine(
                        p1 = o1,
                        p2 = o2,
                        color = depthIndicatorColor,
                        stroke = depthIndicatorStroke
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
                SpanStyle(fontSize = TextUnit.Sp(8)),
                match.range.first,
                match.range.last + 1
            )
        }

    return this
}
package co.adrianblan.storydetail.ui

import android.text.Html
import androidx.compose.Composable
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.drawBehind
import androidx.ui.foundation.Box
import androidx.ui.foundation.Text
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.graphics.StrokeCap
import androidx.ui.layout.*
import androidx.ui.material.MaterialTheme
import androidx.ui.res.colorResource
import androidx.ui.res.stringResource
import androidx.ui.unit.Px
import androidx.ui.unit.dp
import androidx.ui.unit.times
import co.adrianblan.storydetail.FlatComment
import co.adrianblan.storydetail.R

@Composable
fun CommentItem(comment: FlatComment, storyAuthor: String?) {
    CommentItem(
        text = comment.comment.text,
        by = comment.comment.by,
        depthIndex = comment.depthIndex,
        storyAuthor = storyAuthor
    )
}

@Composable
fun CommentItem(
    text: String?,
    by: String?,
    depthIndex: Int,
    storyAuthor: String?
) {

    val depthIndicatorWidth = 12.dp

    val strokeWidthPx: Px = with(DensityAmbient.current) {
        1.2f.dp.toPx()
    }

    val depthIndicatorPaint = Paint().apply {
        color = colorResource(id = R.color.contentMuted)
        strokeWidth = strokeWidthPx.value
        strokeCap = StrokeCap.round
    }

    Box(
        paddingStart = 16.dp + depthIndex * depthIndicatorWidth,
        paddingEnd = 16.dp,
        paddingTop = 8.dp,
        paddingBottom = 6.dp,
        modifier = Modifier.fillMaxWidth() +
                Modifier.drawBehind {

                    val parentSize = this.size
                    var depthOffset: Px = 16.dp.toPx()

                    repeat(depthIndex) {
                        val o1 = Offset(depthOffset.value, 4.dp.toPx().value)
                        val o2 = Offset(
                            depthOffset.value,
                            parentSize.height.value - 4.dp.toPx().value
                        )

                        drawLine(o1, o2, depthIndicatorPaint)
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
                Spacer(Modifier.preferredHeight(12.dp))
                Text(
                    text = stringResource(R.string.comment_deleted_title),
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(Modifier.preferredHeight(16.dp))
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
                Spacer(Modifier.preferredHeight(2.dp))
                Text(
                    text = Html.fromHtml(text.orEmpty()).toString().trimEnd(),
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

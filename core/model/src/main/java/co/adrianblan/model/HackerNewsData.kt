package co.adrianblan.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class StoryId(val id: Long): Parcelable

data class StoryUrl(val url: String)

data class Story(
    val id: StoryId,
    val title: String,
    val text: String? = null,
    val by: String,
    val time: Instant,
    val url: StoryUrl? = null,
    val kids: List<CommentId> = emptyList(),
    val score: Int? = null,
    val descendants: Int? = null
) {
    companion object
}

data class CommentId(val id: Long)

data class Comment(
    val id: CommentId,
    // Comments can be deleted
    val text: String? = null,
    val by: String? = null,
    val time: Instant,
    val kids: List<CommentId> = emptyList()
) {
    companion object
}
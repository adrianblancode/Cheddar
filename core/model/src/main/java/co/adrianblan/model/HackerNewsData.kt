package co.adrianblan.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.Instant

@Immutable
data class StoryId(val id: Long)

@Immutable
data class StoryUrl(val url: String)

@Immutable
data class Story(
    val id: StoryId,
    val title: String,
    val text: String? = null,
    val by: String,
    val time: Instant,
    val url: StoryUrl? = null,
    val kids: ImmutableList<CommentId> = persistentListOf(),
    val score: Int? = null,
    val descendants: Int? = null
) {
    companion object
}

@Immutable
data class CommentId(val id: Long)

@Immutable
data class Comment(
    val id: CommentId,
    // Comments can be deleted
    val text: String? = null,
    val by: String? = null,
    val time: Instant,
    val kids: ImmutableList<CommentId> = persistentListOf()
) {
    companion object
}
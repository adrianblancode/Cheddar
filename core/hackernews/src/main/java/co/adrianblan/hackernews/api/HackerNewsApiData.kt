@file:UseSerializers(InstantSerializer::class)

package co.adrianblan.hackernews.api

import co.adrianblan.model.CommentId
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryUrl
import kotlinx.serialization.*
import java.time.Instant

@Serializable
data class ApiStory(
    val id: Long,
    val title: String,
    val text: String? = null,
    val by: String,
    val time: Instant,
    val url: String? = null,
    val kids: List<Long> = emptyList(),
    val score: Int? = null,
    val descendants: Int? = null
)

fun ApiStory.toDomain() =
    co.adrianblan.model.Story(
        id = StoryId(id),
        title = title,
        text = text,
        by = by,
        time = time,
        url = url?.takeIf { it.isNotEmpty() }?.let { StoryUrl((url)) },
        kids = kids.map { CommentId(it) }
    )

@Serializable(with = NullableApiCommentSerializer::class)
data class ApiComment(
    val id: Long,
    // Comments can be deleted
    val text: String? = null,
    val by: String? = null,
    val time: Instant,
    val kids: List<Long> = emptyList()
)

fun ApiComment.toDomain() =
    co.adrianblan.model.Comment(
        id = CommentId(id),
        text = text,
        by = by,
        time = time,
        kids = kids.map { CommentId(it) }
    )

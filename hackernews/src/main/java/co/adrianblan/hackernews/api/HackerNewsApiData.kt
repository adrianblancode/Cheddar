@file:UseSerializers(InstantSerializer::class)
package co.adrianblan.hackernews.api

import kotlinx.serialization.*
import java.time.Instant


// TODO map to domain classes

@Serializable(with = StoryIdSerializer::class)
data class StoryId(val id: Long)

@Serializable(with = StoryUrlSerializer::class)
data class StoryUrl(val url: String)

@Serializable
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
)

@Serializable(with = CommentIdSerializer::class)
data class CommentId(val id: Long)

@Serializable
data class Comment(
    val id: CommentId,
    // Comments can be deleted
    val text: String? = null,
    val by: String? = null,
    val time: Instant,
    val kids: List<CommentId> = emptyList()
)
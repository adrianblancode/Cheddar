package co.adrianblan.hackernews.api

import kotlinx.serialization.*

@Serializable(with = StoryIdSerializer::class)
data class StoryId(val id: Long)

@Serializable
data class Story(
    val id: StoryId,
    val title: String,
    val text: String? = null,
    val by: String,
    val time: Long,
    val url: String? = null,
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
    val time: Long,
    val kids: List<CommentId> = emptyList()
)
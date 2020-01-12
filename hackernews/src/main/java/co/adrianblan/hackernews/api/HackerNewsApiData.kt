package co.adrianblan.hackernews.api

import kotlinx.serialization.*

@Serializable
data class StoryId(val id: Long)

@Serializable
data class Story(
    val id: StoryId,
    val title: String,
    val text: String,
    val by: String,
    val time: Long,
    val url: String? = null,
    val kids: List<CommentId> = emptyList(),
    val score: Int? = null,
    val descendants: Int? = null
)

@Serializable
data class CommentId(val id: Long)

@Serializable
data class Comment(
    val id: CommentId,
    val title: String,
    val text: String,
    val by: String,
    val time: Long,
    val kids: List<CommentId> = emptyList()
)
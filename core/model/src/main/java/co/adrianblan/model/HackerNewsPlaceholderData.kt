package co.adrianblan.model

import java.time.Instant

private val placeholderInstant = Instant.now()

val Story.Companion.placeholder get() =
    Story(
        id = StoryId(1),
        title = "Test story title",
        text = "Test story text",
        by = "Test story author",
        time = placeholderInstant,
        url = null
    )

val Comment.Companion.placeholder get() =
    Comment(
        id = CommentId(1),
        text = "Test comment text",
        by = "Test comment author",
        time = placeholderInstant
    )
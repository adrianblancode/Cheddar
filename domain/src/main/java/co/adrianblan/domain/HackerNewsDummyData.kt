package co.adrianblan.domain

import java.time.Instant

private val dummyInstant = Instant.now()

val Story.Companion.dummy get() =
    Story(
        id = StoryId(1),
        title = "Test story title",
        text = "Test story text",
        by = "Test story author",
        time = dummyInstant,
        url = null
    )

val Comment.Companion.dummy get() =
    Comment(
        id = CommentId(1),
        text = "Test comment text",
        by = "Test comment author",
        time = dummyInstant
    )
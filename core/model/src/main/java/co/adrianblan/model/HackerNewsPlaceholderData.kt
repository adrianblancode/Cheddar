package co.adrianblan.model

import java.time.Instant

private val placeholderInstant = Instant.now()

val Story.Companion.placeholderLink get() =
    Story(
        id = StoryId(1),
        title = "Test story title",
        text = null,
        by = "Test story author",
        time = placeholderInstant,
        url = StoryUrl("www.example.com")
    )

val Story.Companion.placeholderPost get() =
    Story(
        id = StoryId(1),
        title = "Test story title",
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        by = "Test story author",
        time = placeholderInstant,
        url = null
    )

val Comment.Companion.placeholder get() =
    Comment(
        id = CommentId(1),
        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliquat.",
        by = "Test comment author",
        time = placeholderInstant
    )
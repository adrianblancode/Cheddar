package co.adrianblan.hackernews.api

val Story.Companion.dummy get() =
    Story(
        id = StoryId(1),
        title = "Test story title",
        text = "Test story text",
        by = "Test story author",
        time = 0L
    )

val Comment.Companion.dummy get() =
    Comment(
        id = CommentId(1),
        title = "Test comment title",
        text = "Test comment text",
        by = "Test comment author",
        time = 0L
    )
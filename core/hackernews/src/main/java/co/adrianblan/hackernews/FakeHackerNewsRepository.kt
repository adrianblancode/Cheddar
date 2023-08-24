package co.adrianblan.hackernews

import co.adrianblan.model.Comment
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.model.placeholder
import co.adrianblan.model.placeholderLink
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FakeHackerNewsRepository(
    private val story: Story = Story.placeholderLink,
    private val responseDelay: Duration = 1.seconds
) : HackerNewsRepository {

    override suspend fun fetchStory(storyId: StoryId): Story =
        coroutineScope {
            delay(responseDelay)
            story.copy(id = storyId)
        }

    override suspend fun fetchStoryIds(storyType: StoryType): List<StoryId> =
        coroutineScope {
            delay(responseDelay)
            List(100) {
                StoryId(it.toLong())
            }
        }

    override suspend fun fetchComment(commentId: CommentId): Comment =
        coroutineScope {
            delay(responseDelay)
            Comment.placeholder.copy(id = commentId)
        }
}
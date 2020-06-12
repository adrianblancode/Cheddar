package co.adrianblan.hackernews

import co.adrianblan.domain.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class TestHackerNewsRepository(
    private val responseDelay: Long = 1000L
) : HackerNewsRepository {

    override suspend fun fetchStory(storyId: StoryId): Story =
        coroutineScope {
            delay(responseDelay)
            Story.dummy.copy(id = storyId)
        }

    override suspend fun fetchStories(storyType: StoryType): List<StoryId> =
        coroutineScope {
            delay(responseDelay)
            List(100) {
                StoryId(it.toLong())
            }
        }

    override suspend fun fetchComment(commentId: CommentId): Comment =
        coroutineScope {
            delay(responseDelay)
            Comment.dummy.copy(id = commentId)
        }
}
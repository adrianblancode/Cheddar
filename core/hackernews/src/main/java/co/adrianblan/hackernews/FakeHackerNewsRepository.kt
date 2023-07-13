package co.adrianblan.hackernews

import co.adrianblan.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class FakeHackerNewsRepository(
    private val responseDelay: Long = 1000L
) : HackerNewsRepository {

    override suspend fun fetchStory(storyId: StoryId): Story =
        coroutineScope {
            delay(responseDelay)
            Story.placeholder.copy(id = storyId)
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
            Comment.placeholder.copy(id = commentId)
        }
}
package co.adrianblan.hackernews

import co.adrianblan.model.Comment
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.model.placeholder
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

    override fun cachedStoryIds(storyType: StoryType): List<StoryId>? =
        List(100) {
            StoryId(it.toLong())
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
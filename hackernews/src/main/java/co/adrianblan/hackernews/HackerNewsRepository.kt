package co.adrianblan.hackernews

import android.util.LruCache
import co.adrianblan.common.WeakCache
import co.adrianblan.hackernews.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HackerNewsRepository
@Inject constructor(
    @HackerNewsInternal private val hackerNewsApi: HackerNewsApi
) {
    private val storyCache = WeakCache<StoryId, Story>()

    suspend fun fetchStory(storyId: StoryId): Story =
        storyCache.get(storyId)
            ?: hackerNewsApi.fetchStory(storyId.id)
                .also { story ->
                    storyCache.put(storyId, story)
                }

    suspend fun fetchTopStories(): List<StoryId> =
        hackerNewsApi.fetchTopStories()

    suspend fun fetchComment(commentId: CommentId): Comment =
        hackerNewsApi.fetchComment(commentId.id)
}
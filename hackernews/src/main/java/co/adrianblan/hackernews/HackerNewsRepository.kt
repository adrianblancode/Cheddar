package co.adrianblan.hackernews

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

    suspend fun fetchStories(storyType: StoryType): List<StoryId> =
        when (storyType) {
            StoryType.TOP -> hackerNewsApi.fetchTopStories()
            StoryType.BEST -> hackerNewsApi.fetchBestStories()
            StoryType.NEW -> hackerNewsApi.fetchNewStories()
            StoryType.ASK -> hackerNewsApi.fetchAskStories()
            StoryType.SHOW -> hackerNewsApi.fetchShowStories()
            StoryType.JOB -> hackerNewsApi.fetchJobStories()
        }

    suspend fun fetchComment(commentId: CommentId): Comment =
        hackerNewsApi.fetchComment(commentId.id)
}
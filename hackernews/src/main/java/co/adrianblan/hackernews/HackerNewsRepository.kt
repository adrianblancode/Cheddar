package co.adrianblan.hackernews

import co.adrianblan.common.WeakCache
import co.adrianblan.hackernews.api.*
import co.adrianblan.network.mapNullResponseToEmptyList
import co.adrianblan.network.throwIfEmptyResponse
import co.adrianblan.network.unwrapApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HackerNewsRepository
@Inject constructor(
    private val hackerNewsApiService: HackerNewsApiService
) {
    private val storyCache = WeakCache<StoryId, Story>()

    suspend fun fetchStory(storyId: StoryId): Story =
        storyCache.get(storyId)
            ?: hackerNewsApiService.fetchStory(storyId)
                .throwIfEmptyResponse()
                .unwrapApiResponse()
                .also { story ->
                    storyCache.put(storyId, story)
                }

    suspend fun fetchStories(storyType: StoryType): List<StoryId> =
        hackerNewsApiService.fetchStories(storyType)
            .mapNullResponseToEmptyList()
            .unwrapApiResponse()

    suspend fun fetchComment(commentId: CommentId): Comment =
        hackerNewsApiService.fetchComment(commentId)
            .throwIfEmptyResponse()
            .unwrapApiResponse()
}
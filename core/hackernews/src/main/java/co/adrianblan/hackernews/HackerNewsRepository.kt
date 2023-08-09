package co.adrianblan.hackernews

import co.adrianblan.common.AsyncResource
import co.adrianblan.common.WeakCache
import co.adrianblan.model.*
import co.adrianblan.hackernews.api.HackerNewsApiService
import co.adrianblan.hackernews.api.toDomain
import co.adrianblan.network.mapNullResponseToEmptyList
import co.adrianblan.network.throwIfEmptyResponse
import co.adrianblan.network.unwrapApiResponse
import javax.inject.Inject
import javax.inject.Singleton

interface HackerNewsRepository {
    suspend fun fetchStory(storyId: StoryId): Story
    fun storyIdsResource(storyType: StoryType): AsyncResource<List<StoryId>>
    suspend fun fetchStoryIds(storyType: StoryType): List<StoryId>
    suspend fun fetchComment(commentId: CommentId): Comment?
}

@Singleton
class HackerNewsRepositoryImpl
@Inject constructor(
    private val hackerNewsApiService: HackerNewsApiService
) : HackerNewsRepository {
    private val storyIdsCache = WeakCache<StoryType, List<StoryId>>()
    private val storyCache = WeakCache<StoryId, Story>()
    private val commentCache = WeakCache<CommentId, Comment>()

    override suspend fun fetchStory(storyId: StoryId): Story =
        storyCache.get(storyId)
            ?: hackerNewsApiService.fetchStory(storyId)
                .throwIfEmptyResponse()
                .unwrapApiResponse()
                .toDomain()
                .also { story ->
                    storyCache.put(storyId, story)
                }

    override fun storyIdsResource(storyType: StoryType): AsyncResource<List<StoryId>> =
        AsyncResource(storyIdsCache.get(storyType)) {
            fetchStoryIds(storyType)
        }

    override suspend fun fetchStoryIds(storyType: StoryType): List<StoryId> =
        hackerNewsApiService.fetchStories(storyType)
            .mapNullResponseToEmptyList()
            .unwrapApiResponse()
            .map { StoryId(it) }
            .also { storyIds ->
                storyIdsCache.put(storyType, storyIds)
            }

    override suspend fun fetchComment(commentId: CommentId): Comment? =
        commentCache.get(commentId)
            ?: hackerNewsApiService.fetchComment(commentId)
                .unwrapApiResponse()
                ?.toDomain()
                ?.also { comment ->
                    commentCache.put(commentId, comment)
                }
}
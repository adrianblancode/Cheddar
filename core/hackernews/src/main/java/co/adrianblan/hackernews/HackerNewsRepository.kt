package co.adrianblan.hackernews

import android.util.LruCache
import co.adrianblan.common.WeakCache
import co.adrianblan.hackernews.api.HackerNewsApiService
import co.adrianblan.hackernews.api.toDomain
import co.adrianblan.model.Comment
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import co.adrianblan.model.StoryType
import co.adrianblan.network.mapNullResponseToEmptyList
import co.adrianblan.network.throwIfEmptyResponse
import co.adrianblan.network.unwrapApiResponse
import javax.inject.Inject
import javax.inject.Singleton

interface HackerNewsRepository {
    suspend fun fetchStory(storyId: StoryId): Story
    suspend fun fetchStoryIds(storyType: StoryType): List<StoryId>
    suspend fun fetchComment(commentId: CommentId): Comment?
}

@Singleton
class HackerNewsRepositoryImpl
@Inject constructor(
    private val hackerNewsApiService: HackerNewsApiService
) : HackerNewsRepository {
    private val storyIdsCache = LruCache<StoryType, List<StoryId>>(1)
    private val storyCache = LruCache<StoryId, Story>(200)
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

    override suspend fun fetchStoryIds(storyType: StoryType): List<StoryId> =
        storyIdsCache.get(storyType)
            ?: hackerNewsApiService.fetchStories(storyType)
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
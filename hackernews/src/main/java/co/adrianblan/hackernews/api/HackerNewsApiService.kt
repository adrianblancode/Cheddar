package co.adrianblan.hackernews.api

import co.adrianblan.domain.CommentId
import co.adrianblan.domain.StoryId
import co.adrianblan.hackernews.HackerNewsInternal
import co.adrianblan.domain.StoryType
import co.adrianblan.network.ApiResponse
import co.adrianblan.network.wrapApiResponse
import javax.inject.Inject

class HackerNewsApiService
@Inject constructor(
    @HackerNewsInternal private val hackerNewsApi: HackerNewsApi
) {
    suspend fun fetchStory(storyId: StoryId): ApiResponse<ApiStory?> =
        hackerNewsApi.fetchStory(storyId.id)
            .wrapApiResponse()

    suspend fun fetchStories(storyType: StoryType): ApiResponse<List<Long>?> =
        when (storyType) {
            StoryType.TOP -> hackerNewsApi.fetchTopStories()
            StoryType.BEST -> hackerNewsApi.fetchBestStories()
            StoryType.NEW -> hackerNewsApi.fetchNewStories()
            StoryType.ASK -> hackerNewsApi.fetchAskStories()
            StoryType.SHOW -> hackerNewsApi.fetchShowStories()
            StoryType.JOB -> hackerNewsApi.fetchJobStories()
        }.wrapApiResponse()

    suspend fun fetchComment(commentId: CommentId): ApiResponse<ApiComment?> =
        hackerNewsApi.fetchComment(commentId.id)
            .wrapApiResponse()
}
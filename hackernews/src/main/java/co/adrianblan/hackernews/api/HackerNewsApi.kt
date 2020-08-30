package co.adrianblan.hackernews.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {

    @GET("item/{storyId}.json")
    suspend fun fetchStory(@Path("storyId") storyId: Long): Response<ApiStory>

    @GET("topstories.json")
    suspend fun fetchTopStories(): Response<List<Long>>

    @GET("beststories.json")
    suspend fun fetchBestStories(): Response<List<Long>>

    @GET("newstories.json")
    suspend fun fetchNewStories(): Response<List<Long>>

    @GET("askstories.json")
    suspend fun fetchAskStories(): Response<List<Long>>

    @GET("showstories.json")
    suspend fun fetchShowStories(): Response<List<Long>>

    @GET("jobstories.json")
    suspend fun fetchJobStories(): Response<List<Long>>

    @GET("item/{commentId}.json")
    suspend fun fetchComment(@Path("commentId") commentId: Long): Response<ApiComment?>
}
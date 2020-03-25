package co.adrianblan.hackernews.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HackerNewsApi {

    @GET("item/{storyId}.json")
    suspend fun fetchStory(@Path("storyId") storyId: Long): Response<Story>

    @GET("topstories.json")
    suspend fun fetchTopStories(): Response<List<StoryId>>

    @GET("beststories.json")
    suspend fun fetchBestStories(): Response<List<StoryId>>

    @GET("newstories.json")
    suspend fun fetchNewStories(): Response<List<StoryId>>

    @GET("askstories.json")
    suspend fun fetchAskStories(): Response<List<StoryId>>

    @GET("showstories.json")
    suspend fun fetchShowStories(): Response<List<StoryId>>

    @GET("jobstories.json")
    suspend fun fetchJobStories(): Response<List<StoryId>>

    @GET("item/{commentId}.json")
    suspend fun fetchComment(@Path("commentId") commentId: Long): Response<Comment>
}
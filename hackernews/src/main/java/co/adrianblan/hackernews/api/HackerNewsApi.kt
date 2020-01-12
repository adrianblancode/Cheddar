package co.adrianblan.hackernews.api

import retrofit2.http.GET

interface HackerNewsApi {

    @GET("/topstories.json")
    suspend fun fetchTopStories(): List<StoryId>

    @GET("/beststories.json")
    suspend fun fetchBestStories(): List<StoryId>

    @GET("/newstories.json")
    suspend fun fetchNewStories(): List<StoryId>

    @GET("/askstories.json")
    suspend fun fetchAskStories(): List<StoryId>

    @GET("/showstories.json")
    suspend fun fetchShowStories(): List<StoryId>

    @GET("/jobstories.json")
    suspend fun fetchJobStories(): List<StoryId>
}
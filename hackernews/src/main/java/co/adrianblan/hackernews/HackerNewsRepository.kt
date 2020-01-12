package co.adrianblan.hackernews

import co.adrianblan.hackernews.api.HackerNewsApi
import co.adrianblan.hackernews.api.StoryId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HackerNewsRepository
@Inject constructor(
    @HackerNewsInternal private val hackerNewsApi: HackerNewsApi
) {
    suspend fun fetchTopStories(): List<StoryId> =
        hackerNewsApi.fetchTopStories()
}
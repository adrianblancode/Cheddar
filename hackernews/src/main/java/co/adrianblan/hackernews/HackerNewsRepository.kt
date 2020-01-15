package co.adrianblan.hackernews

import co.adrianblan.hackernews.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HackerNewsRepository
@Inject constructor(
    @HackerNewsInternal private val hackerNewsApi: HackerNewsApi
) {

    suspend fun fetchStory(storyId: StoryId): Story =
        hackerNewsApi.fetchStory(storyId.id)

    suspend fun fetchTopStories(): List<StoryId> =
        hackerNewsApi.fetchTopStories()

    suspend fun fetchComment(commentId: CommentId): Comment =
        hackerNewsApi.fetchComment(commentId.id)
}
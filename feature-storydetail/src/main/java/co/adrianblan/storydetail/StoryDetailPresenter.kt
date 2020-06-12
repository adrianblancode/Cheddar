package co.adrianblan.storydetail

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.toStateFlow
import co.adrianblan.domain.CommentId
import co.adrianblan.domain.Story
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.Presenter
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.webpreview.WebPreviewRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

class StoryDetailPresenter
@Inject constructor(
    private val storyId: StoryId,
    private val hackerNewsRepository: HackerNewsRepository,
    private val webPreviewRepository: WebPreviewRepository,
    override val dispatcherProvider: DispatcherProvider
) : Presenter<StoryDetailViewState> {

    override val state: StateFlow<StoryDetailViewState> =
        flow { emit(hackerNewsRepository.fetchStory(storyId)) }
            .flatMapLatest<Story, StoryDetailViewState> { story ->
                combine(
                    observeWebPreviewState(story.url),
                    observeCommentsViewState(story)
                ) { webPreviewState: WebPreviewState?,
                    storyDetailCommentsState: StoryDetailCommentsState ->

                    StoryDetailViewState.Success(
                        story = story,
                        webPreviewState = webPreviewState,
                        commentsState = storyDetailCommentsState
                    )
                }
            }
            .flowOn(dispatcherProvider.IO)
            .catch {
                Timber.e(it)
                if (it is CancellationException) throw it
                else emit(StoryDetailViewState.Error(it))
            }
            .toStateFlow(StoryDetailViewState.Loading)

    private suspend fun fetchFlattenedComments(commentIds: List<CommentId>): List<FlatComment> =
        coroutineScope {
            commentIds
                .map { commentId ->
                    async { fetchFlattenedComments(commentId, 0) }
                }
                .awaitAll()
                .flatten()
        }

    // Recursively fetches the comment tree and flattens it into a list
    private suspend fun fetchFlattenedComments(
        commentId: CommentId,
        depthIndex: Int
    ): List<FlatComment> =
        coroutineScope {

            val comment = hackerNewsRepository.fetchComment(commentId)

            val children: List<FlatComment> =
                comment.kids
                    .map { childCommentId ->
                        async { fetchFlattenedComments(childCommentId, depthIndex + 1) }
                    }
                    .awaitAll()
                    .flatten()

            listOf(FlatComment(comment = comment, depthIndex = depthIndex)) + children
        }

    private fun observeCommentsViewState(story: Story): Flow<StoryDetailCommentsState> =
        flow {

            emit(StoryDetailCommentsState.Loading)

            try {
                val flatComments: List<FlatComment> =
                    fetchFlattenedComments(story.kids)

                if (flatComments.isEmpty()) emit(StoryDetailCommentsState.Empty)
                else emit(StoryDetailCommentsState.Success(flatComments))

            } catch (t: Throwable) {
                Timber.e(t)

                if (t is CancellationException) throw t
                else emit(StoryDetailCommentsState.Error)
            }
        }

    private fun observeWebPreviewState(url: StoryUrl?): Flow<WebPreviewState?> =
        flow {

            if (url == null) {
                emit(null)
                return@flow
            }

            emit(WebPreviewState.Loading)

            try {
                val webPreview = webPreviewRepository.fetchWebPreview(url.url)
                emit(WebPreviewState.Success(webPreview))
            } catch (t: Throwable) {
                Timber.e(t)

                if (t is CancellationException) throw t
                else emit(WebPreviewState.Error(t))
            }
        }
}
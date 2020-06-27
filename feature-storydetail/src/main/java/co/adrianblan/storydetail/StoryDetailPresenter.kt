package co.adrianblan.storydetail

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.toStateFlow
import co.adrianblan.core.DecoratedStory
import co.adrianblan.core.StoryPreviewUseCase
import co.adrianblan.core.WebPreviewState
import co.adrianblan.domain.CommentId
import co.adrianblan.domain.Story
import co.adrianblan.domain.StoryId
import co.adrianblan.domain.StoryUrl
import co.adrianblan.matryoshka.presenter.Presenter
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
    private val storyPreviewUseCase: StoryPreviewUseCase,
    override val dispatcherProvider: DispatcherProvider
) : Presenter<StoryDetailViewState> {

    // TODO share story emission with shareIn
    override val state: StateFlow<StoryDetailViewState> =
        combine<DecoratedStory, StoryDetailCommentsState, StoryDetailViewState>(
            storyPreviewUseCase.observeDecoratedStory(storyId),
            flow { emit(hackerNewsRepository.fetchStory(storyId)) }
                .flatMapLatest { story ->
                    observeCommentsViewState(story)
                }
        ) { decoratedStory: DecoratedStory,
            storyDetailCommentsState: StoryDetailCommentsState ->

            StoryDetailViewState.Success(
                story = decoratedStory.story,
                webPreviewState = decoratedStory.webPreviewState,
                commentsState = storyDetailCommentsState
            )
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
}
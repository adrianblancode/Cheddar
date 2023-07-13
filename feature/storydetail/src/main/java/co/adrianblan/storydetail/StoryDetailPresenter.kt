package co.adrianblan.storydetail

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.InitialFlow
import co.adrianblan.common.withInitialValue
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.model.*
import co.adrianblan.matryoshka.presenter.Presenter
import co.adrianblan.hackernews.HackerNewsRepository
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
    override val state: InitialFlow<StoryDetailViewState> =
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
            .withInitialValue(StoryDetailViewState.Loading)

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

            val comment: Comment? = hackerNewsRepository.fetchComment(commentId)

            if (comment != null) {

                val children: List<FlatComment> =
                    comment.kids
                        .map { childCommentId ->
                            async { fetchFlattenedComments(childCommentId, depthIndex + 1) }
                        }
                        .awaitAll()
                        .flatten()

                listOf(FlatComment(comment = comment, depthIndex = depthIndex)) + children
            } else emptyList()
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
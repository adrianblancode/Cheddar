package co.adrianblan.storydetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.WhileSubscribed
import co.adrianblan.domain.DecoratedStory
import co.adrianblan.domain.StoryPreviewUseCase
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.model.Comment
import co.adrianblan.model.CommentId
import co.adrianblan.model.Story
import co.adrianblan.model.StoryId
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel
@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val hackerNewsRepository: HackerNewsRepository,
    private val storyPreviewUseCase: StoryPreviewUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val storyId: StoryId = StoryId(savedStateHandle.get<Long>("storyId")!!)

    // TODO share story emission with shareIn
    val viewState: StateFlow<StoryDetailViewState> =
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
                emit(StoryDetailViewState.Error(it))
            }
            .stateIn(viewModelScope, WhileSubscribed, StoryDetailViewState.Loading)

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
            val flatComments: List<FlatComment> = fetchFlattenedComments(story.kids)

            if (flatComments.isEmpty()) emit(StoryDetailCommentsState.Empty)
            else emit(StoryDetailCommentsState.Success(flatComments.toImmutableList()))
        }
            .onStart { emit(StoryDetailCommentsState.Loading) }
            .catch {
                Timber.e(it)
                emit(StoryDetailCommentsState.Error)
            }
}
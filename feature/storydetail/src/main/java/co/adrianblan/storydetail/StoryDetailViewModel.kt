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
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val storyDetailArgs = StoryDetailArgs(savedStateHandle)
    private val storyId: StoryId = storyDetailArgs.storyId

    private val commentsState: StateFlow<StoryDetailCommentsState> =
        flow { emit(hackerNewsRepository.fetchStory(storyId)) }
            .flatMapLatest { story ->
                observeCommentsViewState(story)
            }
            .stateIn(viewModelScope, WhileSubscribed, StoryDetailCommentsState.Loading)

    private val collapsedCommentsFlow = MutableStateFlow(listOf<CommentId>())
    private val commentParents = mutableMapOf<CommentId, CommentId?>()

    val viewState: StateFlow<StoryDetailViewState> =
        combine(
            storyPreviewUseCase.observeDecoratedStory(storyId),
            commentsState,
            collapsedCommentsFlow,
        ) { decoratedStory: DecoratedStory,
            storyDetailCommentsState: StoryDetailCommentsState,
            collapsedComments: List<CommentId> ->

            val commentsState = storyDetailCommentsState.let { state ->
                if (state is StoryDetailCommentsState.Success) {
                    val comments = state.comments.map { comment ->

                        val isCollapsed = comment.comment.id in collapsedComments

                        var parent: CommentId? = commentParents[comment.comment.id]
                        var hasCollapsedParent = false
                        while (parent != null) {
                            if (parent in collapsedComments) {
                                hasCollapsedParent = true
                                break
                            }
                            parent = commentParents[parent]
                        }

                        val collapsedState = when {
                            hasCollapsedParent -> CommentCollapsedState.PARENT_COLLAPSED
                            isCollapsed -> CommentCollapsedState.COLLAPSED
                            else -> null
                        }

                        comment.copy(collapsedState = collapsedState)
                    }
                    state.copy(comments = comments.toImmutableList())
                } else state
            }

            StoryDetailViewState.Success(
                story = decoratedStory.story,
                webPreviewState = decoratedStory.webPreviewState,
                commentsState = commentsState
            ) as StoryDetailViewState
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

                    commentParents[commentId] = null

                    async { fetchFlattenedComments(
                        commentId = commentId,
                        depthIndex = 0
                    ) }
                }
                .awaitAll()
                .flatten()
        }

    // Recursively fetches the comment tree and flattens it into a list
    private suspend fun fetchFlattenedComments(
        commentId: CommentId,
        depthIndex: Int,
    ): List<FlatComment> =
        coroutineScope {

            val comment: Comment? = hackerNewsRepository.fetchComment(commentId)

            if (comment != null) {

                val children: List<FlatComment> =
                    comment.kids
                        .map { childCommentId ->

                            commentParents[childCommentId] = commentId

                            async { fetchFlattenedComments(
                                commentId = childCommentId,
                                depthIndex + 1
                            ) }
                        }
                        .awaitAll()
                        .flatten()

                listOf(FlatComment(
                    comment = comment,
                    numChildren = children.size,
                    depthIndex = depthIndex,
                    collapsedState = null
                )) + children
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

    fun onCommentClick(id: CommentId) {
        val collapsedComments = collapsedCommentsFlow.value
        collapsedCommentsFlow.value = if (id in collapsedComments) {
            collapsedComments - id
        } else {
            collapsedComments + id
        }
    }
}
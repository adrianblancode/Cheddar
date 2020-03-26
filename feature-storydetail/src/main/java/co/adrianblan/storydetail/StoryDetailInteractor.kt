package co.adrianblan.storydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.common.StateFlow
import co.adrianblan.common.asStateFlow
import co.adrianblan.ui.node.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.CommentId
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class StoryDetailInteractor
@Inject constructor(
    @StoryDetailInternal private val storyId: StoryId,
    private val hackerNewsRepository: HackerNewsRepository,
    override val dispatcherProvider: DispatcherProvider,
    @StoryDetailInternal override val parentScope: ParentScope
) : Interactor() {

    private val _viewState = MutableLiveData<StoryDetailViewState>(StoryDetailViewState.Loading)

    val viewState: LiveData<StoryDetailViewState> = _viewState

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

    init {
        scope.launch {

            flowOf(hackerNewsRepository.fetchStory(storyId))
                .flatMapLatest<Story, StoryDetailViewState> { story ->

                    flow {

                        emit(StoryDetailCommentsState.Loading)

                        try {
                            val flatComments: List<FlatComment> =
                                fetchFlattenedComments(story.kids)

                            if (flatComments.isEmpty()) emit(StoryDetailCommentsState.Empty)
                            else emit(StoryDetailCommentsState.Success(flatComments))

                        } catch (t: Throwable) {

                            Timber.e(t)
                            emit(StoryDetailCommentsState.Error)
                        }
                    }
                        .map { commentsState ->
                            StoryDetailViewState.Success(story, commentsState)
                        }
                        .catch {
                            Timber.e(it)
                            emit(
                                StoryDetailViewState.Success(
                                    story,
                                    StoryDetailCommentsState.Error
                                )
                            )
                        }
                }
                .flowOn(dispatcherProvider.IO)
                .catch {
                    Timber.e(it)
                    emit(StoryDetailViewState.Error)
                }
                .collect {
                    _viewState.value = it
                }
        }
    }
}
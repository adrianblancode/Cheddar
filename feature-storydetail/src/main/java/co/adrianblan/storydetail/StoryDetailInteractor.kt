package co.adrianblan.storydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.ui.Interactor
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
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

    val viewState: LiveData<StoryDetailViewState> get() = _viewState

    private val _viewState by lazy {
        MutableLiveData<StoryDetailViewState>(
            StoryDetailViewState.Loading
        )
    }

    init {
        scope.launch {
            flow<StoryDetailViewState> {

                val story: Story = hackerNewsRepository.fetchStory(storyId)

                emit(
                    StoryDetailViewState.Success(
                        story,
                        listOf(
                            StoryDetailItem.CommentsLoadingItem
                        )
                    )
                )

                try {
                    val comments: List<Comment> =
                        flow {
                            story.kids
                                .forEach { commentId ->
                                    val comment = hackerNewsRepository.fetchComment(commentId)
                                    emit(comment)
                                }
                        }
                            .toList()

                    emit(
                        StoryDetailViewState.Success(
                            story,
                            comments.map { StoryDetailItem.CommentItem(it) }
                        )
                    )
                } catch (t: Throwable) {

                    Timber.e(t)

                    emit(
                        StoryDetailViewState.Success(
                            story,
                            listOf(
                                StoryDetailItem.CommentsErrorItem
                            )
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
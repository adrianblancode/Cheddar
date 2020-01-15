package co.adrianblan.storydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.ParentScope
import co.adrianblan.common.ui.CommentsViewState
import co.adrianblan.common.ui.Interactor
import co.adrianblan.common.ui.StoryDetailViewState
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Comment
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        MutableLiveData<StoryDetailViewState>(StoryDetailViewState.Loading)
    }

    init {
        scope.launch {

            val story: Story =
                try {
                    withContext(dispatcherProvider.IO) {
                        hackerNewsRepository.fetchStory(storyId)
                    }

                } catch (t: Throwable) {
                    Timber.e(t)
                    _viewState.value = StoryDetailViewState.Error
                    return@launch
                }

            _viewState.value =
                StoryDetailViewState.Success(
                    story,
                    CommentsViewState.Loading
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
                        .flowOn(dispatcherProvider.IO)
                        .toList()

                _viewState.value =
                    StoryDetailViewState.Success(
                        story,
                        CommentsViewState.Success(comments)
                    )
            } catch (t: Throwable) {

                Timber.e(t)

                _viewState.value =
                    StoryDetailViewState.Success(
                        story,
                        CommentsViewState.Error
                    )
            }
        }
    }
}
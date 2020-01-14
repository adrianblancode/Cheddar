package co.adrianblan.storydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import co.adrianblan.common.ui.Interactor
import co.adrianblan.common.ui.StoryDetailViewState
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.hackernews.api.Story
import co.adrianblan.hackernews.api.StoryId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class StoryDetailInteractor
@Inject constructor(
    private val storyId: StoryId,
    private val hackerNewsRepository: HackerNewsRepository
) : Interactor() {

    val viewState: LiveData<StoryDetailViewState> get() = _viewState

    private val _viewState by lazy {
        MutableLiveData<StoryDetailViewState>(StoryDetailViewState.Loading)
    }

    init {
        attachScope.launch {
            _viewState.value =
                try {
                    val story: Story =
                        withContext(Dispatchers.IO) {
                            hackerNewsRepository.fetchStory(storyId)
                        }

                    StoryDetailViewState.Success(story)
                } catch (t: Throwable) {
                    Timber.e(t)
                    StoryDetailViewState.Error
                }
        }
    }
}
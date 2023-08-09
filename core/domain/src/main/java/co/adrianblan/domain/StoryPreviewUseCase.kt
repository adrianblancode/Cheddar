package co.adrianblan.domain

import co.adrianblan.model.StoryId
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.model.WebPreviewState
import co.adrianblan.webpreview.WebPreviewRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

interface StoryPreviewUseCase {
    fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory>
}

class StoryPreviewUseCaseImpl
@Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository,
    private val webPreviewRepository: WebPreviewRepository
): StoryPreviewUseCase {

    // Observes a decorated story, it will first emit the story and then try to emit decorated data as well
    override fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
        flow {
            emit(hackerNewsRepository.fetchStory(storyId))
        }.flatMapLatest { story ->
            flow {
                val storyUrl = story.url
                if (storyUrl == null) {
                    emit(DecoratedStory(story, null))
                } else {
                    val resource = webPreviewRepository.webPreviewResource(storyUrl.url)
                    val cached = resource.cached

                    if (cached != null) {
                        emit(DecoratedStory(story, WebPreviewState.Success(cached)))
                    } else {
                        emit(DecoratedStory(story, WebPreviewState.Loading))
                        emit(DecoratedStory(story, WebPreviewState.Success(resource.fetch())))
                    }
                }
            }.catch { t ->
                Timber.e(t)
                emit(DecoratedStory(story, WebPreviewState.Error(t)))
            }
        }
}
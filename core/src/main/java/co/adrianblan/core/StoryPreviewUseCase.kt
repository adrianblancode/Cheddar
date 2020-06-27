package co.adrianblan.core

import co.adrianblan.common.DispatcherProvider
import co.adrianblan.domain.StoryId
import co.adrianblan.hackernews.HackerNewsRepository
import co.adrianblan.webpreview.WebPreviewRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

interface StoryPreviewUseCase {
    fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory>
}

class StoryPreviewUseCaseImpl
@Inject constructor(
    private val hackerNewsRepository: HackerNewsRepository,
    private val webPreviewRepository: WebPreviewRepository,
    private val dispatcherProvider: DispatcherProvider
): StoryPreviewUseCase {

    // Observes a decorated story, it will first emit the story and then try to emit decorated data as well
    override fun observeDecoratedStory(storyId: StoryId): Flow<DecoratedStory> =
        flow {

            val story = hackerNewsRepository.fetchStory(storyId)
            val storyUrl = story.url

            if (storyUrl == null) emit(DecoratedStory(story, null))
            else {
                emit(DecoratedStory(story, WebPreviewState.Loading))

                try {
                    val webPreview = webPreviewRepository.fetchWebPreview(storyUrl.url)
                    emit(DecoratedStory(story, WebPreviewState.Success(webPreview)))
                } catch (t: Throwable) {
                    Timber.e(t)

                    if (t is CancellationException) throw t
                    else emit(DecoratedStory(story, WebPreviewState.Error(t)))
                }
            }
        }
}
package co.adrianblan.domain

import androidx.compose.runtime.Immutable
import co.adrianblan.model.Story
import co.adrianblan.model.WebPreviewData

@Immutable
data class DecoratedStory(
    val story: Story,
    val webPreviewState: WebPreviewState?
)

@Immutable
sealed class WebPreviewState {
    data class Success(val webPreview: WebPreviewData) : WebPreviewState()
    object Loading : WebPreviewState()
    data class Error(val throwable: Throwable) : WebPreviewState()
}
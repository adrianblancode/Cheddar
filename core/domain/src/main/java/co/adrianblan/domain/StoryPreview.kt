package co.adrianblan.domain

import co.adrianblan.model.Story
import co.adrianblan.webpreview.WebPreviewData

data class DecoratedStory(
    val story: Story,
    val webPreviewState: WebPreviewState?
)

sealed class WebPreviewState {
    data class Success(val webPreview: WebPreviewData) : WebPreviewState()
    object Loading : WebPreviewState()
    data class Error(val throwable: Throwable) : WebPreviewState()
}
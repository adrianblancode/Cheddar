package co.adrianblan.domain

import androidx.compose.runtime.Immutable
import co.adrianblan.model.Story
import co.adrianblan.model.WebPreviewState

@Immutable
data class DecoratedStory(
    val story: Story,
    val webPreviewState: WebPreviewState?
)
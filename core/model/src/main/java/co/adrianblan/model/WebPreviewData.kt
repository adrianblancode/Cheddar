package co.adrianblan.model

import androidx.compose.runtime.Immutable

/** Preview data for a web url using Open Graph tags */
@Immutable
data class WebPreviewData(
    val siteName: String,
    val description: String?,
    val imageUrl: String?,
    val iconUrl: String?,
    // This is just a best guess
    val favIconUrl: String
) {
    companion object
}
val WebPreviewData.Companion.placeholder
    get() =
        WebPreviewData(
            siteName = "example.com",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            imageUrl = "www.example.com/example.png",
            iconUrl = "www.example.com/example.png",
            favIconUrl = "www.example.com/example.png"
        )


@Immutable
sealed class WebPreviewState {
    data class Success(val webPreview: WebPreviewData) : WebPreviewState()
    object Loading : WebPreviewState()
    data class Error(val throwable: Throwable) : WebPreviewState()
}
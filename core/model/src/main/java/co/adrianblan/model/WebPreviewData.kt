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
)
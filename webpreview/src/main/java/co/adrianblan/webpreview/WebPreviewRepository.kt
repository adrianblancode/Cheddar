package co.adrianblan.webpreview

import co.adrianblan.common.WeakCache
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Preview data a web url using Open Graph tags */
data class WebPreviewData(
    val siteName: String,
    val description: String?,
    val imageUrl: String?,
    val iconUrl: String?,
    // This is just a best guess
    val favIconUrl: String
)

@Singleton
class WebPreviewRepository
@Inject constructor() {

    private val cache = WeakCache<String, WebPreviewData>()

    suspend fun fetchWebPreview(url: String): WebPreviewData =
        cache.get(url)
            ?: suspendCoroutine { continuation ->

                val document = Jsoup.connect(url).get()
                val webPreview = document.toWebPreviewData(url.baseUrl())

                continuation.resume(webPreview)
            }

    private fun Document.toWebPreviewData(baseUrl: String): WebPreviewData {

        // Get Open Graph tags
        val ogTags = head()
            .select("meta[property^=og:]")

        val siteName = ogTags.getOgContentOrNull(OG_SITE_NAME)
            ?: baseUrl.getSiteName()

        val description = ogTags.getOgContentOrNull(OG_DESCRIPTION)
            ?.replace("\n\n", " ")

        val imageUrl = ogTags.getOgContentOrNull(OG_IMAGE)
            ?.takeImageUrlIfCompatible()
            ?.completePartialUrl(baseUrl)


        // Make best effort to get icon tags
        val iconTags = head()
            .select("link[rel]")

        val iconUrl =
            (iconTags.getIconContentOrNull(APPLE_ICON)?.takeImageUrlIfCompatible()
                ?: iconTags.getIconContentOrNull(SHORTCUT_ICON)?.takeImageUrlIfCompatible()
                ?: iconTags.getIconContentOrNull(ICON))?.takeImageUrlIfCompatible()
                ?.completePartialUrl(baseUrl)

        return WebPreviewData(
            siteName = siteName,
            description = description,
            imageUrl = imageUrl,
            iconUrl = iconUrl,
            favIconUrl = "$baseUrl/favicon.ico"
        )
    }

    private fun List<Element>.getOgContentOrNull(propertyName: String): String? =
        firstOrNull {
            it.attr("property") == propertyName
        }?.attr("content")
            .takeIf { it != null }

    private fun List<Element>.getIconContentOrNull(propertyName: String): String? =
        firstOrNull {
            it.attr("rel") == propertyName
        }?.attr("href")
            .takeIf { it != null }

    private fun String.takeImageUrlIfCompatible(): String? {
        val ending = this.split("?")[0]

        // We want to prioritize other image types
        val forbiddenTypes = listOf(".ico", ".svg")

        return if (forbiddenTypes.any { ending.endsWith(it) }) null
        else this
    }

    companion object {
        private const val OG_SITE_NAME = "og:site_name"
        private const val OG_DESCRIPTION = "og:description"
        private const val OG_IMAGE = "og:image"

        private const val APPLE_ICON = "apple-touch-icon"
        private const val SHORTCUT_ICON = "shortcut icon"
        private const val ICON = "icon"
    }
}
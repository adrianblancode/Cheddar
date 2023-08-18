package co.adrianblan.webpreview

import co.adrianblan.common.AsyncResource
import co.adrianblan.common.DispatcherProvider
import co.adrianblan.common.WeakCache
import co.adrianblan.common.urlSiteName
import co.adrianblan.model.WebPreviewData
import kotlinx.coroutines.runInterruptible
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLHandshakeException


@Singleton
class WebPreviewRepository
@Inject constructor(
    private val dispatcherProvider: DispatcherProvider
) {

    private val cache = WeakCache<String, WebPreviewData>()

    suspend fun webPreviewResource(url: String): AsyncResource<WebPreviewData> =
        AsyncResource(cache.get(url)) {
            fetchWebPreview(url)
        }

    private suspend fun fetchWebPreview(url: String): WebPreviewData {

        val webPreview = if (url.endsWith(".pdf")) {
            createEmptyWebPreviewData(url)
        } else {
            try {
                runInterruptible(dispatcherProvider.IO) {
                    Jsoup.connect(url).get()
                }.toWebPreviewData(url)
            } catch (e: Exception) {
                Timber.e(e, "Webpreview error for $url ")
                if (e is HttpStatusException || e is SSLHandshakeException) {
                    createEmptyWebPreviewData(url)
                } else throw e
            }
        }
        cache.put(url, webPreview)
        return webPreview
    }

    private fun createEmptyWebPreviewData(url: String) =
        WebPreviewData(
            siteName = url.urlSiteName(),
            description = null,
            imageUrl = null,
            iconUrl = null,
            favIconUrl = null
        )
}
package co.adrianblan.common

import co.adrianblan.model.StoryUrl

fun StoryUrl.urlSiteName(): String = url.urlSiteName()

fun String.urlSiteName(): String =
    baseUrl()
        .removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")

fun String.baseUrl(): String {
    val http = "http://"
    val https = "https://"

    val protocol =
        when {
            this.startsWith(http) -> http
            this.startsWith(https) -> https
            else -> null
        }

    val tail =
        this.removePrefix(http)
            .removePrefix(https)
            .substringBefore("?")
            .substringBefore("/")

    return protocol.orEmpty() + tail
}

fun String.completePartialUrl(baseUrl: String): String {

    val url = this.removePrefix(".")

    return when {
        // Adds protocol to eg //example.com/image.png
        url.startsWith("//") -> baseUrl.substringBefore("//") + url
        // Adds base url to eg /image.png
        url.startsWith("/") || url.startsWith("./") -> baseUrl + url
        else -> url
    }
}

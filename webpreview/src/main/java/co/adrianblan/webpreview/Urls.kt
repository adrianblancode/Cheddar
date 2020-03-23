package co.adrianblan.webpreview

// Only remove .com because we are lazy
internal fun String.getSiteName(): String =
    this.removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
        .substringBefore("?")
        .substringBefore("/")
        .removeSuffix(".com")

internal fun String.baseUrl(): String {
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

// Takes a possibly partial url eg "/image.png" and completes it
internal fun String.completePartialUrl(baseUrl: String): String {

    val url = this

    return if (url.startsWith("/")) baseUrl + url
    else url
}
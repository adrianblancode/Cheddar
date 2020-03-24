package co.adrianblan.common

fun String.urlSiteName(): String {

    /*
    // Drops tld and also removes remaining .co
    urlWithTld.split(".")
        .dropLast(1)
        .joinToString(".")
        .removeSuffix(".co")
     */

    return baseUrl()
        .removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
}

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

// Takes a possibly partial url eg "/image.png" and completes it
fun String.completePartialUrl(baseUrl: String): String {

    val url = this.removePrefix(".")

    return if (url.startsWith("/")) baseUrl + url
    else url
}

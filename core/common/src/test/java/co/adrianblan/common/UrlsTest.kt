package co.adrianblan.common

import org.junit.Assert.assertEquals
import org.junit.Test

class UrlsTest {

    private val longUrl = "https://www.example.com/path/path2?query=1&query2"
    private val minimalUrl = "example.com"

    @Test
    fun testLongBaseUrl() {
        val baseUrl = longUrl.baseUrl()
        assertEquals("https://www.example.com", baseUrl)
    }

    @Test
    fun testMinimalBaseUrl() {
        val baseUrl = minimalUrl.baseUrl()
        assertEquals("example.com", baseUrl)
    }

    @Test
    fun testLongUrlSiteName() {
        val baseUrl = longUrl.urlSiteName()
        assertEquals("example.com", baseUrl)
    }

    @Test
    fun testMinimalUrlSiteName() {
        val baseUrl = minimalUrl.urlSiteName()
        assertEquals("example.com", baseUrl)
    }

    @Test
    fun completeLongPartialUrl() {
        val suffix = "/icons/favicon.ico"
        val url = suffix.completePartialUrl(longUrl.baseUrl())
        assertEquals("https://www.example.com$suffix", url)
    }

    @Test
    fun completeMinimalPartialUrl() {
        val suffix = "/icons/favicon.ico"
        val url = suffix.completePartialUrl(minimalUrl.baseUrl())
        assertEquals("example.com$suffix", url)
    }
}
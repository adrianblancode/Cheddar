package co.adrianblan.storyfeed

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PagingTest {

    @Test
    fun testMergePagesOne() = runTest {
        val result: List<List<String>> =
            flowOf(1 to listOf("A"))
                .mergePages()
                .toList()

        assertEquals(listOf(listOf("A")), result)
    }

    @Test
    fun testMergePagesOverWrite() = runTest {
        val result: List<List<String>> =
            flowOf(
                1 to listOf("A"),
                1 to listOf("AA")
            )
                .mergePages()
                .toList()

        assertEquals(listOf(listOf("A"), listOf("AA")), result)
    }

    @Test
    fun testMergePagesMultiple() = runTest {
        val result: List<List<String>> =
            flowOf(
                1 to listOf("A"),
                2 to listOf("B"),
                2 to listOf("BB"),
                1 to listOf("AA"),
                2 to listOf("BBBB")
            )
                .mergePages()
                .toList()

        val expected: List<List<String>> = listOf(
            listOf("A"),
            listOf("A, B"),
            listOf("A", "BB"),
            listOf("AA", "BB"),
            listOf("AA", "BBBB")
        )

        assertEquals(expected.size, result.size)

        // Different containers, same contents
        assertEquals(expected.toString(), result.toString())
    }
}